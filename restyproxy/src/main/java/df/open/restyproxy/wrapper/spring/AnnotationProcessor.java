package df.open.restyproxy.wrapper.spring;

import df.open.restyproxy.base.RestyRequestTemplate;

import java.lang.annotation.Annotation;

/**
 * Created by darrenfu on 17-7-19.
 */
public interface AnnotationProcessor {

    /**
     * Gets annotation type.
     *
     * @return the annotation type
     */
    Class<? extends Annotation> getAnnotationType();

    /**
     * Process boolean.
     *
     * @param requestTemplate the request template
     * @param annotation      the annotation
     * @return the boolean
     */
    boolean process(RestyRequestTemplate requestTemplate, Annotation annotation);

}
