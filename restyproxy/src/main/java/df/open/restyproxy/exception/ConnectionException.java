package df.open.restyproxy.exception;

/**
 * HTTP连接异常
 * Created by darrenfu on 17-7-22.
 */
public class ConnectionException extends RestyException {
    public ConnectionException(String msg) {
        super(msg);
    }

    public ConnectionException(String code, String msg) {
        super(code, msg);
    }
}
