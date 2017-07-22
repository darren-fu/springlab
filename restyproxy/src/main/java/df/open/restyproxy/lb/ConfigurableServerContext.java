package df.open.restyproxy.lb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by darrenfu on 17-6-28.
 */
public class ConfigurableServerContext implements ServerContext {

    private static ConcurrentHashMap<LoadBalancer, Boolean> needUpdateServerMap = new ConcurrentHashMap<>();

    @Override
    public List<ServerInstance> getAllServerList() {
        return getServer(null);
    }

    @Override
    public List<ServerInstance> getServerList(String serviceName) {
        return getServer(serviceName);
    }


    private List<ServerInstance> getServer(String serviceName) {
        List<ServerInstance> list = new ArrayList<>();
        list.add(ServerInstance.buildInstance(serviceName, "localhost", 9201));
        list.add(ServerInstance.buildInstance(serviceName, "localhost", 9202));

        return list;
    }

    @Override
    public void updateServerList() {

    }

    @Override
    public void updateServerList(String serviceName) {
        for (LoadBalancer loadBalancer : needUpdateServerMap.keySet()) {
            needUpdateServerMap.put(loadBalancer, true);
        }
    }

    @Override
    public boolean hasServerUpdated(LoadBalancer loadBalancer) {
        Boolean needUpdate = needUpdateServerMap.getOrDefault(loadBalancer, true);
        if (needUpdate) {
            needUpdateServerMap.put(loadBalancer, false);
            return true;
        }

        return false;
    }
}
