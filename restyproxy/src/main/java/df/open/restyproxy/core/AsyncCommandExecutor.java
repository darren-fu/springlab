package df.open.restyproxy.core;

import df.open.restyproxy.base.RestyCommandContext;
import df.open.restyproxy.loadbalance.LoadBalancer;
import df.open.restyproxy.loadbalance.ServerInstance;
import df.open.restyproxy.proxy.RestyCommand;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Request;
import org.asynchttpclient.uri.Uri;

import java.net.URL;

/**
 * Created by darrenfu on 17-7-1.
 */
public class AsyncCommandExecutor implements CommandExecutor {

    private Request request;

    private RestyCommandContext context;

    @Override
    public <T> T execute(LoadBalancer lb, RestyCommand restyCommand) {

        return null;
    }

    private Request buildRequest(ServerInstance instance, RestyCommand restyCommand) {

        Request request = new BoundRequestBuilder(context.getHttpClient(restyCommand.getServiceName()),
                restyCommand.getMethod(),
                true)
                .setUri(instance.buildAsyncUri(restyCommand.getPath(), ""))
                .build();
        this.request = request;

        return null;
    }


    public static void main(String[] args) {
        Uri uri = Uri.create("http://localhost:8080/api/get?name=123&password=222");
        Uri newUri = uri.withNewQuery("aa=22&aaa=sss");
        System.out.println(uri.toUrl());
        System.out.println(uri.getPath());
        System.out.println(uri.getQuery());
        System.out.println(newUri.getQuery());
        System.out.println(uri.getScheme());
        System.out.println(uri.getSchemeDefaultPort());


    }

}
