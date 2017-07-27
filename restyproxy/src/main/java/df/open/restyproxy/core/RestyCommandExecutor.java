package df.open.restyproxy.core;

import df.open.restyproxy.base.RestyCommandContext;
import df.open.restyproxy.cb.CircuitBreaker;
import df.open.restyproxy.cb.CircuitBreakerFactory;
import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.command.RestyCommandStatus;
import df.open.restyproxy.command.RestyFuture;
import df.open.restyproxy.exception.CircuitBreakException;
import df.open.restyproxy.http.converter.JsonResponseConverter;
import df.open.restyproxy.http.converter.ResponseConverter;
import df.open.restyproxy.http.converter.ResponseConverterContext;
import df.open.restyproxy.http.converter.StringResponseConverter;
import df.open.restyproxy.lb.LoadBalancer;
import df.open.restyproxy.lb.ServerContext;
import df.open.restyproxy.lb.ServerInstance;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.asynchttpclient.uri.Uri;

import java.util.*;

/**
 * 异步Resty请求执行器
 * Created by darrenfu on 17-7-1.
 */
public class RestyCommandExecutor implements CommandExecutor {

    /**
     * Async Http 请求
     */
    private Request request;

    /**
     * Resty请求上下文
     */
    private RestyCommandContext context;

    /**
     * 服务实例上下文
     */
    private ServerContext serverContext;


    private List<ResponseConverter> converterList;

    public RestyCommandExecutor(RestyCommandContext context, ServerContext serverContext) {
        this.context = context;
        this.serverContext = serverContext;

        this.converterList = new ArrayList<>();
        converterList.add(new JsonResponseConverter());
        converterList.add(new StringResponseConverter());
    }


    @Override
    public boolean executable(RestyCommand restyCommand) {
        return restyCommand != null && restyCommand.getStatus() == RestyCommandStatus.INIT;
    }

    @Override
    public Object execute(LoadBalancer lb, RestyCommand restyCommand) {


        int retry = restyCommand.getRestyCommandConfig().getRetry();

        Object result = null;
        CircuitBreaker circuitBreaker = CircuitBreakerFactory.defaultCircuitBreaker(restyCommand.getServiceName());
        ServerInstance serverInstance = null;

        Set<String> excludeInstanceIdList = circuitBreaker.getBrokenServer();

        for (int times = 0; times <= retry; times++) {
            try {


                // 0.command = path + serviceName
                // 1.判断command使用的serverInstanceList是否存在被熔断的server
                // 1.1 存在的话 server加入 loadBalance 的excludeServerList

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
            } finally {
                System.out.println("finally...");
            }
        }
        return result;
    }

    /**
     * 构建Request
     *
     * @param instance
     * @param restyCommand
     */
    private void buildRequest(ServerInstance instance, RestyCommand restyCommand) {

        BoundRequestBuilder requestBuilder = new BoundRequestBuilder(context.getHttpClient(restyCommand.getServiceName()),
                restyCommand.getHttpMethod(),
                true);
        requestBuilder.setUri(restyCommand.getUri(instance));
        requestBuilder.setSingleHeaders(restyCommand.getRequestTemplate().getHeaders());
        this.request = requestBuilder.build();
    }


    public static void main(String[] args) {
        Uri uri = Uri.create("http://localhost:8080/api/get?name=123&password=222");
        Uri newUri = uri.withNewQuery("&aa=22&aaa=sss");
        System.out.println(uri.toUrl());
        System.out.println(uri.getPath());
        System.out.println(uri.getQuery());
        System.out.println(newUri.getQuery());
        System.out.println(uri.getScheme());
        System.out.println(uri.getSchemeDefaultPort());
    }

}
