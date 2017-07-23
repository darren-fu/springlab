package df.open.restyproxy.command;

import df.open.restyproxy.base.RestyCommandContext;
import df.open.restyproxy.base.RestyRequestTemplate;
import df.open.restyproxy.cb.CircuitBreaker;
import df.open.restyproxy.exception.RestyException;
import df.open.restyproxy.lb.ServerInstance;
import df.open.restyproxy.util.StringBuilderFactory;
import lombok.Data;
import org.asynchttpclient.*;
import org.asynchttpclient.uri.Uri;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * .init command
 * .build request
 * .choose server dest
 * .circuit break
 * .execute request
 * <p>
 * <p>
 * Created by darrenfu on 17-6-20.
 */
@Data
public class DefaultRestyCommand implements RestyCommand {

    /**
     * request 模板
     */
    private RestyRequestTemplate requestTemplate;

    /**
     * command请求路径 [requestTemplate]
     */
    private String path;

    /**
     * Get or Post [requestTemplate]
     */
    private String httpMethod;

    /**
     * Class
     */
    private Class serviceClz;

    /**
     * Method
     */
    private Method serviceMethod;

    /**
     * 返回类型
     */
    private Type returnType;

    /**
     * 方法参数列表
     */
    private Object[] args;

    /**
     * 上下文context
     */
    private RestyCommandContext context;

    /**
     * command 配置
     */
    private RestyCommandConfig restyCommandConfig;
    /**
     * 服务名称 [restyCommandConfig]
     */
    private String serviceName;


    private RestyCommandStatus status;

    private RestyException exception;

    private Request request;

    private CircuitBreaker circuitBreaker;

    public DefaultRestyCommand(Class serviceClz,
                               Method serviceMethod,
                               Type returnTyp,
                               Object[] args,
                               RestyCommandContext context) {
        this.serviceClz = serviceClz;
        this.serviceMethod = serviceMethod;
        this.returnType = returnTyp;
        this.args = args;
        this.context = context;
        this.status = RestyCommandStatus.INIT;

        if (context.getCommandProperties(serviceMethod) == null) {
            throw new RuntimeException("缺少CommandProperties，无法初始化RestyCommand");
        }

        this.restyCommandConfig = context.getCommandProperties(serviceMethod);
        this.serviceName = restyCommandConfig.getServiceName();

        this.requestTemplate = context.getRequestTemplate(serviceMethod);
        this.httpMethod = requestTemplate.getHttpMethod();
        this.path = requestTemplate.getPath();
    }


    @Override
    public Uri getUri(ServerInstance serverInstance) {

        return new Uri(serverInstance.isHttps() ? HTTPS : HTTP,
                null,
                serverInstance.getHost(),
                serverInstance.getPort(),
                path,
                paramsToString());

    }

    private String paramsToString() {
        if (this.requestTemplate.getParams() == null || this.requestTemplate.getParams().size() == 0) {
            return null;
        }
        Set<String> paramNames = this.requestTemplate.getParams().keySet();
        StringBuilder sb = StringBuilderFactory.DEFAULT.stringBuilder();
        for (int i = 0; i < paramNames.size(); i++) {

        }
        int i = 0;
        for (String paramName : paramNames) {
            if (i != 0) {
                sb.append("&");
            }
            Object paramValue = this.requestTemplate.getParams().getOrDefault(paramName, "");
            sb.append(paramName);
            sb.append("=");
            sb.append(paramValue);
            i = 1;
        }
        return sb.toString();
    }


    @Override
    public RestyCommandStatus getStatus() {

        return this.status;
    }

    @Override
    public RestyCommand ready(CircuitBreaker cb) {

        this.circuitBreaker = cb;
        this.status = status = RestyCommandStatus.READY;
        return this;
    }

    @Override
    public ListenableFuture<Response> start(ServerInstance instance) {
        this.status = RestyCommandStatus.STARTED;
        boolean shouldBreak = circuitBreaker.shouldBreak(this, instance);
        // TODO fallback
        AsyncHttpClient httpClient = context.getHttpClient(this.getServiceName());
        BoundRequestBuilder requestBuilder = new BoundRequestBuilder(httpClient,
                httpMethod,
                true);
        requestBuilder.setUri(getUri(instance));
        requestBuilder.setSingleHeaders(getRequestTemplate().getHeaders());
        this.request = requestBuilder.build();
        ListenableFuture<Response> future = httpClient.executeRequest(request);
        return future;
    }

    @Override
    public RestyCommand success() {
        // TODO 事件 eventEmit

        this.status = RestyCommandStatus.SUCCESS;
        circuitBreaker.update(this);
        return this;
    }

    @Override
    public RestyCommand failed(RestyException restyException) {
        this.exception = restyException;
        this.status = RestyCommandStatus.FAILED;
        circuitBreaker.update(this);
        return this;
    }


    @Override
    public Exception getFailException() {
        return null;
    }

  
}
