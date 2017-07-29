package df.open.restypass.http.client;

import lombok.Data;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;

/**
 * Created by darrenfu on 17-6-24.
 */
@Data
public class HttpClientHolder {

    private AsyncHttpClient client;

    private AsyncHttpClientConfig config;

    public HttpClientHolder(AsyncHttpClientConfig config) {
        this.client = new DefaultAsyncHttpClient(config);
        this.config = config;
    }

    public HttpClientHolder(AsyncHttpClient client, AsyncHttpClientConfig config) {
        this.client = client;
        this.config = config;
    }
}
