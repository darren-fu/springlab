package df.open.restyproxy.annotation;

import java.lang.annotation.*;

/**
 * Created by darrenfu on 17-6-20.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RestyService {

    String value() default "";

}
