package df.open.restyproxy.starter.proxy;

import df.open.restyproxy.command.RestyCommandContext;
import df.open.restyproxy.command.DefaultRestyCommand;
import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.core.RestyCommandExecutor;
import df.open.restyproxy.core.CommandExecutor;
import df.open.restyproxy.exception.RestyException;
import df.open.restyproxy.core.FallbackExecutor;
import df.open.restyproxy.http.converter.JsonResponseConverter;
import df.open.restyproxy.http.converter.ResponseConverter;
import df.open.restyproxy.http.converter.StringResponseConverter;
import df.open.restyproxy.lb.LoadBalancer;
import df.open.restyproxy.lb.LoadBalanceBuilder;
import df.open.restyproxy.lb.server.ServerContext;
import df.open.restyproxy.lb.server.ServerContextBuilder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * 说明:
 * <p/>
 * Copyright: Copyright (c)
 * <p/>
 * Company:
 * <p/>
 *
 * @author darren-fu
 * @version 1.0.0
 * @contact 13914793391
 * @date 2016/11/22
 */
@Slf4j
public class RestyProxyInvokeHandler implements InvocationHandler {


    private RestyCommandContext restyCommandContext;


    public RestyProxyInvokeHandler(RestyCommandContext restyCommandContext) {
        this.restyCommandContext = restyCommandContext;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isSpecialMethod(method)) {
            return handleSpecialMethod(proxy, method, args);
        }

        Object result = null;

        RestyCommand restyCommand = new DefaultRestyCommand(method.getDeclaringClass(),
                method,
                method.getGenericReturnType(),
                args,
                restyCommandContext);

        ServerContext serverContext = ServerContextBuilder.createConfigurableServerContext();
        LoadBalancer loadBalancer = LoadBalanceBuilder.createRandomLoadBalancer();
        CommandExecutor commandExecutor = new RestyCommandExecutor(restyCommandContext, serverContext);
        FallbackExecutor fallbackExecutor = new FallbackExecutor();
        try {
            if (commandExecutor.executable(restyCommand)) {
                result = commandExecutor.execute(loadBalancer, restyCommand);
            } else {
                throw new IllegalStateException("Resty command is suitable:" + restyCommand);
            }
        } catch (RestyException ex) {
            log.error("请求发生异常:", ex);
            if (fallbackExecutor.executable(restyCommand)) {
                return fallbackExecutor.execute(loadBalancer, restyCommand);
            } else {
                throw ex;
            }
        }


        return result;

        //TODO method返回基本类型，如果返回的是null会报错，待验证
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
