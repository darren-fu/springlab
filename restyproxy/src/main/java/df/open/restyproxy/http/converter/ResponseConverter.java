package df.open.restyproxy.http.converter;

import java.lang.reflect.Type;

/**
 * Http响应结果转换器
 * Created by darrenfu on 17-7-19.
 */
public interface ResponseConverter<T> {

    boolean support(Type type, String contentType);

    T convert(byte[] body, Type type);

}
