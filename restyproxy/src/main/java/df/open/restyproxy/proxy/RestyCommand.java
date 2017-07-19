package df.open.restyproxy.proxy;

import df.open.restyproxy.base.RestyRequestTemplate;
import df.open.restyproxy.loadbalance.ServerInstance;
import org.asynchttpclient.uri.Uri;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;

/**
 * Created by darrenfu on 17-6-26.
 */
public interface RestyCommand {

    String HTTP = "http";
    String HTTPS = "https";

    String getPath();

    String getHttpMethod();

    String getServiceName();

    Method getServiceMethod();

    Type getReturnType();

    Object[] getArgs();

    RestyCommandConfig getRestyCommandConfig();

    Uri getUri(ServerInstance serverInstance);

    RestyRequestTemplate getRequestTemplate();
}
