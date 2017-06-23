package df.open.restyproxy.http.config;

import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

/**
 * Created by darrenfu on 17-6-19.
 */
public class AsyncHttpConfig {

    private static AsyncHttpClientConfig clientConfig;

    static {

        clientConfig = new DefaultAsyncHttpClientConfig.Builder()
                .setConnectTimeout(5000)
                .setMaxConnectionsPerHost(10000)
                .setValidateResponseHeaders(false)
                .build();

    }

    public static AsyncHttpClientConfig getConfig() {
        return clientConfig;
    }

    public static AsyncHttpClientConfig refreshConfig() {
        return clientConfig;
    }


}
