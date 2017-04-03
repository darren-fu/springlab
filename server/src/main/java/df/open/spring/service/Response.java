package df.open.spring.service;

/**
 * Created by darrenfu on 17-4-1.
 */
public class Response<T> {
    T info;

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code;
}
