package df.open.restyproxy.base;

import lombok.Data;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Request 模板
 * Created by darrenfu on 17-6-25.
 */
@Data
public class RestyRequestTemplate {
    private static final String HTTP_POST = "POST";
    private static final String HTTP_GET = "GET";

    private Method method;

    private String path;
    private String httpMethod;

    private String baseUrl;
    private String methodUrl;

    private Map<String, String> headers;

    private Map<String, Object> params;

    private Map<String, Object> queries;

    private Map<String, Object> pathVariables;


    public void addHeader(String head, String value) {
        if (headers == null) {
            headers = new HashMap();
        }
        headers.put(head, value);
    }

    public void addParam(String param, String value) {
        if (params == null) {
            params = new HashMap();
        }
        params.put(param, value);
    }


}
