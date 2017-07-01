package df.open.restyproxy.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;

/**
 * Created by darrenfu on 17-6-26.
 */
public interface RestyCommand {

    String getPath();

    String getMethod();

    String getServiceName();

    Method getServiceMethod();

    Type getReturnType();

    Object[] getArgs();

    RestyCommandConfig getRestyCommandConfig();

}
