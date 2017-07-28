package df.open.restyproxy.wrapper.spring;

import df.open.restyproxy.base.RestyRequestTemplate;
import df.open.restyproxy.wrapper.spring.pojo.PathVariableData;
import df.open.restyproxy.wrapper.spring.pojo.RequestBodyData;
import df.open.restyproxy.wrapper.spring.pojo.RequestParamData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static df.open.restyproxy.util.CommonTools.emptyToNull;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * Spring 注解 包装解析类
 * Created by darrenfu on 17-7-19.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SpringAnnotationWrapper {

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    private RequestMappingProcessor requestMappingProcessor = new RequestMappingProcessor();

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


        // 处理method参数的注解 @RequestParam @PathVariable
        processAnnotationsOnParams(requestTemplate, method);

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


    /**
     * Process annotations on params.
     *
     * @param requestTemplate the request template
     * @param method          the method
     */
    protected void processAnnotationsOnParams(RestyRequestTemplate requestTemplate, Method method) {
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            // 处理PathVariable
            PathVariable pathVariable = findMergedAnnotation(parameter, PathVariable.class);
            if (pathVariable != null) {
                PathVariableData pathVariableData = new PathVariableData();
                pathVariableData.setIndex(i);
                pathVariableData.setName("{" + (StringUtils.isNotEmpty(pathVariable.name()) ? pathVariable.name() : parameter.getName()) + "}");
                pathVariableData.setRequired(pathVariable.required());
                requestTemplate.addPathVariable(pathVariableData);
            }
            // 处理RequestParam
            RequestParam requestParam = findMergedAnnotation(parameter, RequestParam.class);
            if (requestParam != null) {
                RequestParamData requestParamData = new RequestParamData();
                requestParamData.setIndex(i);
                requestParamData.setName(StringUtils.isNotEmpty(requestParam.name()) ? requestParam.name() : parameter.getName());
                requestParamData.setRequired(requestParam.required());
                requestParamData.setDefaultValue(requestParam.defaultValue());
                requestTemplate.addRequestParam(requestParamData);
            }

            // 处理RequestBody和无注解参数
            RequestBody requestBody = findMergedAnnotation(parameter, RequestBody.class);
            if (requestBody != null || parameter.getAnnotations().length == 0) {
                RequestBodyData requestBodyData = new RequestBodyData();
                requestBodyData.setIndex(i);
                requestBodyData.setName(parameter.getName());
                requestBodyData.setDefaultValue(null);
                if (requestBody != null) {
                    requestBodyData.setRequired(requestBody.required());
                } else {
                    requestBodyData.setRequired(false);
                }
                requestTemplate.addRequestBody(requestBodyData);
            }
        }
    }


    private String resolve(String value) {
        if (StringUtils.isNotEmpty(value)
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
