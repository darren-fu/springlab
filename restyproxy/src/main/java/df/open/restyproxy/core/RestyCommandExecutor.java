package df.open.restyproxy.core;

import df.open.restyproxy.base.RestyCommandContext;
import df.open.restyproxy.cb.CircuitBreaker;
import df.open.restyproxy.cb.CircuitBreakerFactory;
import df.open.restyproxy.cb.DefaultCircuitBreaker;
import df.open.restyproxy.command.RestyFuture;
import df.open.restyproxy.event.EventConsumer;
import df.open.restyproxy.http.converter.JsonResponseConverter;
import df.open.restyproxy.http.converter.ResponseConverter;
import df.open.restyproxy.http.converter.ResponseConverterContext;
import df.open.restyproxy.http.converter.StringResponseConverter;
import df.open.restyproxy.http.pojo.FailedResponse;
import df.open.restyproxy.lb.LoadBalancer;
import df.open.restyproxy.lb.ServerContext;
import df.open.restyproxy.lb.ServerInstance;
import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.util.ClassTools;
import df.open.restyproxy.util.FutureTools;
import org.asynchttpclient.*;
import org.asynchttpclient.uri.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    public Object execute(LoadBalancer lb, RestyCommand restyCommand) {

        //TODO 熔断判断
        // 0.command = path + serviceName
        // 1.判断command使用的serverInstanceList是否存在被熔断的server
        // 1.1 存在的话 server加入 loadBalance 的excludeServerList
        //
        CircuitBreaker circuitBreaker = CircuitBreakerFactory.defaultCircuitBreaker(restyCommand.getServiceName());


        // 负载均衡器 选择可用服务实例
        ServerInstance serverInstance = lb.choose(serverContext, restyCommand, Collections.EMPTY_LIST);

        boolean shouldPass = circuitBreaker.shouldPass(restyCommand, serverInstance);

        if (!shouldPass) {
            System.out.println("##should not pass!!!!");
            return null;
        }

        RestyFuture future = restyCommand.ready(circuitBreaker)
                .start(serverInstance);

        boolean isSync = true;


        if (isSync) {

            Response response = future.getResponse();


            Object restyResult = ResponseConverterContext.DEFAULT.convertResponse(restyCommand, response);
            EventConsumer consumer = ClassTools.castTo(circuitBreaker, EventConsumer.class);
            restyCommand.emit(consumer.getEventKey(), restyCommand);

            return restyResult;
        } else {
//            RestyFuture restyFuture = new RestyFuture(restyCommand, future, ResponseConverterContext.DEFAULT);
//            return restyFuture;
            return null;
        }
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
