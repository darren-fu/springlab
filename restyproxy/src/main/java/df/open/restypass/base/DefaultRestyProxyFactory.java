package df.open.restypass.base;

import df.open.restypass.command.RestyCommandContext;
import df.open.restypass.executor.CommandExecutor;
import df.open.restypass.lb.server.ServerContext;

/**
 * 默认工厂类
 * Created by darrenfu on 17-7-27.
 */
public class DefaultRestyProxyFactory implements RestyProxyFactory {
    @Override
    public RestyCommandContext getRestyCommandContext() {
        return null;
    }

    @Override
    public ServerContext getServerContext() {
        return null;
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return null;
    }
}
