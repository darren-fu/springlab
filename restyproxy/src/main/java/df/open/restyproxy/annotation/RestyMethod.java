package df.open.restyproxy.annotation;

import java.lang.annotation.*;

/**
 * Created by darrenfu on 17-6-20.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RestyMethod {
    String value() default "";

}
