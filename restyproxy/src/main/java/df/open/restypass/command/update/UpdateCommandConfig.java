package df.open.restypass.command.update;

import lombok.Data;

/**
 * 用于更新的Command配置项
 * Created by darrenfu on 17-7-27.
 */
@Data
public class UpdateCommandConfig {
    /**
     * 服名名称
     */
    private String serviceName;

    /**
     * 原始的请求path
     */
    private String path;
    ////////////////////////////////////////////
    /**
     * 断路器是否启用
     */
    private Boolean circuitBreakEnabled;

    /**
     * 降级服务是否启用
     */
    private Boolean fallbackEnabled;

}
