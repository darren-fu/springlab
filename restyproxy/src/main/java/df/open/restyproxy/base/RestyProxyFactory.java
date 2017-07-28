package df.open.restyproxy.base;

import df.open.restyproxy.command.RestyCommandContext;
import df.open.restyproxy.core.CommandExecutor;
import df.open.restyproxy.lb.server.ServerContext;

/**
 * 工厂接口
 * Created by darrenfu on 17-7-27.
 */
public interface RestyProxyFactory {

    RestyCommandContext getRestyCommandContext();

    ServerContext getServerContext();

    CommandExecutor getCommandExecutor();

}
