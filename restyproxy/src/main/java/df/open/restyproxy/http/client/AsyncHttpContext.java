package df.open.restyproxy.http.client;

import df.open.restyproxy.http.config.AsyncHttpConfig;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;

/**
 * Created by darrenfu on 17-6-19.
 */
public class AsyncHttpContext {

    private static AsyncHttpClient asyncHttpClient;

    private AsyncHttpContext(AsyncHttpClientConfig clientConfig) {
        this.asyncHttpClient = new DefaultAsyncHttpClient(clientConfig);
    }

    public AsyncHttpClient getClient() {
        return asyncHttpClient;
    }

    public static AsyncHttpContext getInstance() {
        return AsyncHttpContextHolder.instance;
    }

    private static class AsyncHttpContextHolder {
        private static AsyncHttpContext instance = new AsyncHttpContext(AsyncHttpConfig.getConfig());
    }

}
