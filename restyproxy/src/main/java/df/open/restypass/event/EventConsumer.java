package df.open.restypass.event;

import java.util.function.Consumer;

/**
 * event consumer
 * Created by darrenfu on 17-7-23.
 */
@SuppressWarnings("unused")
public interface EventConsumer {

    /**
     * 注册事件.
     *
     * @param event    the event
     * @param consumer the consumer
     */
    default void on(String event, Consumer consumer) {
        EventBus.registerEventAndConsumer(event, consumer);
    }


    /**
     * 注册一次性事件.
     *
     * @param event    the event
     * @param consumer the consumer
     */
    default void once(String event, Consumer consumer) {
        System.out.println("once event");
    }

    /**
     * 获取事件key
     *
     * @return the event key
     */
    String getEventKey();



}
