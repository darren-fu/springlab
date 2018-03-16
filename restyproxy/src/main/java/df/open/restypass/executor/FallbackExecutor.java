package df.open.restypass.executor;

import df.open.restypass.command.RestyCommand;

/**
 * 降级服务执行器
 * Created by darrenfu on 17-7-28.
 */
public interface FallbackExecutor {
    
    /**
     * Executable boolean.
     *
     * @param restyCommand the resty command
     * @return the boolean
     */
    boolean executable(RestyCommand restyCommand);

    /**
     * 执行RestyCommand降级服务，返回结果
     *
     * @param restyCommand the resty command
     * @return the t
     */
    Object execute(RestyCommand restyCommand);
}
