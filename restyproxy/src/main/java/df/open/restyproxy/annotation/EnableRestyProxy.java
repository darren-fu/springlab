package df.open.restyproxy.annotation;

import df.open.restyproxy.base.DefaultRestyProxyFactory;
import df.open.restyproxy.base.RestyProxyFactory;
import df.open.restyproxy.proxy.RestyProxyRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 说明:
 * <p/>
 * Copyright: Copyright (c)
 * <p/>
 * Company:
 * <p/>
 *
 * @author darren-fu
 * @version 1.0.0
 * @contact 13914793391
 * @date 2016/11/22
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RestyProxyRegister.class)
public @interface EnableRestyProxy {

    String value() default "";

    Class<? extends RestyProxyFactory> factory() default DefaultRestyProxyFactory.class;

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

}
