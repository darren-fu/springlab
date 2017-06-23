package df.open.restyproxy.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import df.open.restyproxy.base.RestyProxyProperties;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;

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
public class DefaultRestyProxyInvokeHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isSpecialMethod(method)) {
            return handleSpecialMethod(proxy, method, args);
        }



        System.out.println("method.getClass():" + method.getClass());
        System.out.println("method.getName():" + method.getName());
        System.out.println("method.getGenericReturnType():" + method.getGenericReturnType());
        System.out.println("method.getParameterCount():" + method.getParameterCount());
        System.out.println("method.getParameterTypes():" + Arrays.toString(method.getParameterTypes()));
        System.out.println("method.getParameters():" + Arrays.toString(method.getParameters()));

        System.out.println("method.getDeclaredAnnotations():" + Arrays.toString(method.getDeclaredAnnotations()));
        Type returnType = method.getGenericReturnType();
        // SpringMvcContract
        for (Object arg : args) {
            System.out.println("arg.annotation: " + Arrays.toString(arg.getClass().getDeclaredAnnotations()));
        }

        System.out.println("###########################################");
        return handleReturnValue(returnType);
    }



    private Object handleReturnValue(Type returnType) {
        String typeName = returnType.getTypeName();
        ObjectMapper objectMapper = RestyProxyProperties.getDefaultProperties().getObjectMapper();


        switch (typeName) {
            case "java.lang.Object":
                return new Object();
            case "void":
                return null;
            case "int":
                return 0;
            case "java.lang.Integer":
                return 0;
            case "java.lang.String":
                return "result";
            default:
                return null;
        }
    }







    private boolean isSpecialMethod(Method method) {
        return "equals".equals(method.getName())
                || "hashCode".equals(method.getName())
                || "toString".equals(method.getName());
    }

    private Object handleSpecialMethod(Object proxy, Method method, Object[] args) {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler =
                        args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return hashCode();
        } else if ("toString".equals(method.getName())) {
            return toString();
        }
        return null;

    }

}
