package df.open.restypass.cb;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import df.open.restypass.command.RestyCommand;
import df.open.restypass.enums.CircuitBreakerStatus;
import df.open.restypass.enums.RestyCommandStatus;
import df.open.restypass.exception.RequestException;
import df.open.restypass.lb.server.ServerInstance;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 默认断路器
 * Created by darrenfu on 17-7-23.
 */
@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
@ToString(exclude = {"commandQueue", "halfOpenLock"})
@Slf4j
public class DefaultCircuitBreaker implements CircuitBreaker {

    /**
     * key:[RestyCommand#path, ServerInstance#instanceId, metricsKey]
     */
    private static Table<String, String, String> keyTable = HashBasedTable.create();

    /**
     * 短路条件：失败请求占总请求的比例
     */
    private static Integer breakFailPercentage = 50;

    /**
     * 短路条件：失败请求最低数量
     */
    private static Integer breakFailCount = 10;

    /**
     * 断路器半开的时间间隔，Half_Open时，断路器会放行一个请求，如果成功->Open，如果失败->Break
     */
    private static Long halfOpenMilliseconds = 1000 * 10L;

    /**
     * Event key的前缀
     */
    private static final String KEY_PREFIX = "breaker-";

    /**
     * 注册事件使用Key
     */
    private String eventKey;

    /**
     * 半开状态使用的锁，保证只有一个请求通过
     */
    private ReentrantLock halfOpenLock;

    /**
     * 统计结果缓冲区 metricsKey->Deque
     */
    private ConcurrentHashMap<String, Metrics> segmentMap;

    /**
     * 记录短路状态 metricsKey->BreakerStatus
     */
    private ConcurrentHashMap<String, CircuitBreakerStatus> statusMap;

    /**
     * Command 阻塞队列
     */
    private LinkedBlockingQueue<RestyCommand> commandQueue;

    /**
     * 损坏的server的InstanceId列表
     */
    private Set<String> brokenServerSet;

    /**
     * 断路器是否启动
     */
    private AtomicBoolean started;

    /**
     * Init Method
     */
    public DefaultCircuitBreaker() {
        this.started = new AtomicBoolean(false);
    }

