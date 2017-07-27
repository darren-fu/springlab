package df.open.restyproxy.command;

import df.open.restyproxy.exception.ConnectionException;
import df.open.restyproxy.exception.RestyException;
import df.open.restyproxy.http.converter.ResponseConverter;
import df.open.restyproxy.http.converter.ResponseConverterContext;
import df.open.restyproxy.http.pojo.FailedResponse;
import df.open.restyproxy.util.FutureTools;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RestyFuture implements Future {

    private RestyCommand restyCommand;

    private ListenableFuture<Response> future;

    private ResponseConverterContext converterContext;

    private List<ResponseConverter> converterList;

    public RestyFuture(RestyCommand restyCommand, ListenableFuture<Response> future) {
        this.restyCommand = restyCommand;
        this.future = future;
        this.converterContext = ResponseConverterContext.DEFAULT;
    }

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

//    public <T> T getResult() {
//        T result = (T) converterContext.convertResponse(restyCommand, FutureTools.fetchResponse(future));
//
//        System.out.println(result);
//        System.out.println(result.getClass());
//        return result;
//    }

    public Response getResponse() {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("获取响应失败:", e.getMessage());
            return FailedResponse.create(new ConnectionException(e));
        }
    }


    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Response response = future.get(timeout, unit);
        return response;
    }


    public static RestyFuture async(Object obj) {
        if (obj instanceof RestyFuture) {
            return (RestyFuture) obj;
        }
        return null;
    }
}
