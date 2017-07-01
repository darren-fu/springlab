package df.open.restyproxy.core;

import df.open.restyproxy.loadbalance.LoadBalancer;
import df.open.restyproxy.proxy.RestyCommand;

/**
 * 执行器
 * Created by darrenfu on 17-7-1.
 */
public interface CommandExecutor {


    /**
     * 执行RestyCommand，返回结果
     *
     * @param <T>          the type parameter
     * @param lb           the lb
     * @param restyCommand the resty command
     * @return the t
     */
    <T> T execute(LoadBalancer lb, RestyCommand restyCommand);
}
