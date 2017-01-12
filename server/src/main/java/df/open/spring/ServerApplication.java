package df.open.spring;

import df.open.spring.proxy.EnableInterfaceProxy;
import df.open.spring.proxy.InterfaceProxyConfig;
import df.open.spring.service.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 说明:
 * <p/>
 * Copyright: Copyright (c)
 * <p/>
 * Company: 江苏千米网络科技有限公司
 * <p/>
 *
 * @author 付亮(OF2101)
 * @version 1.0.0
 * @date 2016/11/8
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@RestController
@EnableInterfaceProxy
public class ServerApplication implements EmbeddedServletContainerCustomizer {

    @Autowired
    private InterfaceProxyConfig interfaceProxyConfig;

    @Autowired
    private ProxyService proxyService;


    private RedisTemplate redisTemplate;

    public static void main(String[] args) {
        SpringApplicationBuilder appBuilder = new SpringApplicationBuilder();
        appBuilder.sources(ServerApplication.class);
//        appBuilder.run(new String[]{"--debug"});
        appBuilder.run();
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
//        container.setPort(7200);
    }

    @RequestMapping("/rest")
    public String rest() {
        String res = "rrrrrrrr";
//        System.out.println(res);
//        System.out.println(interfaceProxyConfig.getName());
        System.out.println(proxyService.getStatus());
        System.out.println(proxyService.getAge(10L, "test"));
        System.out.println(proxyService.getHeight(10L));
        return res;
    }
}
