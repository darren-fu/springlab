package df.open.restyproxy.loadbalance;

/**
 * Created by darrenfu on 17-6-28.
 */
public class ServerContextBuilder {


    public static ServerContext createConfigableServerContext() {
        return new ConfigurableServerContext();
    }
}
