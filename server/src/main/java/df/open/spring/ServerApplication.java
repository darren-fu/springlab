package df.open.spring;

import df.open.restyproxy.starter.EnableRestyProxy;
import df.open.restyproxy.command.RestyCommandContext;
import df.open.restyproxy.command.update.UpdatedCommandConfig;
import df.open.spring.service.ProxyService;
import df.open.spring.service.Response;
import df.open.spring.service.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
@EnableRestyProxy
public class ServerApplication implements EmbeddedServletContainerCustomizer {

    @Autowired
    private ProxyService proxyService;


    public static void main(String[] args) {
        SpringApplicationBuilder appBuilder = new SpringApplicationBuilder();
        appBuilder.sources(ServerApplication.class);
        appBuilder.run();
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
//        container.setPort(7200);
    }

    @RequestMapping("/status")
    public String rest() {
        return proxyService.getStatus();
    }

    @RequestMapping(value = "/list")
    public List<User> getList() throws ExecutionException, InterruptedException {
//        List<User> list = proxyService.getList();
        return proxyService.getList();
    }

    @RequestMapping(value = "/update")
    public String update() {
        User user = new User();
        user.setName("XXX");
        return proxyService.update(10L, "myname", user);
    }

    @RequestMapping("/age")
    public Response<String> getAge() {
        return proxyService.getAge(10L, "code", "test");
    }


    @RequestMapping("/refresh")
    public String refresh() {
        UpdatedCommandConfig commandConfig = new UpdatedCommandConfig();
        commandConfig.setServiceName("resty");
        //        commandConfig.setPath();
        commandConfig.setCircuitBreakEnabled(false);
        commandConfig.setFallbackEnabled(false);
        RestyCommandContext.getInstance().refresh(commandConfig);
        return "Done";
    }

}
