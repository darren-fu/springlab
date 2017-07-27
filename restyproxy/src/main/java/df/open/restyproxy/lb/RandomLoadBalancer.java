package df.open.restyproxy.lb;

import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.lb.server.ServerContext;
import df.open.restyproxy.lb.server.ServerInstance;

import java.util.List;
import java.util.Set;

/**
 * 负载均衡
 * 随机算法
 * Created by darrenfu on 17-6-28.
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public ServerInstance choose(ServerContext context, RestyCommand command, Set<String> excludeInstanceIdList) {
        List<ServerInstance> serverList = context.getServerList(command.getServiceName());

        return serverList.get(0);
    }
}
