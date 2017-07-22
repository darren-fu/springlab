package df.open.restyproxy.core;

import df.open.restyproxy.base.RestyCommandContext;
import df.open.restyproxy.http.converter.JsonResponseConverter;
import df.open.restyproxy.http.converter.ResponseConverter;
import df.open.restyproxy.http.converter.ResponseConverterContext;
import df.open.restyproxy.http.converter.StringResponseConverter;
import df.open.restyproxy.lb.LoadBalancer;
import df.open.restyproxy.lb.ServerContext;
import df.open.restyproxy.lb.ServerInstance;
import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.util.FutureTools;
import org.asynchttpclient.*;
import org.asynchttpclient.uri.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 异步Resty请求执行器
 * Created by darrenfu on 17-7-1.
 */
public class AsyncCommandExecutor implements CommandExecutor {

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

    public AsyncCommandExecutor(RestyCommandContext context, ServerContext serverContext) {
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


        // 负载均衡器 选择可用服务实例
        ServerInstance serverInstance = lb.choose(serverContext, restyCommand, Collections.EMPTY_LIST);
        this.buildRequest(serverInstance, restyCommand);
        // 获取分配的异步连接池
        AsyncHttpClient httpClient = context.getHttpClient(restyCommand.getServiceName());

        // 执行Resty请求
        ListenableFuture<Response> future = httpClient.executeRequest(this.request);

        boolean isAsync = false;

        if (!isAsync) {
            Response response = FutureTools.fetchResponse(future);
            Object restyResult = ResponseConverterContext.DEFAULT.convertResponse(restyCommand, response);
            return restyResult;
        } else {
            RestyFuture restyFuture = new RestyFuture(restyCommand, future, ResponseConverterContext.DEFAULT);
            return restyFuture;
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
