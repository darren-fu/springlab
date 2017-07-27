package df.open.restyproxy.cb;

import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.event.EventConsumer;
import df.open.restyproxy.lb.server.ServerInstance;

import java.util.Set;

/**
 * 断路器
 * Created by darrenfu on 17-7-22.
 */
public interface CircuitBreaker extends EventConsumer {

    /**
     * 启动断路器.
     */
    void start();

    /**
     * 停止断路器.
     */
    void end();

    /**
     * 是否通过
     *
     * @param restyCommand   the resty command
     * @param serverInstance the server instance
     * @return the boolean
     */
    boolean shouldPass(RestyCommand restyCommand, ServerInstance serverInstance);

    /**
     * 获取短路的server instance id 列表
     *
     * @return the broken server
     */
    Set<String> getBrokenServer();

    /**
     * The enum BreakerStatus.
     */
    enum BreakerStatus {
        /**
         * Open status.
         */
        OPEN(),
        /**
         * Break status.
         */
        BREAK(),
        /**
         * HalfOpen status.
         */
        HALF_OPEN(),

        /**
         * 强制短路
         */
        FORCE_BREAK()


    }

}
