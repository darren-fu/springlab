package df.open.restyproxy.exception;

/**
 * Resty 基础异常类
 * <p>
 * Created by darrenfu on 17-7-22.
 */
public class RestyException extends RuntimeException {

    private static String RESTY_EXCEPTION_CODE = "RESTY_EXCEPTION";

    private String code;

    public RestyException(String msg) {
        super(msg);
        this.code = RESTY_EXCEPTION_CODE;
    }


    public RestyException(String code, String msg) {
        super(msg);
        this.code = code;
    }

}
