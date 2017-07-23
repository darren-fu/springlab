package df.open.restyproxy.command;

import df.open.restyproxy.base.RestyRequestTemplate;
import df.open.restyproxy.event.EventEmit;
import df.open.restyproxy.lb.ServerInstance;
import org.asynchttpclient.uri.Uri;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Resty请求命令
 * Created by darrenfu on 17-6-26.
 */
public interface RestyCommand extends RestyCommandLifeCycle, EventEmit {

    /**
     * HTTP
     */
    String HTTP = "http";
    /**
     * HTTPS
     */
    String HTTPS = "https";

    /**
     * RestyCommand的请求路径 （eg. /resty/user/get）
     *
     * @return the path
     */
    String getPath();

    /**
     * RestyCommand的请求方式（GET/POST）
     *
     * @return the http method
     */
    String getHttpMethod();

    /**
     * RestyCommand对应的服务名称
     *
     * @return the service name
     */
    String getServiceName();

    /**
     * RestyCommand对应的method
     *
     * @return the service method
     */
    Method getServiceMethod();

    /**
     * 获取RestyCommand的返回类型
     *
     * @return the return type
     */
    Type getReturnType();

    /**
     * 获取RestyCommand的请求参数
     *
     * @return the object [ ]
     */
    Object[] getArgs();

    /**
     * 获取Command的配置
     *
     * @return the resty command config
     */
    RestyCommandConfig getRestyCommandConfig();

    /**
     * 获取请求的URI
     *
     * @param serverInstance the server instance
     * @return the uri
     */
    Uri getUri(ServerInstance serverInstance);

    /**
     * 获取关联的请求模板
     *
     * @return the request template
     */
    RestyRequestTemplate getRequestTemplate();


}
