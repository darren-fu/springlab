package df.open.restyproxy.loadbalance;

import df.open.restyproxy.base.RestyConsts;
import lombok.Data;
import org.asynchttpclient.uri.Uri;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

/**
 * Created by darrenfu on 17-6-25.
 */
@Data
public class ServerInstance {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务实例ID
     */
    private String instanceId;

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
        return instance;
    }



}
