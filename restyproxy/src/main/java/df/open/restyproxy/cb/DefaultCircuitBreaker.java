package df.open.restyproxy.cb;

import df.open.restyproxy.command.RestyCommand;
import df.open.restyproxy.event.EventConsumer;
import df.open.restyproxy.lb.ServerInstance;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

/**
 * Created by darrenfu on 17-7-23.
 */
@ToString
public class DefaultCircuitBreaker implements CircuitBreaker, EventConsumer {

    private String eventKey;

    public DefaultCircuitBreaker() {
        this.eventKey = "COMMAND_FINISH#" + UUID.randomUUID().toString();
        this.registerEvent();
    }

    @Override
    public boolean shouldBreak(RestyCommand restyCommand, ServerInstance serverInstance) {
        return false;
    }

    @Override
    public List<String> getBrokenServer() {
        return null;
    }

    @Override
    public void update(RestyCommand restyCommand) {

    }

    private void registerEvent() {
        this.on(this.eventKey, (command) -> {
            if (command instanceof RestyCommand) {
                System.out.println("event get");
                System.out.println(command);
                update((RestyCommand) command);
            }
        });
    }

    @Override
    public String getEventKey() {
        return this.eventKey;
    }
}
