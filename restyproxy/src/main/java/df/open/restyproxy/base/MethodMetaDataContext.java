package df.open.restyproxy.base;

import df.open.restyproxy.annotation.RestyMethod;
import df.open.restyproxy.annotation.RestyService;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by darrenfu on 17-6-21.
 */
public class MethodMetaDataContext {


    private static ConcurrentHashMap<String, RestyService> serviceMetaDataMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Method, RestyMethod> methodMetaDataMap = new ConcurrentHashMap<>();


    public static void main(String[] args) {
    }


    public static void storeRestyService(String clzName, RestyService restyService) {
        serviceMetaDataMap.putIfAbsent(clzName, restyService);
    }

    public static void storeRestyMethod(Method method, RestyMethod restyMethod) {
        methodMetaDataMap.putIfAbsent(method, restyMethod);
    }


    public static RestyService getRestyService(String clzName) {
        return serviceMetaDataMap.get(clzName);
    }


}
