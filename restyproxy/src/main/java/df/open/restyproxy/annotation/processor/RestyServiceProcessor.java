package df.open.restyproxy.annotation.processor;

import df.open.restyproxy.annotation.RestyService;
import df.open.restyproxy.command.RestyCommandConfig;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;

/**
 * Created by darrenfu on 17-6-24.
 */
public class RestyServiceProcessor implements RestyAnnotationProcessor {


    @Override
    public RestyCommandConfig processor(Annotation annotation, RestyCommandConfig properties) {

        if (annotation != null && annotation.annotationType().equals(RestyService.class)) {

            RestyService restyService = (RestyService) annotation;

            String serviceName = restyService.serviceName();
            if (StringUtils.isEmpty(serviceName)) {
                throw new IllegalArgumentException("service name can not be null");
            }
            properties.setServiceName(serviceName);

            //fallback
            properties.setFallbackEnabled(restyService.fallbackEnabled());
            if (restyService.fallbackClass() != null) {
                properties.setFallbackClass(restyService.fallbackClass());
            }

            // circuit break
            properties.setCircuitBreakEnabled(restyService.circuitBreakEnabled());

            System.out.println("annotation value: " + serviceName);
        }
        return properties;
    }
}
