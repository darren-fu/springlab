package df.open.restyproxy.core;

import df.open.restyproxy.lb.LoadBalancer;
import df.open.restyproxy.command.RestyCommand;

/**
 * 执行器
 * Created by darrenfu on 17-7-1.
 */
public interface CommandExecutor {


    /**
     * Executable boolean.
     *
     * @param restyCommand the resty command
     * @return the boolean
     */
    boolean executable(RestyCommand restyCommand);

    /**
     * 执行RestyCommand，返回结果
     *
     * @param lb           the lb
     * @param restyCommand the resty command
     * @return the t
     */
    Object execute(LoadBalancer lb, RestyCommand restyCommand);
}
