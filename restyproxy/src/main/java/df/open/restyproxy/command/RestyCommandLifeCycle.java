package df.open.restyproxy.command;

import df.open.restyproxy.cb.CircuitBreaker;
import df.open.restyproxy.exception.RestyException;
import df.open.restyproxy.lb.ServerInstance;

/**
 * RestyCommand生命周期
 * Created by darrenfu on 17-7-22.
 */
public interface RestyCommandLifeCycle {

    /**
     * 获取RestyCommand执行状态
     *
     * @return the boolean
     */
    RestyCommandStatus getStatus();


    /**
     * 准备状态，设置熔断器， 申请HttpClient
     *
     * @param cb the cb
     * @return the resty command life cycle
     */
    RestyCommandLifeCycle ready(CircuitBreaker cb);

    /**
     * 开始请求.
     *
     * @param instance the instance
     * @return the listenable future
     */
    RestyFuture start(ServerInstance instance);


    /**
     * Gets instance id.
     *
     * @return the instance id
     */
    String getInstanceId();

    /**
     * RestyCommand执行成功
     *
     * @return the resty command life cycle
     */
    RestyCommandLifeCycle success();

    /**
     * RestyCommand执行失败
     *
     * @param RestyException the resty exception
     * @return the resty command life cycle
     */
    RestyCommandLifeCycle failed(RestyException RestyException);


    /**
     * 获取导致command失败异常
     *
     * @return the fail exception
     */
    RestyException getFailException();


}
