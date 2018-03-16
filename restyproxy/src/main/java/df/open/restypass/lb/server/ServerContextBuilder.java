package df.open.restypass.lb.server;

/**
 * ServerContext 工厂类
 * Created by darrenfu on 17-6-28.
 */
public class ServerContextBuilder {

    public static ServerContext createConfigurableServerContext() {
        return new ConfigurableServerContext();
    }
}
