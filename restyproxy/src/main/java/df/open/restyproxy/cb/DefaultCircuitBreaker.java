package df.open.restyproxy.cb;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.command.RestyCommandStatus;
import df.open.restyproxy.event.EventConsumer;
import df.open.restyproxy.exception.RequestException;
import df.open.restyproxy.lb.ServerInstance;
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
@ToString(exclude = {"commandQueue", "halfOpenLock"})
@Slf4j
public class DefaultCircuitBreaker implements CircuitBreaker {

    /**
     * key:[RestyCommand#path, ServerInstance#instanceId, segmentKey]
     */
    private static Table<String, String, String> keyTable = HashBasedTable.create();


    private static Integer maxSegmentSize = 10;

    private static Integer breakFailPercentage = 50;
    private static Integer breakFailCount = 10;

    private static Long halfOpenMilliseconds = 1000 * 10L;

    private static final String KEY_PREFIX = "Breaker-";

    private String eventKey;

    private ReentrantLock halfOpenLock;

    /**
     * 统计结果缓冲区 segmentKey->Deque
     */
    private ConcurrentHashMap<String, Deque<Metrics>> segmentMap;

    /**
     * 记录短路状态 segmentKey->Status
     */
    private ConcurrentHashMap<String, Status> statusMap;

    /**
     * Command 阻塞队列
     */
    private LinkedBlockingQueue<RestyCommand> commandQueue;

    private Set<String> brokenServerSet;

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
            this.eventKey = KEY_PREFIX + UUID.randomUUID().toString().replace("-", "");
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

        String segmentKey = getSegmentKey(restyCommand.getPath(), serverInstance.getInstanceId());
        // 获取统计段
        Deque<Metrics> metricsDeque = getSegmentDeque(segmentKey);
        Metrics first = metricsDeque.peekFirst();
        if (first == null) {
            return true;
        }
        boolean shouldPass = true;

        // 获取计数器
        Metrics.SegmentMetrics metrics = first.getMetrics();
        // 计数器中失败数量和比例超过阀值，则触发短路判断
        if (metrics.failCount() >= breakFailCount && metrics.failPercentage() >= breakFailPercentage) {
            Status status = statusMap.get(segmentKey);
            shouldPass = false;
            if (status == null || status == Status.OPEN) {
                // 短路
                statusMap.put(segmentKey, Status.BREAK);
            } else if (status == Status.HALFOPEN) {
                // noop
            } else {
                // 如果上次的请求距离现在超过阀值，则允许一次试探请求
                long now = System.currentTimeMillis();
                if (metrics.last() != null && (now - metrics.last() > halfOpenMilliseconds)) {
                    final ReentrantLock lock = this.halfOpenLock;
                    lock.lock();
                    try {
                        // 判断当前短路状态 确保只有一个请求通过
                        if (statusMap.get(segmentKey) == Status.BREAK) {
                            statusMap.put(segmentKey, Status.HALFOPEN);
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
     * @return
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
                        String key = getSegmentKey(restyCommand.getPath(), restyCommand.getInstanceId());
                        // 获取 统计段所属队列
                        Deque<Metrics> metricsDeque = getSegmentDeque(key);
                        Metrics first = metricsDeque.peekFirst();
                        if (first == null) {
                            first = new Metrics();
                            metricsDeque.addFirst(first);
                        }

                        boolean isSuccess = isSuccessCommand(restyCommand);
                        boolean forceUseNewMetrics = false;
                        // 如果当前处在短路或半短路状态
                        Status status = statusMap.get(key);
                        if ((status == Status.BREAK || status == Status.HALFOPEN)) {
                            // 结果成功 则不再短路，打开断路器
                            if (isSuccess) {
                                // 并使用一个新的计数器
                                forceUseNewMetrics = true;
                                statusMap.put(key, Status.OPEN);
                            } else {
                                // 否则恢复到短路状态
                                statusMap.put(key, Status.BREAK);
                            }
                        }
                        first.store(isSuccess, forceUseNewMetrics);
                    }
                    log.debug("处理完成, 处理个数:{}，剩余:{}个", commandList.size(), commandQueue.size());

                } catch (Exception ex) {
                    log.error("断路器RestyCommand处理失败:{}", ex);
                }

            }
        });
    }


    /**
     * command是否成功
     *
     * @param restyCommand
     * @return
     */
    private boolean isSuccessCommand(RestyCommand restyCommand) {
        if (restyCommand.getStatus() == RestyCommandStatus.FAILED) {
            if (restyCommand.getFailException() instanceof RequestException) {
                return true;
            } else {
                return false;
            }
        } else if (restyCommand.getStatus() == RestyCommandStatus.SUCCESS) {
            return true;
        }
        return false;
    }


    /**
     * 返回segmentKey
     *
     * @param commandPath
     * @param instanceId
     * @return
     */
    private String getSegmentKey(String commandPath, String instanceId) {
        String segmentKey = keyTable.get(commandPath, instanceId);
        if (StringUtils.isEmpty(segmentKey)) {
            segmentKey = commandPath + instanceId;
            keyTable.put(commandPath, instanceId, segmentKey);
        }
        return segmentKey;
    }


    /**
     * 获取 segmentDeque
     *
     * @param segmentKey
     * @return
     */
    private Deque<Metrics> getSegmentDeque(String segmentKey) {
        Deque<Metrics> segmentsDeque = segmentMap.get(segmentKey);
        if (segmentsDeque == null) {
            Deque<Metrics> deque = new ConcurrentLinkedDeque<>();
            // putIfAbsent
            segmentsDeque = segmentMap.putIfAbsent(segmentKey, deque);
            if (segmentsDeque == null) {
                // put成功， 返回新生成的
                return deque;
            }
        }
        if (segmentsDeque.size() > maxSegmentSize) {
            segmentsDeque.removeLast();
        }
        return segmentsDeque;
    }


}
