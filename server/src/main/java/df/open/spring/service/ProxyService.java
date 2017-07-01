package df.open.spring.service;

import df.open.restyproxy.annotation.RestyMethod;
import df.open.restyproxy.annotation.RestyService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;

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
@RestyService(serviceName = "resty")
public interface ProxyService extends ApplicationService {

    @RestyMethod
    String getStatus();

    @RestyMethod(value = "/get/value")
    Response<String> getAge(@RequestParam("id") Long id, String code, @PathVariable(value = "name") String name);

    int getHeight(Long id);

}
