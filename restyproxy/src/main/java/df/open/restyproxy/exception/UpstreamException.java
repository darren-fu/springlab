package df.open.restyproxy.exception;

/**
 * 下游服务异常
 * Created by darrenfu on 17-7-22.
 */
public class UpstreamException extends ExecuteException {
    public UpstreamException(String msg) {
        super(msg);
    }

    public UpstreamException(String code, String msg) {
        super(code, msg);
    }
}
