package df.open.restyproxy.cb;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 断路器 工厂类
 * Created by darrenfu on 17-7-25.
 */
public class CircuitBreakerFactory {

    private static ConcurrentHashMap<String, CircuitBreaker> breakerMap = new ConcurrentHashMap<>();


    /**
     * 获取 默认 断路器，划分维度为 service
     *
     * @param serviceName the service name
     * @return the circuit breaker
     */
    public static CircuitBreaker defaultCircuitBreaker(String serviceName) {
        CircuitBreaker circuitBreaker = breakerMap.get(serviceName);
        if (circuitBreaker == null) {
            circuitBreaker = new DefaultCircuitBreaker();
            CircuitBreaker existBreaker = breakerMap.putIfAbsent(serviceName, circuitBreaker);
            if (existBreaker != null) {
                circuitBreaker = existBreaker;
            }
            circuitBreaker.start();
        }
        return circuitBreaker;
    }

}
