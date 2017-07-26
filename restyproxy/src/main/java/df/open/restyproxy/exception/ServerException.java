package df.open.restyproxy.exception;

/**
 * 下游服务异常
 * Created by darrenfu on 17-7-22.
 */
public class ServerException extends RestyException {

    public ServerException(Throwable throwable) {
        super(throwable);
    }
    public ServerException(String msg) {
        super(msg);
    }

    public ServerException(String code, String msg) {
        super(code, msg);
    }
}
