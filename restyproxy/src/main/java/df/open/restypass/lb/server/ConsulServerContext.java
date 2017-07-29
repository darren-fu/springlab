package df.open.restypass.lb.server;

import java.util.List;
import java.util.Map;

/**
 * Created by darrenfu on 17-6-25.
 */
public class ConsulServerContext {

    private List<String> serverList;

    private Map<String, List<ServerInstance>> serverMap;


//    public ConsulServerContext(DiscoveryClient discoveryClient) {
//        this.serverList = discoveryClient.getServices();
//        serverMap = new HashMap<>();
//        for (String serverName : this.serverList) {
//            serverMap.put(serverName, convertServerInstance(discoveryClient.getInstances(serverName)));
//        }
//    }
//
//    private List<ServerInstance> convertServerInstance(List<ServiceInstance> serviceInstanceList) {
//        List<ServerInstance> serverInstanceList = new ArrayList<>();
//
//        for (ServiceInstance serviceInstance : serviceInstanceList) {
//            ServerInstance instance = new ServerInstance();
//            instance.setServiceName(serviceInstance.getServiceId());
//            instance.setHost(serviceInstance.getHost());
//            instance.setPort(serviceInstance.getPort());
//            instance.setHttps(serviceInstance.isSecure());
//            instance.setProps(serviceInstance.getMetadata());
//            instance.setAlive(true);
//            instance.setRoom(RestyConst.ROOM_DEFAULT);
//            instance.setStartTime(new Date());
//            try {
//                URL url = new URL(instance.isHttps() ? "HTTPS" : "HTTP", instance.getHost(), instance.getPort(), "");
////                instance.setUrl(url);
////                instance.setUri();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//
//            serverInstanceList.add(instance);
//        }
//        return serverInstanceList;
//    }

}