    @Override
    public String getEventKey() {
        return this.eventKey;
    }


    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            this.eventKey = KEY_PREFIX + UUID.randomUUID().toString().replace("-", "").toLowerCase();
            this.segmentMap = new ConcurrentHashMap<>();
            this.statusMap = new ConcurrentHashMap<>();
            this.brokenServerSet = new CopyOnWriteArraySet<>();
            this.commandQueue = new LinkedBlockingQueue<>();
            this.halfOpenLock = new ReentrantLock();
            this.registerEvent();
            this.startTask();
        }
    }

    @Override
    public void end() {
        started.compareAndSet(true, false);
    }

    @Override
    public boolean shouldPass(RestyCommand restyCommand, ServerInstance serverInstance) {
        // 未启动
        if (!started.get()) {
            return true;
        }

        // 强制短路
        if (restyCommand.getRestyCommandConfig().isForceBreakEnabled()) {
            return false;
        }

        // 断路器未启用
        if (!restyCommand.getRestyCommandConfig().isCircuitBreakEnabled()) {
            return true;
        }

        String metricsKey = getMetricsKey(restyCommand.getPath(), serverInstance.getInstanceId());
        // 获取统计段
        Metrics metrics = getCommandMetrics(metricsKey);
        if (metrics == null) {
            return true;
        }
        boolean shouldPass = true;

        // 获取计数器
        Metrics.SegmentMetrics segmentMetrics = metrics.getMetrics();
        // 计数器中失败数量和比例超过阀值，则触发短路判断
        if (segmentMetrics.failCount() >= breakFailCount && segmentMetrics.failPercentage() >= breakFailPercentage) {
            CircuitBreakerStatus breakerStatus = statusMap.get(metricsKey);
            shouldPass = false;
            if (breakerStatus == null || breakerStatus == CircuitBreakerStatus.OPEN) {
                // 短路
                statusMap.put(metricsKey, CircuitBreakerStatus.BREAK);
            } else if (breakerStatus == CircuitBreakerStatus.HALF_OPEN) {
                // noop
            } else {
                // 如果上次的请求距离现在超过阀值，则允许一次试探请求
                long now = System.currentTimeMillis();
                if (segmentMetrics.last() != null && (now - segmentMetrics.last() > halfOpenMilliseconds)) {
                    final ReentrantLock lock = this.halfOpenLock;
                    lock.lock();
                    try {
                        // 判断当前短路状态 确保只有一个请求通过
                        if (statusMap.get(metricsKey) == CircuitBreakerStatus.BREAK) {
                            statusMap.put(metricsKey, CircuitBreakerStatus.HALF_OPEN);
                            shouldPass = true;
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
            if (shouldPass) {
                log.debug("尝试恢复短路服务:{}:{},metrics:{}", restyCommand.getServiceName(), restyCommand.getPath(), metrics);
            } else {
                log.debug("熔断服务:{}:{},metrics:{}", restyCommand.getServiceName(), restyCommand.getPath(), metrics);
            }
        }
        return shouldPass;
    }

    @Override
    public Set<String> getBrokenServer() {
        return Collections.EMPTY_SET;
    }

    /**
     * 注册事件以及消费函数
     */
    private void registerEvent() {
        this.on(this.eventKey, (command) -> {
            if (command instanceof RestyCommand) {
                if (isQueueAvailable()) {
                    boolean offer = commandQueue.offer((RestyCommand) command);
                    if (!offer) {
                        throw new RuntimeException("Failed to add command into queue");
                    }
                }
            }
        });
    }

    /**
     * command阻塞队列是否可用
     *
     * @return 可用 true，不可用 false
     */
    private boolean isQueueAvailable() {
        return commandQueue != null && commandQueue.size() < 20000;
    }


    /**
     * 消费队列 统计已完成的command信息，更新 segment
     */
    private void startTask() {
        Executors.newSingleThreadExecutor().submit(() -> {
            log.info("启动RestyCommand统计线程:" + this.eventKey);
            while (true) {
                try {
                    // 阻塞
                    RestyCommand firstCommand = commandQueue.take();
                    // 取出queue中所有的数据
                    List<RestyCommand> commandList = new LinkedList<>();
                    commandList.add(firstCommand);
                    commandQueue.drainTo(commandList);

                    for (RestyCommand restyCommand : commandList) {
                        String key = getMetricsKey(restyCommand.getPath(), restyCommand.getInstanceId());
                        // 获取 计数器
                        Metrics metrics = getCommandMetrics(key);

                        boolean isSuccess = isCommandSuccessExecuted(restyCommand);
                        boolean forceUseNewMetrics = false;
                        // 如果当前处在短路或半短路状态
                        CircuitBreakerStatus breakerStatus = statusMap.get(key);
                        if ((breakerStatus == CircuitBreakerStatus.BREAK || breakerStatus == CircuitBreakerStatus.HALF_OPEN)) {
                            // 结果成功 则不再短路，打开断路器
                            if (isSuccess) {
                                // 并使用一个新的计数器
                                forceUseNewMetrics = true;
                                statusMap.put(key, CircuitBreakerStatus.OPEN);
                            } else {
                                // 否则恢复到短路状态
                                statusMap.put(key, CircuitBreakerStatus.BREAK);
                            }
                        }
                        metrics.store(isSuccess, forceUseNewMetrics);
                    }
                    log.debug("处理完成, 处理个数:{}，剩余:{}个", commandList.size(), commandQueue.size());

                } catch (Exception ex) {
                    log.error("断路器RestyCommand处理失败:{}", ex);
                }

            }
        });
    }


    /**
     * command是否成功执行
     *
     * @param restyCommand Resty请求
     * @return 请求是否成功
     */
    private boolean isCommandSuccessExecuted(RestyCommand restyCommand) {
        if (restyCommand.getStatus() == RestyCommandStatus.FAILED) {
            // 请求错误是客户端错误，此处认为请求已被成功执行
            return restyCommand.getFailException() instanceof RequestException;
        } else if (restyCommand.getStatus() == RestyCommandStatus.SUCCESS) {
            return true;
        }
        return false;
    }


    /**
     * 返回MetricsKey
     *
     * @param commandPath 请求 path
     * @param instanceId  server实例ID
     * @return key
     */
    private String getMetricsKey(String commandPath, String instanceId) {
        String metricsKey = keyTable.get(commandPath, instanceId);
        if (StringUtils.isEmpty(metricsKey)) {
            metricsKey = commandPath + instanceId;
            keyTable.put(commandPath, instanceId, metricsKey);
        }
        return metricsKey;
    }


    /**
     * 获取 segmentDeque
     *
     * @param metricsKey key
     * @return 计数器
     */
    private Metrics getCommandMetrics(String metricsKey) {
        Metrics metrics = segmentMap.get(metricsKey);
        if (metrics == null) {
            Metrics newMetrics = new Metrics();
            // putIfAbsent
            metrics = segmentMap.putIfAbsent(metricsKey, newMetrics);
            if (metrics == null) {
                // put成功， 返回新生成的
                return newMetrics;
            }
        }
        return metrics;
    }


}
