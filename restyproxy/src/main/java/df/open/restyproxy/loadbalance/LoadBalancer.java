package df.open.restyproxy.loadbalance;

import df.open.restyproxy.proxy.RestyCommand;

import java.util.List;

/**
 * 负载均衡接口
 * Created by darrenfu on 17-6-26.
 */
public interface LoadBalancer {

    /**
     * 负载均衡，服务路由
     *
     * @param context            服务实例Context
     * @param command            Resty命令
     * @param excludeServerList 排除的server列表，如，重试时需要排除被选择过的服务实例
     * @return the server instance
     */
    ServerInstance choose(ServerContext context, RestyCommand command, List<ServerInstance> excludeServerList);
}
