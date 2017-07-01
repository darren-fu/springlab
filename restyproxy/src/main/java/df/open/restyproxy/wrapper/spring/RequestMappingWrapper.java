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

import static java.lang.String.format;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * Created by darrenfu on 17-6-19.
 */
public class RequestMappingWrapper {
    private ResourceLoader resourceLoader = new DefaultResourceLoader();


    private static final String ACCEPT = "Accept";

    private static final String CONTENT_TYPE = "Content-Type";


    public RestyRequestTemplate processAnnotation(Class clz, Method method) {
        RestyRequestTemplate requestTemplate = new RestyRequestTemplate();

        RequestMapping methodRequestMapping = method.getDeclaredAnnotation(RequestMapping.class);
        if (methodRequestMapping != null) {
            processAnnotationOnMethod(requestTemplate, methodRequestMapping, method);
        }
        return requestTemplate;
    }


    protected void processAnnotationOnMethod(RestyRequestTemplate requestTemplate,
                                             Annotation methodAnnotation, Method method) {
        if (!(methodAnnotation instanceof RequestMapping)) {
            return;
        }

        RequestMapping methodMapping = findMergedAnnotation(method, RequestMapping.class);
        // HTTP Method
        RequestMethod[] methods = methodMapping.method();
        if (methods.length == 0) {
            methods = new RequestMethod[]{RequestMethod.GET};
        }
        checkOne(method, methods, "method");
        requestTemplate.setRequestMethod(methods[0].name());

        // path
        checkAtMostOne(method, methodMapping.value(), "value");
        if (methodMapping.value().length > 0) {
            String pathValue = emptyToNull(methodMapping.value()[0]);
            if (pathValue != null) {
                pathValue = resolve(pathValue);
                // Append path from @RequestMapping if value is present on method
                if (!pathValue.startsWith("/")) {
                    pathValue = "/" + pathValue;
                }
                requestTemplate.setMethodUrl(pathValue);
            }
        }

        // produces
        parseProduces(requestTemplate, method, methodMapping);

        // consumes
        parseConsumes(requestTemplate, method, methodMapping);

        // headers
        parseHeaders(requestTemplate, method, methodMapping);

    }

    private void parseProduces(RestyRequestTemplate md, Method method,
                               RequestMapping annotation) {
        checkAtMostOne(method, annotation.produces(), "produces");
        String[] serverProduces = annotation.produces();
        String clientAccepts = serverProduces.length == 0 ? null
                : emptyToNull(serverProduces[0]);
        if (clientAccepts != null) {
            md.addHeader(ACCEPT, clientAccepts);
        }
    }

    private void parseConsumes(RestyRequestTemplate md, Method method,
                               RequestMapping annotation) {
        checkAtMostOne(method, annotation.consumes(), "consumes");
        String[] serverConsumes = annotation.consumes();
        String clientProduces = serverConsumes.length == 0 ? null
                : emptyToNull(serverConsumes[0]);
        if (clientProduces != null) {
            md.addHeader(CONTENT_TYPE, clientProduces);
        }
    }

    private void parseHeaders(RestyRequestTemplate md, Method method,
                              RequestMapping annotation) {
        // TODO: only supports one header value per key
        if (annotation.headers() != null && annotation.headers().length > 0) {
            for (String header : annotation.headers()) {
                int index = header.indexOf('=');
                md.addHeader(resolve(header.substring(0, index)),
                        resolve(header.substring(index + 1).trim()));
            }
        }
    }

    private void checkAtMostOne(Method method, Object[] values, String fieldName) {
        checkState(values != null && (values.length == 0 || values.length == 1),
                "Method %s can only contain at most 1 %s field. Found: %s",
                method.getName(), fieldName,
                values == null ? null : Arrays.asList(values));
    }

    private void checkOne(Method method, Object[] values, String fieldName) {
        checkState(values != null && values.length == 1,
                "Method %s can only contain 1 %s field. Found: %s", method.getName(),
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

    private String emptyToNull(String string) {
        return string == null || string.isEmpty() ? null : string;
    }

}
