package df.open.restypass.annotation;

import java.lang.annotation.*;

/**
 * 标注为Resty请求方法注解
 * Created by darrenfu on 17-6-20.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RestyMethod {

    /**
     * 是否启用降级服务，为空时以@RestyService的配置为准
     * "true" or "false"
     *
     * @see RestyMethod#fallbackEnabled()
     */
    String fallbackEnabled() default "";

    /**
     * 失败后重试次数，为-1时以@RestyService的配置为准
     *
     * @see RestyMethod#retry()
     */
    int retry() default -1;

    /**
     * 是否启用断路器，为空时以@RestyService的配置为准
     *
     * @see RestyMethod#circuitBreakEnabled()
     */
    String circuitBreakEnabled() default "";

    /**
     * 是否强制短路，为空时以@RestyService的配置为准
     *
     * @see RestyMethod#forceBreakEnabled()
     */
    String forceBreakEnabled() default "";
}
