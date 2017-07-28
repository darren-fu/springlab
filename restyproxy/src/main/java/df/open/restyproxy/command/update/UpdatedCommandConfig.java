package df.open.restyproxy.command.update;

import lombok.Data;

/**
 * 用于更新的Command配置项
 * Created by darrenfu on 17-7-27.
 */
@Data
public class UpdatedCommandConfig {

    private String serviceName;
    private String path;

    private Boolean circuitBreakEnabled;
    private Boolean fallbackEnabled;

}
