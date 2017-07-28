package df.open.restyproxy.wrapper.spring;

import df.open.restyproxy.base.RestyRequestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

import static df.open.restyproxy.util.CommonTools.emptyToNull;
import static java.lang.String.format;

/**
 * RequestMapping 解析类
 * base on SpringMvcContract
 * Created by darrenfu on 17-6-19.
 */
public class RequestMappingProcessor implements AnnotationProcessor {
    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    private static final Class<RequestMapping> ANNOTATION = RequestMapping.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean process(RestyRequestTemplate requestTemplate, Annotation annotation) {

        RequestMapping methodMapping = ANNOTATION.cast(annotation);
        // HTTP Method
        RequestMethod[] methods = methodMapping.method();
        if (methods.length == 0) {
            methods = new RequestMethod[]{RequestMethod.GET};
        }
        checkOne(methods, "method");
        requestTemplate.setHttpMethod(methods[0].name());

        // path
        checkAtMostOne(methodMapping.value(), "value");
        if (methodMapping.value().length > 0) {
            String pathValue = emptyToNull(methodMapping.value()[0]);
            if (pathValue != null) {
                pathValue = resolve(pathValue);
                // Append path from @RequestMapping if value is present on httpMethod
                if (!pathValue.startsWith("/")) {
                    pathValue = "/" + pathValue;
                }
                requestTemplate.setMethodUrl(pathValue);
            }
        }

        // produces #不需要解析consumes， 无视接口返回的content-type，只处理application/json
        // parseProduces(requestTemplate, method, methodMapping);

        // consumes #不需要解析consumes， 无视接口所需的content-type，只提交application/json
        // parseConsumes(requestTemplate, method, methodMapping);

        // params
        parseParams(requestTemplate, null, methodMapping);

        // headers
        parseHeaders(requestTemplate, null, methodMapping);
        return false;
    }

    private void parseHeaders(RestyRequestTemplate requestTemplate, Method method,
                              RequestMapping annotation) {
        // TODO: only supports one header value per key
        for (String header : annotation.headers()) {
            int index = header.indexOf('=');
            requestTemplate.addHeader(resolve(header.substring(0, index)),
                    resolve(header.substring(index + 1).trim()));
        }
    }

    private void parseParams(RestyRequestTemplate requestTemplate, Method method,
                             RequestMapping annotation) {
        // TODO: only supports one header value per key
        for (String param : annotation.params()) {
            int index = param.indexOf('=');
            requestTemplate.addParam(resolve(param.substring(0, index)),
                    resolve(param.substring(index + 1).trim()));
        }
    }

    private void checkAtMostOne(Object[] values, String fieldName) {
        checkState(values != null && (values.length == 0 || values.length == 1),
                "Method can only contain at most 1 %s field. Found: %s",
                fieldName,
                values == null ? null : Arrays.asList(values));
    }

    private void checkOne(Object[] values, String fieldName) {
        checkState(values != null && values.length == 1,
                "Method can only contain 1 %s field. Found: %s",
                fieldName, values == null ? null : Arrays.asList(values));
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)
                && this.resourceLoader instanceof ConfigurableApplicationContext) {
            return ((ConfigurableApplicationContext) this.resourceLoader).getEnvironment()
                    .resolvePlaceholders(value);
        }
        return value;
    }

    private void checkState(boolean expression,
                            String errorMessageTemplate,
                            Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException(
                    format(errorMessageTemplate, errorMessageArgs));
        }
    }

}
