package df.open.restypass.http.converter;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Created by darrenfu on 17-7-19.
 */
public class StringResponseConverter implements ResponseConverter<String> {

    private static final String TEXT_PLAIN = "text/plain";

    private Charset charset = Charset.defaultCharset();

    @Override
    public boolean support(Type type, String contentType) {

        if (!contentType.startsWith(TEXT_PLAIN)) {
            return false;
        }

        if (type instanceof Class && type != String.class) {
            return false;
        }

        return true;
    }

    @Override
    public String convert(byte[] body, Type type, String contentType) {
        return new String(body, getCharset(contentType));
    }
}
