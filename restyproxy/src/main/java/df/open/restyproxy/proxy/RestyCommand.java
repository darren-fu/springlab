package df.open.restyproxy.proxy;

import lombok.Data;
import org.asynchttpclient.Request;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by darrenfu on 17-6-20.
 */
@Data
public class RestyCommand {

    private String serviceName;

    private Method method;

    private Type returnType;

    private Object[] args;

    private Request request;

    private RestyCommandProperties commandProperties;

}
