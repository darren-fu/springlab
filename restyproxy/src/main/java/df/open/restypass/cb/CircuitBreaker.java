package df.open.restypass.cb;

import df.open.restypass.command.RestyCommand;
import df.open.restypass.event.EventConsumer;
import df.open.restypass.lb.server.ServerInstance;

import java.util.Set;

/**
 * 断路器
 * Created by darrenfu on 17-7-22.
 */
@SuppressWarnings("unused")
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

}
