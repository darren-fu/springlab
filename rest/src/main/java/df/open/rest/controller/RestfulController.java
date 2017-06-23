package df.open.rest.controller;

import df.open.rest.entity.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by darrenfu on 17-6-19.
 */
@RestController
public class RestfulController {


    @RequestMapping(value = "/index")
    public Response index(@RequestParam(value = "name", required = false) String name) {

        Response response = new Response();
        response.setInfo("hello:" + name);
        response.setMsg("OK");
        response.setCode("0");
        return response;
    }


}
