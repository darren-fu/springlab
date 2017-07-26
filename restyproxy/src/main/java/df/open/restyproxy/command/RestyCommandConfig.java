package df.open.restyproxy.command;

/**
 * Resty请求的配置
 * Created by darrenfu on 17-6-21.
 */
public interface RestyCommandConfig {

    String getServiceName();

    void setServiceName(String serviceName);

    boolean isEnableAsync();

    boolean isEnableCircuitBreak();

    boolean isEnableFallback();

    Class getFallbackClass();

    int getRetry();

    void setEnableAsync(boolean enableAsync);

    void setEnableCircuitBreak(boolean enableCircuitBreak);

    void setEnableFallback(boolean enableFallback);

    void setFallbackClass(Class fallbackClass);

    void setRetry(int retry);


    class DefaultRestyCommandConfig implements RestyCommandConfig {

        private String serviceName;

        private boolean enableAsync = false;

        private boolean enableCircuitBreak = false;

        private boolean enableFallback = false;

        private Class fallbackClass;

        private int retry = 2;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

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

        public void setEnableAsync(boolean enableAsync) {
            this.enableAsync = enableAsync;
        }

        public void setEnableCircuitBreak(boolean enableCircuitBreak) {
            this.enableCircuitBreak = enableCircuitBreak;
        }

        public void setEnableFallback(boolean enableFallback) {
            this.enableFallback = enableFallback;
        }

        public void setFallbackClass(Class fallbackClass) {
            this.fallbackClass = fallbackClass;
        }

        public void setRetry(int retry) {
            this.retry = retry;
        }
    }
}
