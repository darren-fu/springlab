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

    String serviceName() default "";

    Class fallbackClass() default Noop.class;

    String fallbackBean() default "";

    boolean fallbackEnabled() default true;

    int retry() default 1;

    boolean circuitBreakEnabled() default true;


    class Noop {

    }
}
