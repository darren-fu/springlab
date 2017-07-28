package df.open.restyproxy.event;

/**
 * event publisher
 * Created by darrenfu on 17-7-23.
 */
public interface EventEmit {

    /**
     * 发送事件.
     *
     * @param <T>   the type parameter
     * @param event the event
     * @param obj   the obj
     */
    default <T> void emit(String event, T obj) {
        EventBus.emitEvent(event, obj);
    }
}
