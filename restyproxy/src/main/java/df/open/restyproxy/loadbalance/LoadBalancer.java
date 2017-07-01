package df.open.restyproxy.loadbalance;

import df.open.restyproxy.proxy.RestyCommand;

/**
 * Created by darrenfu on 17-6-26.
 */
public interface LoadBalancer {

    /**
     * 负载均衡，服务路由
     *
     * @param context the context
     * @param command the command
     * @return the server instance
     */
    ServerInstance choose(ServerContext context, RestyCommand command);
}
