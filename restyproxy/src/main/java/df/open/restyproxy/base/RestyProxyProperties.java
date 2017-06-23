package df.open.restyproxy.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import df.open.restyproxy.util.JsonTools;
import lombok.Data;

/**
 * 系统参数
 * Created by darrenfu on 17-6-20.
 */
@Data
public class RestyProxyProperties {
    /**
     * 使用jackson序列化
     */
    private ObjectMapper objectMapper;


    public static RestyProxyProperties getDefaultProperties() {
        return DefaultPropertiesHolder.defaultProperties;
    }

    private static class DefaultPropertiesHolder {

        private static RestyProxyProperties defaultProperties = new RestyProxyProperties();

        static {
            defaultProperties.setObjectMapper(JsonTools.defaultMapper().getMapper());
        }
    }

}
