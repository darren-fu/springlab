package df.open.restyproxy.exception;

/**
 * 请求连接异常（httpConnectException, TimeoutException,InterruptException）
 * Created by darrenfu on 17-7-22.
 */
public class ConnectionException extends RestyException {

    public ConnectionException(Throwable throwable) {
        super(throwable);
    }

    public ConnectionException(String msg) {
        super(msg);
    }

    public ConnectionException(String code, String msg) {
        super(code, msg);
    }
}
