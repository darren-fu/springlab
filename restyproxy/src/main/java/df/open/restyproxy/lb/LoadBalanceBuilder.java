package df.open.restyproxy.lb;

/**
 * Created by darrenfu on 17-6-28.
 */
public class LoadBalanceBuilder {

    /**
     * Create random load balancer load balancer.
     *
     * @return the load balancer
     */
    public static LoadBalancer createRandomLoadBalancer() {
        return new RandomLoadBalancer();
    }

}
