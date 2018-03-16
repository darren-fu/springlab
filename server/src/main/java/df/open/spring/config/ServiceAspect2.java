package df.open.spring.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclarePrecedence;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 服务 性能切面
 * author: fuliang
 * date: 2017/3/30
 */
@SuppressWarnings("Duplicates")
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@DeclarePrecedence("df.open.spring.config.ServiceAspect1,df.open.spring.config.ServiceAspect2,org.springframework.cache.aspectj.AnnotationCacheAspect,*")
public class ServiceAspect2 implements Ordered {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAspect2.class);


    //Service层切点
    @Pointcut("@target(org.springframework.stereotype.Service)" +
            "&& execution(public * *(..))")
    public void serviceAspect() {

    }

    @Before("serviceAspect()")
    public void doAop(JoinPoint joinPoint) {
        System.out.println("###222222before...");
    }

    @Override
    public int getOrder() {
        return 3;
    }

//    @Around("serviceAspect()")
//    public Object around(ProceedingJoinPoint point) throws Throwable {
//        Object result;
//        String methodName = point.getSignature().getName();
//        Class<?> classTarget = point.getTarget().getClass();
//        Long start = System.currentTimeMillis();
//        System.out.println("########### around...");
//        try {
//            //执行目标方法
//            result = point.proceed();
//        } catch (Throwable e) {
//            throw e;
//        } finally {
//        }
//        return result;
//    }

}
