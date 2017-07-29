package df.open.rest.controller;

import com.netflix.loadbalancer.ILoadBalancer;
import df.open.rest.entity.Response;
import df.open.rest.entity.User;
import df.open.rest.test.ImplA;
import df.open.rest.test.InterfaceA;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by darrenfu on 17-6-19.
 */
@RestController
@RequestMapping("/resty")
public class RestfulController implements ApplicationContextAware {

    @Autowired(required = false)
    private ILoadBalancer loadBalancer;
    @Autowired
    private HttpServletRequest request;

    private ApplicationContext applicationContext;

    @RequestMapping(value = "/index")
    public Response index(@RequestParam(value = "name", required = false) String name) {

        Response response = new Response();
        response.setInfo("hello:" + name);
        response.setMsg("OK");
        response.setCode("0");
        System.out.println(loadBalancer);
        return response;
    }


    //        @RequestMapping(value = "/get_status", method = RequestMethod.GET, headers = "Client=RestyProxy", params = "Param1=val1")
//    @RequestMapping(value = "/get_status", method = RequestMethod.GET, params = "Param1=val1")
    @RequestMapping(value = "/get_status", method = RequestMethod.GET)
    public String getStatus() {
        return "Status is OK";
    }

    @RequestMapping
    public List<User> getList() {
        User user = new User("darren");
//        try {
//            Thread.sleep(1000*60);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return Collections.singletonList(user);
    }

    @RequestMapping(value = "/get_age", method = RequestMethod.GET)
    Response<String> getAge(@RequestParam("id") Long id, String code, @PathVariable(value = "name") String name) {
        Response response = new Response();
        response.setCode("200");
        response.setMsg("OK!");
        response.setInfo("age is 20");
        return response;
    }

    @RequestMapping(value = "/update/{name}", method = RequestMethod.POST)
    public String update(@RequestParam("id") Long id, @PathVariable String name, @RequestBody User user) {
        System.out.println(user);
        System.out.println(name);

        System.out.println(id);
        System.out.println("URI:" + request.getRequestURI());
        return "UPDATED";
    }

    @RequestMapping(value = "/test")
    public void test() {
        InterfaceA bean = applicationContext.getBean(ImplA.class);
        System.out.println(bean);
        Map<String, InterfaceA> beansOfType = applicationContext.getBeansOfType(InterfaceA.class);

        for (InterfaceA interfaceA : beansOfType.values()) {

            System.out.println(interfaceA.getClass());
        }


    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
