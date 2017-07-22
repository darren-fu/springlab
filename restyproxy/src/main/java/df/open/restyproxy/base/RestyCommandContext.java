package df.open.restyproxy.base;

import df.open.restyproxy.annotation.RestyMethod;
import df.open.restyproxy.annotation.RestyService;
import df.open.restyproxy.annotation.processor.RestyMethodProcessor;
import df.open.restyproxy.annotation.processor.RestyServiceProcessor;
import df.open.restyproxy.http.client.HttpClientHolder;
import df.open.restyproxy.http.config.AsyncHttpConfigFactory;
import df.open.restyproxy.command.RestyCommandConfig;
import df.open.restyproxy.wrapper.spring.SpringAnnotationWrapper;
import org.asynchttpclient.AsyncHttpClient;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RestyCommand所依赖的Context
 * 存储各种依赖数据
 * Created by darrenfu on 17-6-21.
 */
public class RestyCommandContext {

    // 服务与注解 map
    private ConcurrentHashMap<Class, RestyService> serviceMetaDataMap;

    // method与注解 map
    private ConcurrentHashMap<Method, RestyMethod> methodMetaDataMap;

    // method与commandProperties map
    private ConcurrentHashMap<Method, RestyCommandConfig> commandPropertiesMap;
    // serviceName与HttpClient map
    private ConcurrentHashMap<String, HttpClientHolder> httpClientPool;

    private ConcurrentHashMap<Method, RestyRequestTemplate> requestTemplateMap;
    // 注解处理器
    private RestyServiceProcessor serviceProcessor;
    private RestyMethodProcessor methodProcessor;

    private RestyCommandContext() {
        serviceMetaDataMap = new ConcurrentHashMap<>();
        methodMetaDataMap = new ConcurrentHashMap<>();
        commandPropertiesMap = new ConcurrentHashMap<>();
        serviceProcessor = new RestyServiceProcessor();
        methodProcessor = new RestyMethodProcessor();
        requestTemplateMap = new ConcurrentHashMap<>();
        httpClientPool = new ConcurrentHashMap<>();
    }

    /**
     * instance
     */
    private static RestyCommandContext commandContext = new RestyCommandContext();

    /**
     * 获取实例
     *
     * @return the instance
     */
    public static RestyCommandContext getInstance() {
        return commandContext;
    }


    /**
     * Store resty service.
     *
     * @param clz          the clz
     * @param restyService the resty service
     */
    private void storeRestyService(Class clz, RestyService restyService) {
        serviceMetaDataMap.putIfAbsent(clz, restyService);
    }

    /**
     * Store resty serviceMethod.
     *
     * @param method      the serviceMethod
     * @param restyMethod the resty serviceMethod
     */
    private void storeRestyMethod(Method method, RestyMethod restyMethod) {
        methodMetaDataMap.putIfAbsent(method, restyMethod);
    }


    /**
     * 为service创建Resty配置
     *
     * @param serviceClz the service clz
     */
    public void initContextForService(Class serviceClz) {
        RestyService restyService = (RestyService) serviceClz.getDeclaredAnnotation(RestyService.class);
        if (restyService == null) {
            return;
        }
        this.storeRestyService(serviceClz, restyService);
        HttpClientHolder clientHolder = new HttpClientHolder(AsyncHttpConfigFactory.getConfig());
        httpClientPool.putIfAbsent(restyService.serviceName(), clientHolder);


        SpringAnnotationWrapper wrapper = new SpringAnnotationWrapper();

        for (Method method : serviceClz.getMethods()) {
            //存储 httpMethod 和 restyCommandConfig
            RestyCommandConfig commandProperties = processRestyAnnotation(restyService, method);
            commandPropertiesMap.putIfAbsent(method, commandProperties);

            // 存储 httpMethod 和 requestTemplate
            RestyRequestTemplate restyRequestTemplate = wrapper.processAnnotation(serviceClz, method);
            requestTemplateMap.put(method, restyRequestTemplate);
        }
        System.out.println("RestyCommandContext初始化成功！");
    }

    /**
     * 处理Resty 注解,生成 RestyCommandConfig
     *
     * @param restyService
     * @param method
     * @return
     */
    private RestyCommandConfig processRestyAnnotation(RestyService restyService, Method method) {
        RestyCommandConfig commandProperties = new RestyCommandConfig.DefaultRestyCommandConfig();
        RestyMethod restyMethod = method.getDeclaredAnnotation(RestyMethod.class);
        if (restyMethod != null) {
            this.storeRestyMethod(method, restyMethod);
        }
        // 处理resty 注解
        serviceProcessor.processor(restyService, commandProperties);
        methodProcessor.processor(restyMethod, commandProperties);

        return commandProperties;
    }

    /**
     * Gets command properties.
     *
     * @param method the httpMethod
     * @return the command properties
     */
    public RestyCommandConfig getCommandProperties(Method method) {
        return commandPropertiesMap.get(method);
    }


    /**
     * Gets resty service.
     *
     * @param clz the clz
     * @return the resty service
     */
    public RestyService getRestyService(Class clz) {
        return serviceMetaDataMap.get(clz);
    }


    /**
     * Gets http client.
     *
     * @param serviceName the service name
     * @return the http client
     */
    public AsyncHttpClient getHttpClient(String serviceName) {
        HttpClientHolder clientHolder = httpClientPool.get(serviceName);
        if (clientHolder == null) {
            throw new RuntimeException("获取http client失败");
        }
        return clientHolder.getClient();
    }


    /**
     * Gets request template.
     *
     * @param restyMethod the resty httpMethod
     * @return the request template
     */
    public RestyRequestTemplate getRequestTemplate(Method restyMethod) {
        return requestTemplateMap.get(restyMethod);
    }

}
