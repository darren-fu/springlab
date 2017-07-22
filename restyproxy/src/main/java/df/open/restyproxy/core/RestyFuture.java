package df.open.restyproxy.core;

import df.open.restyproxy.http.converter.ResponseConverter;
import df.open.restyproxy.http.converter.ResponseConverterContext;
import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.util.FutureTools;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by darrenfu on 17-7-20.
 */
public class RestyFuture implements Future {

    private RestyCommand restyCommand;

    private ListenableFuture<Response> future;

    private ResponseConverterContext converterContext;

    private List<ResponseConverter> converterList;


    public RestyFuture(RestyCommand restyCommand, ListenableFuture<Response> future, ResponseConverterContext converterContext) {
        this.restyCommand = restyCommand;
        this.future = future;
        this.converterContext = converterContext;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        Response response = future.get();
        return null;
    }

    public <T> T getResult() {
        T result = (T) converterContext.convertResponse(restyCommand, FutureTools.fetchResponse(future));

        System.out.println(result);
        System.out.println(result.getClass());
        return result;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Response response = future.get(timeout, unit);
        return null;
    }


    public static RestyFuture async(Object obj) {
        if (obj instanceof RestyFuture) {
            return (RestyFuture) obj;
        }
        return null;
    }
}
