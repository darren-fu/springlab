package df.open.restyproxy.command;

import df.open.restyproxy.command.refresh.ConfigRefresh;
import df.open.restyproxy.command.refresh.RefreshCommandConfig;

/**
 * Resty请求的配置
 * Created by darrenfu on 17-6-21.
 */
public interface RestyCommandConfig extends ConfigRefresh<RefreshCommandConfig> {

    String getServiceName();

    void setServiceName(String serviceName);

    boolean isAsyncEnabled();

    boolean isCircuitBreakEnabled();

    boolean isForceBreakEnabled();

    boolean isFallbackEnabled();

    Class getFallbackClass();

    String getFallbackBean();

    int getRetry();

    void setAsyncEnabled(boolean asyncEnabled);

    void setCircuitBreakEnabled(boolean enableCircuitBreak);

    void setForceBreakEnabled(boolean forceBreakEnabled);

    void setFallbackEnabled(boolean enableFallback);

    void setFallbackClass(Class fallbackClass);

    void setFallbackBean(String fallbackBean);

    void setRetry(int retry);


    class DefaultRestyCommandConfig implements RestyCommandConfig {

        private String serviceName;

        private boolean asyncEnabled = false;

        private boolean circuitBreakEnabled = true;

        private boolean forceBreakEnabled = false;

        private boolean fallbackEnabled = true;

        private Class fallbackClass;

        private String fallbackBean;

        private int retry = 1;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public boolean isAsyncEnabled() {
            return asyncEnabled;
        }

        public boolean isCircuitBreakEnabled() {
            return circuitBreakEnabled;
        }

        public boolean isFallbackEnabled() {
            return fallbackEnabled;
        }

        public Class getFallbackClass() {
            return fallbackClass;
        }

        @Override
        public String getFallbackBean() {
            return fallbackBean;
        }

        public int getRetry() {
            return retry;
        }

        public void setAsyncEnabled(boolean asyncEnabled) {
            this.asyncEnabled = asyncEnabled;
        }

        public void setCircuitBreakEnabled(boolean enableCircuitBreak) {
            this.circuitBreakEnabled = enableCircuitBreak;
        }

        public void setFallbackEnabled(boolean enableFallback) {
            this.fallbackEnabled = enableFallback;
        }

        public void setFallbackClass(Class fallbackClass) {
            this.fallbackClass = fallbackClass;
        }

        @Override
        public void setFallbackBean(String fallbackBean) {
            this.fallbackBean = fallbackBean;
        }

        public void setRetry(int retry) {
            this.retry = retry;
        }


        public boolean isForceBreakEnabled() {
            return forceBreakEnabled;
        }

        public void setForceBreakEnabled(boolean forceBreakEnabled) {
            this.forceBreakEnabled = forceBreakEnabled;
        }


        @Override
        public boolean refresh(RefreshCommandConfig refreshCommandConfig) {

            if (refreshCommandConfig.getCircuitBreakEnabled() != null) {
                this.setCircuitBreakEnabled(refreshCommandConfig.getCircuitBreakEnabled());
            }
            if (refreshCommandConfig.getFallbackEnabled() != null) {
                this.setFallbackEnabled(refreshCommandConfig.getFallbackEnabled());
            }
            return true;
        }
    }
}
