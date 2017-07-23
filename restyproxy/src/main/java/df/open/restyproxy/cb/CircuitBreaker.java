package df.open.restyproxy.cb;

import df.open.restyproxy.lb.ServerInstance;
import df.open.restyproxy.command.RestyCommand;

import java.util.List;

/**
 * Created by darrenfu on 17-7-22.
 */
public interface CircuitBreaker {

    boolean shouldBreak(RestyCommand restyCommand, ServerInstance serverInstance);

    List<String> getBrokenServer();

    void update(RestyCommand restyCommand);

}
