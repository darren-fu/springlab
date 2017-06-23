package df.open.restyproxy.http.client;

import df.open.restyproxy.http.config.AsyncHttpConfig;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by darrenfu on 17-6-21.
 */
public class HttpClientFactory {


    private static ConcurrentHashMap<String, AsyncHttpClient>
            httpClientMap = new ConcurrentHashMap<>();

    public static AsyncHttpClient getAsyncHttpClient(String serviceName) {

        if (httpClientMap.get(serviceName) == null) {
            AsyncHttpClient client = new DefaultAsyncHttpClient(AsyncHttpConfig.getConfig());
            httpClientMap.putIfAbsent(serviceName, client);
        }
        return httpClientMap.get(serviceName);
    }
}
