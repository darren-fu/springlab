package df.open.restyproxy.exception;

/**
 * Resty请求错误异常
 * Created by darrenfu on 17-7-22.
 */
public class BadRequestException extends ExecuteException {
    public BadRequestException(String msg) {
        super(msg);
    }

    public BadRequestException(String code, String msg) {
        super(code, msg);
    }
}
