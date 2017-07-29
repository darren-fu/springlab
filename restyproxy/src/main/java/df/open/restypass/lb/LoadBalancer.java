package df.open.restypass.lb;

import df.open.restypass.command.RestyCommand;
import df.open.restypass.lb.server.ServerContext;
import df.open.restypass.lb.server.ServerInstance;

import java.util.Set;

/**
 * 负载均衡接口
 * Created by darrenfu on 17-6-26.
 */
public interface LoadBalancer {

    /**
     * 负载均衡，服务路由
     *
     * @param context           服务实例Context
     * @param command           Resty命令
     * @param excludeInstanceIdList 排除的server instance Id列表，如，重试时需要排除被选择过的服务实例
     * @return the server instance
     */
    ServerInstance choose(ServerContext context, RestyCommand command, Set<String> excludeInstanceIdList);
}
