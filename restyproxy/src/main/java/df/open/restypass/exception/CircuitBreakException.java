package df.open.restypass.exception;

/**
 * 短路 默认抛出此异常
 * Created by darrenfu on 17-7-27.
 */
@SuppressWarnings("unused")
public class CircuitBreakException extends RestyException {
    public CircuitBreakException(String msg) {
        super(msg);
    }

    public CircuitBreakException(String code, String msg) {
        super(code, msg);
    }

    public CircuitBreakException(Throwable throwable) {
        super(throwable);
    }
}
