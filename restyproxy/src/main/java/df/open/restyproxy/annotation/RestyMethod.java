package df.open.restyproxy.annotation;

import java.lang.annotation.*;

/**
 * Resty方法注解
 * Created by darrenfu on 17-6-20.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RestyMethod {
//    String value() default "";

    String fallbackEnabled() default "";

    int retry() default -1;

    String circuitBreakEnabled() default "";

    String forceBreakEnabled() default "";
}
