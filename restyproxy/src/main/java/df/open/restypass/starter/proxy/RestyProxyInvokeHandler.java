package df.open.restypass.starter.proxy;

import df.open.restypass.command.RestyCommandContext;
import df.open.restypass.command.DefaultRestyCommand;
import df.open.restypass.command.RestyCommand;
import df.open.restypass.executor.FallbackExecutor;
import df.open.restypass.executor.RestyCommandExecutor;
import df.open.restypass.executor.CommandExecutor;
import df.open.restypass.exception.RestyException;
import df.open.restypass.executor.RestyFallbackExecutor;
import df.open.restypass.lb.LoadBalancer;
import df.open.restypass.lb.LoadBalanceBuilder;
import df.open.restypass.lb.server.ServerContext;
import df.open.restypass.lb.server.ServerContextBuilder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理类执行器
 *
 * @author darren-fu
 * @version 1.0.0
 * @contact 13914793391
 * @date 2016/11/22
 */
@Slf4j
public class RestyProxyInvokeHandler implements InvocationHandler {


    private RestyCommandContext restyCommandContext;

    private CommandExecutor commandExecutor;

    private FallbackExecutor fallbackExecutor;

    public RestyProxyInvokeHandler(RestyCommandContext restyCommandContext,
                                   CommandExecutor commandExecutor,
                                   FallbackExecutor fallbackExecutor) {
        this.restyCommandContext = restyCommandContext;
        this.commandExecutor = commandExecutor;
        this.fallbackExecutor = fallbackExecutor;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isSpecialMethod(method)) {
            return handleSpecialMethod(proxy, method, args);
        }

        Object result;

        RestyCommand restyCommand = new DefaultRestyCommand(method.getDeclaringClass(),
                method,
                method.getGenericReturnType(),
                args,
                restyCommandContext);

        ServerContext serverContext = ServerContextBuilder.createConfigurableServerContext();
        LoadBalancer loadBalancer = LoadBalanceBuilder.createRandomLoadBalancer();
        CommandExecutor commandExecutor = new RestyCommandExecutor(restyCommandContext, serverContext);
        RestyFallbackExecutor restyFallbackExecutor = new RestyFallbackExecutor();
        try {
            if (commandExecutor.executable(restyCommand)) {
                result = commandExecutor.execute(loadBalancer, restyCommand);
            } else {
                throw new IllegalStateException("Resty command is suitable:" + restyCommand);
            }
        } catch (RestyException ex) {
            log.error("请求发生异常:", ex);
            if (restyFallbackExecutor.executable(restyCommand)) {
                result = restyFallbackExecutor.execute(restyCommand);
            } else {
                throw ex;
            }
        }
        return result;
    }

    private boolean isSpecialMethod(Method method) {
        return "equals".equals(method.getName())
                || "hashCode".equals(method.getName())
                || "toString".equals(method.getName());
    }

    private Object handleSpecialMethod(Object proxy, Method method, Object[] args) {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler =
                        args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return hashCode();
        } else if ("toString".equals(method.getName())) {
            return toString();
        }
        return null;

    }

}
