package df.open.restyproxy.http.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import df.open.restyproxy.util.JsonTools;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by darrenfu on 17-7-19.
 */
@Slf4j
public class JsonResponseConverter implements ResponseConverter<Object> {

    private static final String APPLICATION_JSON = "application/json";
    private ObjectMapper objectMapper;


    public JsonResponseConverter() {
        this.objectMapper = JsonTools.defaultMapper().getMapper();
    }

    @Override
    public boolean support(Type type, String contentType) {
        if (!contentType.startsWith(APPLICATION_JSON)) {
            return false;
        }
        return true;
    }

    @Override
    public Object convert(byte[] body, Type type, String contentType) {
        JavaType javaType = TypeFactory.defaultInstance().constructType(type);
        try {
            return objectMapper.readValue(body, javaType);
        } catch (IOException e) {
            log.error("JSON转换失败,javaType:{}", javaType);
            e.printStackTrace();
        }
        return null;
    }
}
