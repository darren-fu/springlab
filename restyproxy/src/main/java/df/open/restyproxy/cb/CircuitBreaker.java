package df.open.restyproxy.cb;

import df.open.restyproxy.lb.ServerInstance;
import df.open.restyproxy.command.RestyCommand;

import java.util.List;

/**
 * 断路器
 * Created by darrenfu on 17-7-22.
 */
public interface CircuitBreaker {

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
    List<String> getBrokenServer();

    /**
     * The enum Status.
     */
    enum Status {
        /**
         * Open status.
         */
        OPEN(), /**
         * Break status.
         */
        BREAK(), /**
         * Halfopen status.
         */
        HALFOPEN()

    }

}
