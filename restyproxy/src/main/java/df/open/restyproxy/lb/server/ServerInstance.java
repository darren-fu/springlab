package df.open.restyproxy.lb.server;

import df.open.restyproxy.base.RestyConsts;
import df.open.restyproxy.util.StringBuilderFactory;
import lombok.Data;
import org.asynchttpclient.uri.Uri;

import java.util.Date;
import java.util.Map;

/**
 * 服务实例
 * Created by darrenfu on 17-6-25.
 */
@Data
public class ServerInstance {

    /**
     * 服务实例ID unique
     */
    private String instanceId;
    
    /**
     * 服务名称
     */
    private String serviceName;


    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    /**
     * 是否 https
     */
    private boolean isHttps;

    /**
     * url
     */
    private Uri uri;
    /**
     * 机房
     */
    private String room;

    /**
     * 是否存活
     */
    private boolean isAlive;

    /**
     * 实例开始服务时间
     */
    private Date startTime;

    /**
     * 其它属性
     */
    private Map<String, String> props;


    public static ServerInstance buildInstance(String serviceName, String host, Integer port) {
        ServerInstance instance = new ServerInstance();

        instance.setServiceName(serviceName);
        instance.setHost(host);
        instance.setPort(port);
        instance.setStartTime(new Date());
        instance.setAlive(true);
        instance.setRoom(RestyConsts.ROOM_DEFAULT);
        instance.setHttps(false);

        StringBuilder sb = StringBuilderFactory.DEFAULT.stringBuilder();
//        sb.append("#");
        sb.append(serviceName);
//        sb.append("#");
//        sb.append(instance.getRoom());
//        sb.append("#");
//        sb.append(instance.isHttps ? "HTTP" : "HTTPS");
        sb.append("@");
        sb.append(host);
        sb.append(":");
        sb.append(port);

        instance.setInstanceId(sb.toString());
        return instance;
    }


}
