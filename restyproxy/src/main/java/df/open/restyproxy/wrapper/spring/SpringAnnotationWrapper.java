package df.open.restyproxy.wrapper.spring;

import df.open.restyproxy.base.RestyRequestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

import static df.open.restyproxy.util.CommonTools.emptyToNull;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * Spring 注解 包装解析类
 * Created by darrenfu on 17-7-19.
 */
public class SpringAnnotationWrapper {
    private static final String ACCEPT = "Accept";

    private static final String CONTENT_TYPE = "Content-Type";

    private ResourceLoader resourceLoader = new DefaultResourceLoader();


    private RequestMappingProcessor requestMappingProcessor = new RequestMappingProcessor();
    private RequestParamProcessor requestParamProcessor = new RequestParamProcessor();

    /**
     * Instantiates a new Spring annotation wrapper.
     */
    public SpringAnnotationWrapper() {
    }


    /**
     * Process annotation resty request template.
     *
     * @param clz    the clz
     * @param method the method
     * @return the resty request template
     */
    public RestyRequestTemplate processAnnotation(Class clz, Method method) {
        RestyRequestTemplate requestTemplate = new RestyRequestTemplate();
        requestTemplate.setMethod(method);

        // 解析Class上的RequestMapping
        if (clz.getInterfaces().length == 1) {
            processAnnotationOnClass(requestTemplate, clz.getInterfaces()[0]);
        }
        processAnnotationOnClass(requestTemplate, clz);

        // 解析method上的RequestMapping
        RequestMapping methodRequestMapping = findMergedAnnotation(method, RequestMapping.class);
        if (methodRequestMapping != null) {
            requestMappingProcessor.process(requestTemplate, methodRequestMapping);
        }
        processTemplatePath(requestTemplate);

        return requestTemplate;
    }


    private void processTemplatePath(RestyRequestTemplate requestTemplate) {
        requestTemplate.setPath(defaultIfEmpty(requestTemplate.getBaseUrl(), "")
                + defaultIfEmpty(requestTemplate.getMethodUrl(), ""));
    }

    /**
     * Process annotation on class.
     *
     * @param requestTemplate the request template
     * @param clz             the clz
     */
    protected void processAnnotationOnClass(RestyRequestTemplate requestTemplate, Class<?> clz) {
        RequestMapping classAnnotation = findMergedAnnotation(clz,
                RequestMapping.class);
        if (classAnnotation != null) {
            // Prepend path from class annotation if specified
            if (classAnnotation.value().length > 0) {
                String pathValue = emptyToNull(classAnnotation.value()[0]);
                pathValue = resolve(pathValue);
                if (!pathValue.startsWith("/")) {
                    pathValue = "/" + pathValue;
                }
                requestTemplate.setBaseUrl(pathValue);
            }
        }
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
