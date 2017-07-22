package df.open.restyproxy.annotation.processor;

import df.open.restyproxy.command.RestyCommandConfig;

import java.lang.annotation.Annotation;

/**
 * Created by darrenfu on 17-6-24.
 */
public interface RestyAnnotationProcessor {

    RestyCommandConfig processor(Annotation annotation, RestyCommandConfig properties);

}
