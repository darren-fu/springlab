package df.open.restyproxy.http.converter;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Http响应结果转换器
 * Created by darrenfu on 17-7-19.
 */
public interface ResponseConverter<T> {

    boolean support(Type type, String contentType);

    T convert(byte[] body, Type type, String contentType);

    default Charset getCharset(String contentType) {
        try {
            if (StringUtils.isNotEmpty(contentType) && contentType.lastIndexOf(";charset=") > 0) {

                int i = contentType.lastIndexOf(";charset=");
                if (i > 0 && contentType.length() > i + 9) {
                    return Charset.forName(contentType.substring(i + 9));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Charset.defaultCharset();
    }

}
