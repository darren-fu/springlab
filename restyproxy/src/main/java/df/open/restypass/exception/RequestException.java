package df.open.restypass.exception;

/**
 * Resty请求错误异常
 * Created by darrenfu on 17-7-22.
 */
@SuppressWarnings("unused")
public class RequestException extends RestyException {

    public RequestException(Throwable throwable) {
        super(throwable);
    }

    public RequestException(String msg) {
        super(msg);
    }

    public RequestException(String code, String msg) {
        super(code, msg);
    }
}
