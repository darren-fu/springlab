package df.open.restypass.event;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 事件总线
 * Created by darrenfu on 17-7-23.
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
public class EventBus {

    private static ConcurrentHashMap<String, List<Consumer>> eventMap = new ConcurrentHashMap<>();

    /**
     * 注册事件和消费者
     *
     * @param event    the event
     * @param consumer the consumer
     */
    static void registerEventAndConsumer(String event, Consumer consumer) {
        // 不存在 event
        if (!eventMap.containsKey(event)) {
            //
            List<Consumer> consumerList = new CopyOnWriteArrayList<>();
            consumerList.add(consumer);
            // 新增
            List<Consumer> existConsumers = eventMap.putIfAbsent(event, consumerList);
            if (existConsumers != null) {
                // 并发，新增
                existConsumers.add(consumer);
            }
        } else {
            // event已存在
            List<Consumer> consumerList = eventMap.get(event);
            if (consumerList != null) {
                consumerList.add(consumer);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("注册事件：{}成功", event);
        }
    }

    /**
     * 消费事件
     *
     * @param <T>   the type parameter
     * @param event the event
     * @param obj   the obj
     */
    @SuppressWarnings("unchecked")
    static <T> void emitEvent(String event, T obj) {

        List<Consumer> consumers = eventMap.get(event);
        if (consumers != null && eventMap.size() > 0) {
            for (Consumer consumer : consumers) {
                consumer.accept(obj);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("消费事件:{},参数:{}", event, obj);
        }
    }

}
