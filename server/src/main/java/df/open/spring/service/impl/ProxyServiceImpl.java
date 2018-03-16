package df.open.spring.service.impl;

import df.open.spring.service.ProxyService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
@Service
public class ProxyServiceImpl implements ProxyService {
    @Override
    @Cacheable(value = "status")
    public String getStatus() {
        System.out.println(Thread.currentThread().getName() + "----> 调用getStatus...");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "OK";
    }

    @Override
    public Integer getAge(Long id, String name) {
        return 10;
    }

    @Override
    public int getHeight(Long id) {
        return 23;
    }
}
