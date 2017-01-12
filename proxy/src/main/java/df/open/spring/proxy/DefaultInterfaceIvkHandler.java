package df.open.spring.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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
public class DefaultInterfaceIvkHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("method.getClass():" + method.getClass());
        System.out.println("method.getName():" + method.getName());
        System.out.println("method.getGenericReturnType():" + method.getGenericReturnType());
        System.out.println("method.getParameterCount():" + method.getParameterCount());
        System.out.println("method.getParameterTypes():" + Arrays.toString(method.getParameterTypes()));
        System.out.println("method.getParameters():" + Arrays.toString(method.getParameters()));

        Type returnType = method.getGenericReturnType();
        System.out.println("###########################################");
        return handleReturnValue(returnType);
    }


    private Object handleReturnValue(Type returnType) {
        String typeName = returnType.getTypeName();
        System.out.println("typeName:" + typeName);

        switch (typeName) {
            case "java.lang.Object":
                return new Object();
            case "void":
                return null;
            case "int":
                return 0;
            case "java.lang.Integer" :
                return 0;
            case "java.lang.String":
                return "result";
            default:
                return null;
        }
    }

    public static void main(String[] args) {


    }
}
