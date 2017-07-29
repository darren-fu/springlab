package df.open.spring.service.impl;

import df.open.restypass.exception.RestyException;
import df.open.spring.service.ProxyService;
import df.open.spring.service.Response;
import df.open.spring.service.User;

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
public class ProxyServiceImpl implements ProxyService {
    @Override
    public String getStatus() {
        System.out.println("执行基本降级");
        return "Fallback";
    }

    public String getStatus(RestyException ex) {
        System.out.println("执行加强降级，Ex:" + ex.getMessage());
        return "Enhance Fallback";
    }

    @Override
    public List<User> getList() {
        return null;
    }

    @Override
    public Response<String> getAge(Long id, String code, String name) {
        return null;
    }

    @Override
    public int getHeight(Long id) {
        return 0;
    }

    @Override
    public String update(Long id, String name, User user) {
        System.out.println("执行基本降级");

        return "FALLBACK";
    }

    @Override
    public void applicationIndex() {

    }
}
