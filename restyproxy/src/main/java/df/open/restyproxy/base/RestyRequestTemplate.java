package df.open.restyproxy.base;

import lombok.Data;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Request 模板
 * Created by darrenfu on 17-6-25.
 */
@Data
public class RestyRequestTemplate {
    private Method restyMethod;

    private String path;
    private String method;

    private String baseUrl;
    private String methodUrl;
    private Map<String, Object> headers;

    private List<String> paramList;

    public void addHeader(String head, String value) {
        headers.put(head, value);
    }
    

}
