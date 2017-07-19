package df.open.restyproxy.loadbalance;

import df.open.restyproxy.proxy.RestyCommand;

import java.util.List;

/**
 * 负载均衡
 * 随机算法
 * Created by darrenfu on 17-6-28.
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public ServerInstance choose(ServerContext context, RestyCommand command, List<ServerInstance> excludeServerList) {
        List<ServerInstance> serverList = context.getServerList(command.getServiceName());

        return serverList.get(0);
    }
}
