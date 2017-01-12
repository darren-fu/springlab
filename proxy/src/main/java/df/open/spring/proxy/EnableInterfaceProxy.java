package df.open.spring.proxy;

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
@Import(InterfaceProxyRegister.class)
public @interface EnableInterfaceProxy {

    String value() default "";

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

}
