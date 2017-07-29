package df.open.restypass.executor;

import df.open.restypass.cb.CircuitBreaker;
import df.open.restypass.cb.CircuitBreakerFactory;
import df.open.restypass.command.RestyCommand;
import df.open.restypass.command.RestyCommandContext;
import df.open.restypass.command.RestyFuture;
import df.open.restypass.enums.RestyCommandStatus;
import df.open.restypass.exception.CircuitBreakException;
import df.open.restypass.http.converter.ResponseConverterContext;
import df.open.restypass.lb.LoadBalancer;
import df.open.restypass.lb.server.ServerContext;
import df.open.restypass.lb.server.ServerInstance;
import org.asynchttpclient.Response;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 异步Resty请求执行器
 * Created by darrenfu on 17-7-1.
 */
public class RestyCommandExecutor implements CommandExecutor {

    /**
     * Resty请求上下文
     */
    private RestyCommandContext context;

    /**
     * 服务实例上下文
     */
    private ServerContext serverContext;


    public RestyCommandExecutor(RestyCommandContext context, ServerContext serverContext) {
        this.context = context;
        this.serverContext = serverContext;
    }

    @Override
    public boolean executable(RestyCommand restyCommand) {
        return restyCommand != null && restyCommand.getStatus() == RestyCommandStatus.INIT;
    }

    @Override
    public Object execute(LoadBalancer lb, RestyCommand restyCommand) {

        // 重试次数
        int retry = restyCommand.getRestyCommandConfig().getRetry();

        Object result = null;
        CircuitBreaker circuitBreaker = CircuitBreakerFactory.defaultCircuitBreaker(restyCommand.getServiceName());
        ServerInstance serverInstance = null;

        // 排除 彻底断路的server， 尝试过的server
        // 1.判断command使用的serverInstanceList是否存在被熔断的server
        // 1.1 存在的话 server加入 loadBalance 的excludeServerList

        Set<String> excludeInstanceIdList = circuitBreaker.getBrokenServer();

        // 重试机制
        for (int times = 0; times <= retry; times++) {
            try {
                // 负载均衡器 选择可用服务实例
                serverInstance = lb.choose(serverContext, restyCommand, excludeInstanceIdList);

                boolean shouldPass = circuitBreaker.shouldPass(restyCommand, serverInstance);

                if (!shouldPass) {
                    System.out.println("##should not pass!!!!");
                    // fallback or exception
                    throw new CircuitBreakException("circuit break is working");
                }

                RestyFuture future = restyCommand.ready(circuitBreaker)
                        .start(serverInstance);

                // 同步调用
                Response response = future.getResponse();
                result = ResponseConverterContext.DEFAULT.convertResponse(restyCommand, response);
                if (restyCommand.getStatus() == RestyCommandStatus.FAILED) {
                    throw restyCommand.getFailException();
                }

                if (restyCommand.getStatus() == RestyCommandStatus.SUCCESS) {
                    // 响应成功，无需重试了
                    break;
                }
            } catch (Exception ex) {
                if (times == retry) {
                    System.out.println("##重试了:" + times + " 次，到达最大值:" + retry);
                    throw ex;
                } else {
                    // 将本次使用的server 加入排除列表
                    if (excludeInstanceIdList == null || excludeInstanceIdList == Collections.EMPTY_SET) {
                        excludeInstanceIdList = new HashSet<>();
                    }
                    if (serverInstance != null) {
                        excludeInstanceIdList.add(serverInstance.getInstanceId());
                    }
                    System.out.println("@@重试了:" + times + " 次，最大值:" + retry);
                }
            }
        }
        return result;
    }


}
