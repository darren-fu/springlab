package df.open.rest.controller;

import com.netflix.loadbalancer.ILoadBalancer;
import df.open.rest.entity.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by darrenfu on 17-6-19.
 */
@RestController
@RequestMapping("/resty")
public class RestfulController {

    @Autowired(required = false)
    private ILoadBalancer loadBalancer;

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

    @RequestMapping(value = "/get_age", method = RequestMethod.GET)
    Response<String> getAge(@RequestParam("id") Long id, String code, @PathVariable(value = "name") String name) {
        Response response = new Response();
        response.setCode("200");
        response.setMsg("OK!");
        response.setInfo("age is 20");
        return response;
    }


}
