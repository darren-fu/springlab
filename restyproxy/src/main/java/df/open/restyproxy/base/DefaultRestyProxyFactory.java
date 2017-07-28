package df.open.restyproxy.base;

import df.open.restyproxy.command.RestyCommandContext;
import df.open.restyproxy.core.CommandExecutor;
import df.open.restyproxy.lb.server.ServerContext;

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
