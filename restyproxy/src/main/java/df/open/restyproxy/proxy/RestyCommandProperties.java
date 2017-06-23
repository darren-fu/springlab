package df.open.restyproxy.proxy;

/**
 * Created by darrenfu on 17-6-21.
 */
public interface RestyCommandProperties {
    boolean isEnableAsync();

    boolean isEnableCircuitBreak();

    boolean isEnableFallback();

    Class getFallbackClass();

    int getRetry();


    class DefaultRestyCommandProperties implements RestyCommandProperties {

        private boolean enableAsync = false;

        private boolean enableCircuitBreak = false;

        private boolean enableFallback = false;

        private Class fallbackClass;

        private int retry = 2;


        public boolean isEnableAsync() {
            return enableAsync;
        }

        public boolean isEnableCircuitBreak() {
            return enableCircuitBreak;
        }

        public boolean isEnableFallback() {
            return enableFallback;
        }

        public Class getFallbackClass() {
            return fallbackClass;
        }

        public int getRetry() {
            return retry;
        }
    }
}
