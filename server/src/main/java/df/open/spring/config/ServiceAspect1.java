package df.open.spring.config;

import df.open.spring.base.SpringContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 服务 性能切面
 * author: fuliang
 * date: 2017/3/30
 */
@SuppressWarnings("Duplicates")
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceAspect1 implements Ordered {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAspect1.class);


    //Service层切点
    @Pointcut("@target(org.springframework.stereotype.Service)" +
            "&& execution(public * *(..))")
    public void serviceAspect() {

    }

    @Before("serviceAspect()")
    public void doAop(JoinPoint joinPoint) {
        System.out.println("###1111before...");
    }

    @After("serviceAspect()")
    public void doAopAfter(JoinPoint joinPoint) {
        System.out.println("###11111after...");

        if (joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Object target = joinPoint.getTarget();
            Class<?> classTarget = target.getClass();
            Method method = methodSignature.getMethod();
            Object[] args = joinPoint.getArgs();

            System.out.println(target);
            Object bean = SpringContextHolder.getBean(classTarget);
            boolean aopProxy = AopUtils.isAopProxy(bean);
            try {
                Object invoke = method.invoke(bean, args);
                System.out.println("invoke");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public int getOrder() {
        return 4;
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
