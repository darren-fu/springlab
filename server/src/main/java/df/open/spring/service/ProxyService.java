package df.open.spring.service;

import df.open.restyproxy.annotation.RestyMethod;
import df.open.restyproxy.annotation.RestyService;
import df.open.spring.service.impl.ProxyServiceImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 说明:
 * <p/>
 * Copyright: Copyright (c)
 * <p/>
 * Company:
 * <p/>
 *
 * @author darren-fu
 * @version 1.0.0
 * @contact 13914793391
 * @date 2016/11/22
 */
@RestyService(serviceName = "resty", fallbackClass = ProxyServiceImpl.class)
@RequestMapping(value = "/resty")
public interface ProxyService extends ApplicationService {

    @RestyMethod
    @RequestMapping(value = "/get_status", method = RequestMethod.GET, headers = "Client=RestyProxy", params = "Param1=val1")
    String getStatus();

    @RestyMethod()
    @RequestMapping(value = "/list")
    List<User> getList();

    @RestyMethod(value = "/get/value")
    @RequestMapping(value = "/get_age", method = RequestMethod.GET)
    Response<String> getAge(@RequestParam("id") Long id, String code, @PathVariable(value = "name") String name);

    int getHeight(Long id);

}
