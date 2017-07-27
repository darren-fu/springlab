package df.open.restyproxy.fallback;

import df.open.restyproxy.annotation.RestyService;
import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.command.RestyCommandConfig;
import df.open.restyproxy.command.RestyCommandStatus;
import df.open.restyproxy.core.CommandExecutor;
import df.open.restyproxy.exception.RestyException;
import df.open.restyproxy.lb.LoadBalancer;
import df.open.restyproxy.util.ClassTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务降级处理类
 * Created by darrenfu on 17-7-27.
 */
@Slf4j
public class FallbackExecutor implements CommandExecutor {

    private static ConcurrentHashMap<Class, Object> fallbackClassMap = new ConcurrentHashMap<>();


    @Override
    public boolean executable(RestyCommand restyCommand) {
        return restyCommand != null
                && restyCommand.getStatus() == RestyCommandStatus.FAILED
                && ((restyCommand.getRestyCommandConfig().getFallbackClass() != null && restyCommand.getRestyCommandConfig().getFallbackClass() != RestyService.Noop.class)
                || StringUtils.isNotEmpty(restyCommand.getRestyCommandConfig().getFallbackBean()));
    }

    @Override
    public Object execute(LoadBalancer lb, RestyCommand restyCommand) {

        RestyCommandConfig config = restyCommand.getRestyCommandConfig();
        Class fallbackClass = config.getFallbackClass();

        if (fallbackClass != null && fallbackClass != RestyService.Noop.class) {
            Object fallbackObj = fallbackClassMap.get(fallbackClass);

            if (fallbackObj == null) {
                fallbackObj = ClassTools.instance(fallbackClass);
                Object existObj = fallbackClassMap.putIfAbsent(fallbackClass, fallbackObj);
                if (existObj != null) {
                    fallbackObj = existObj;
                }
            }
            Method fallbackMethod = findMethodInFallbackClass(fallbackClass, restyCommand);

            try {
                return fallbackMethod.invoke(fallbackObj, copyArgsWithException(restyCommand));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private Method findMethodInFallbackClass(Class fallbackClass,
                                             RestyCommand restyCommand) {
        Method serviceMethod = restyCommand.getServiceMethod();
        String methodName = serviceMethod.getName();


        Method method = getMethod(fallbackClass, methodName, copyParamsWithException(restyCommand));
        if (method == null) {
            method = getMethod(fallbackClass, methodName, serviceMethod.getParameterTypes());
            if (method == null) {
                log.error("{}中没有发现合适的降级方法:{}", fallbackClass, methodName);
                throw new RuntimeException(fallbackClass + "没有合适的降级方法:" + methodName);
            }
        }
        return method;

    }


    private Method getMethod(Class clz, String methodName, Class<?>[] paramTypes) {
        try {
            return clz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            log.warn("类{}中没有找到方法:{}", clz, methodName);
            return null;
        }
    }

    private Class<?>[] copyParamsWithException(RestyCommand restyCommand) {
        Method serviceMethod = restyCommand.getServiceMethod();
        if (serviceMethod.getParameterTypes().length == 0) {
            return new Class[]{RestyException.class};
        }

        Class<?>[] paramTypes = new Class[serviceMethod.getParameterTypes().length + 1];
        paramTypes[0] = RestyException.class;
        System.arraycopy(serviceMethod.getParameterTypes(), 0, paramTypes, 1, serviceMethod.getParameterTypes().length);
        return paramTypes;
    }

    private Object[] copyArgsWithException(RestyCommand restyCommand) {
        if (restyCommand.getArgs() == null || restyCommand.getArgs().length == 0) {
            return new Object[]{restyCommand.getFailException()};
        }

        Object[] args = new Object[restyCommand.getArgs().length + 1];
        args[0] = restyCommand.getFailException();
        System.arraycopy(restyCommand.getArgs(), 0, args, 1, restyCommand.getArgs().length);
        return args;
    }

}
