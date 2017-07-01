package df.open.restyproxy.proxy;

import df.open.restyproxy.base.RestyCommandContext;
import df.open.restyproxy.base.RestyRequestTemplate;
import lombok.Data;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Request;
import org.asynchttpclient.uri.Uri;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;

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


    private String path;

    private String method;

    private Class serviceClz;

    private Method serviceMethod;

    private Type returnType;

    private Object[] args;

    private RestyCommandContext context;

    private String serviceName;

    private RestyCommandConfig restyCommandConfig;


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

        if (context.getCommandProperties(serviceMethod) == null) {
            throw new RuntimeException("缺少CommandProperties，无法初始化RestyCommand");
        }

        RestyCommandConfig commandProperties = context.getCommandProperties(serviceMethod);
        this.serviceName = commandProperties.getServiceName();
        this.restyCommandConfig = commandProperties;
        RestyRequestTemplate requestTemplate = context.getRequestTemplate(serviceMethod);


    }


}
