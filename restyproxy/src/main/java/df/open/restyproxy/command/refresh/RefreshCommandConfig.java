package df.open.restyproxy.command.refresh;

import lombok.Data;

/**
 * Created by darrenfu on 17-7-27.
 */
@Data
public class RefreshCommandConfig {

    private String serviceName;
    private String path;

    private Boolean circuitBreakEnabled;
    private Boolean fallbackEnabled;

}
