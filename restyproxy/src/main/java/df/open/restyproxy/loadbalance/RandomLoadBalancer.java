package df.open.restyproxy.loadbalance;

import df.open.restyproxy.proxy.RestyCommand;

/**
 * Created by darrenfu on 17-6-28.
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public ServerInstance choose(ServerContext context, RestyCommand command) {
        return null;
    }
}
