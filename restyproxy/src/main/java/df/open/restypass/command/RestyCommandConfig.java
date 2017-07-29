package df.open.restypass.command;

import df.open.restypass.command.update.Updater;
import df.open.restypass.command.update.UpdateCommandConfig;

/**
 * Resty请求的配置
 * Created by darrenfu on 17-6-21.
 */
@SuppressWarnings("unused")
public interface RestyCommandConfig extends Updater<UpdateCommandConfig> {

    /**
     * 获取服务名称
     *
     * @return the service name
     */
    String getServiceName();

    /**
     * 设置服务名称
     *
     * @param serviceName the service name
     */
    void setServiceName(String serviceName);

    /**
     * Is async enabled boolean.
     *
     * @return the boolean
     */
    boolean isAsyncEnabled();

    /**
     * Is circuit break enabled boolean.
     *
     * @return the boolean
     */
    boolean isCircuitBreakEnabled();

    /**
     * Is force break enabled boolean.
     *
     * @return the boolean
     */
    boolean isForceBreakEnabled();

    /**
     * Is fallback enabled boolean.
     *
     * @return the boolean
     */
    boolean isFallbackEnabled();

    /**
     * Gets fallback class.
     *
     * @return the fallback class
     */
    Class getFallbackClass();

    /**
     * Gets fallback bean.
     *
     * @return the fallback bean
     */
    String getFallbackBean();

    /**
     * Gets retry.
     *
     * @return the retry
     */
    int getRetry();

    /**
     * Sets async enabled.
     *
     * @param asyncEnabled the async enabled
     */
    void setAsyncEnabled(boolean asyncEnabled);

    /**
     * Sets circuit break enabled.
     *
     * @param enableCircuitBreak the enable circuit break
     */
    void setCircuitBreakEnabled(boolean enableCircuitBreak);

    /**
     * Sets force break enabled.
     *
     * @param forceBreakEnabled the force break enabled
     */
    void setForceBreakEnabled(boolean forceBreakEnabled);

    /**
     * Sets fallback enabled.
     *
     * @param enableFallback the enable fallback
     */
    void setFallbackEnabled(boolean enableFallback);

    /**
     * Sets fallback class.
     *
     * @param fallbackClass the fallback class
     */
    void setFallbackClass(Class fallbackClass);

    /**
     * Sets fallback bean.
     *
     * @param fallbackBean the fallback bean
     */
    void setFallbackBean(String fallbackBean);

    /**
     * Sets retry.
     *
     * @param retry the retry
     */
    void setRetry(int retry);


    /**
     * The type Default resty command config.
     */
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
        public boolean refresh(UpdateCommandConfig updateCommandConfig) {

            if (updateCommandConfig.getCircuitBreakEnabled() != null) {
                this.setCircuitBreakEnabled(updateCommandConfig.getCircuitBreakEnabled());
            }
            if (updateCommandConfig.getFallbackEnabled() != null) {
                this.setFallbackEnabled(updateCommandConfig.getFallbackEnabled());
            }
            return true;
        }
    }
}
