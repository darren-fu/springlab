package df.open.restyproxy.annotation.processor;

import df.open.restyproxy.annotation.RestyService;
import df.open.restyproxy.proxy.RestyCommandConfig;

import java.lang.annotation.Annotation;

/**
 * Created by darrenfu on 17-6-24.
 */
public class RestyServiceProcessor implements RestyAnnotationProcessor {


    @Override
    public RestyCommandConfig processor(Annotation annotation, RestyCommandConfig properties) {

        if (annotation != null && annotation.annotationType().equals(RestyService.class)) {

            RestyService restyService = (RestyService) annotation;

            String value = restyService.serviceName();
            properties.setServiceName(value);
            System.out.println("annotation value: " + value);
        }
        return properties;
    }
}
