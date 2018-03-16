package df.open.restypass.lb.server;

import df.open.restypass.lb.LoadBalancer;

import java.util.List;

/**
 * Created by darrenfu on 17-6-26.
 */
public interface ServerContext {

    List<ServerInstance> getAllServerList();

    List<ServerInstance> getServerList(String serviceName);

    void updateServerList();

    void updateServerList(String serviceName);

    boolean hasServerUpdated(LoadBalancer loadBalancer);
}
