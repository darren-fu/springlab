package df.open.restyproxy.exception;

/**
 * RestyCommand执行异常
 * Created by darrenfu on 17-7-22.
 */
public class ExecuteException extends RestyException {
    public ExecuteException(String msg) {
        super(msg);
    }

    public ExecuteException(String code, String msg) {
        super(code, msg);
    }
}
