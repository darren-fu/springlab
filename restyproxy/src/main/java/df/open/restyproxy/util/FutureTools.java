package df.open.restyproxy.util;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import java.util.concurrent.ExecutionException;

/**
 * Created by darrenfu on 17-7-20.
 */
public class FutureTools {


    /**
     * Fetch response response.
     *
     * @param future the future
     * @return the response
     */
    public static Response fetchResponse(ListenableFuture<Response> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
