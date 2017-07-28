package df.open.restyproxy.annotation;

import java.lang.annotation.*;

/**
 * 标注为RestyService的注解
 * Created by darrenfu on 17-6-20.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RestyService {

    /**
     * 服务名称
     */
    String serviceName() default "";

    /**
     * 降级服务类
     * 优先级低于降级服务Bean：fallbackBean
     */
    Class fallbackClass() default Noop.class;

    /**
     * 降级服务bean的名称（Spring管理的Bean）
     * 优先级高于降级服务类:fallbackClass
     */
    String fallbackBean() default "";

    /**
     * 降级服务是否启用
     * 默认启用
     */
    boolean fallbackEnabled() default true;

    /**
     * 失败后是否重试
     * 默认重试一次
     */
    int retry() default 1;

    /**
     * 断路器是否启用
     * 默认启用
     */
    boolean circuitBreakEnabled() default true;

    /**
     * 是否强制短路
     * 默认false
     */
    boolean forceBreakEnabled() default false;

    /**
     * 啥也不干的类
     */
    class Noop {

    }
}
