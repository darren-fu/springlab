package df.open.restyproxy.core;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import df.open.restyproxy.base.RestyCommandContext;
import df.open.restyproxy.http.converter.StringResponseConverter;
import df.open.restyproxy.loadbalance.LoadBalancer;
import df.open.restyproxy.loadbalance.ServerContext;
import df.open.restyproxy.loadbalance.ServerInstance;
import df.open.restyproxy.proxy.RestyCommand;
import df.open.restyproxy.util.JsonTools;
import org.asynchttpclient.*;
import org.asynchttpclient.uri.Uri;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

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

    public AsyncCommandExecutor(RestyCommandContext context, ServerContext serverContext) {
        this.context = context;
        this.serverContext = serverContext;
    }


    @Override
    public Object execute(LoadBalancer lb, RestyCommand restyCommand) {
        Object result = null;

        // 负载均衡器 选择可用服务实例
        ServerInstance serverInstance = lb.choose(serverContext, restyCommand, Collections.EMPTY_LIST);
        this.buildRequest(serverInstance, restyCommand);
        // 获取分配的异步连接池
        AsyncHttpClient httpClient = context.getHttpClient(restyCommand.getServiceName());

        // 执行Resty请求
        ListenableFuture<Response> future = httpClient.executeRequest(this.request);

        try {
            Response response = future.get();

            if (response != null && 200 != response.getStatusCode()) {
                throw new RuntimeException(response.getResponseBody());
            }

            byte[] body = response.getResponseBodyAsBytes();

            StringResponseConverter converter = new StringResponseConverter();

            if (converter.support(restyCommand.getReturnType(), response.getContentType())) {
                result = converter.convert(body, restyCommand.getReturnType());

            } else {
                JavaType type = TypeFactory.defaultInstance().constructType(restyCommand.getReturnType());
                result = JsonTools.defaultMapper().getMapper().readValue(body, type);

            }


        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

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
