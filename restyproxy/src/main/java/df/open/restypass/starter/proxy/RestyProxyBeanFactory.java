package df.open.restypass.starter.proxy;

import df.open.restypass.command.RestyCommandContext;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;

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
@Data
public class RestyProxyBeanFactory implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private Class<?> type;

    private RestyCommandContext restyCommandContext;

    private ApplicationContext applicationContext;

    @Override
    public Object getObject() throws Exception {
        return createProxy(type, restyCommandContext);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    protected Object createProxy(Class type, RestyCommandContext restyCommandContext) {
        Object proxy = null;
        try {
            RestyProxyInvokeHandler interfaceIvkHandler = new RestyProxyInvokeHandler(restyCommandContext, commandExecutor, fallbackExecutor);
            proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, interfaceIvkHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proxy;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(type, "type不能为空");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
