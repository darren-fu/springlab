package df.open.restyproxy.annotation.processor;

import df.open.restyproxy.annotation.RestyMethod;
import df.open.restyproxy.command.RestyCommandConfig;

import java.lang.annotation.Annotation;

/**
 * Created by darrenfu on 17-6-24.
 */
public class RestyMethodProcessor implements RestyAnnotationProcessor {


    @Override
    public RestyCommandConfig processor(Annotation annotation, RestyCommandConfig properties) {

        if (annotation != null && annotation.annotationType().equals(RestyMethod.class)) {

            RestyMethod restyMethod = (RestyMethod) annotation;

            String value = restyMethod.value();

            System.out.println("annotation value: " + value);
        }
        return properties;
    }
}
