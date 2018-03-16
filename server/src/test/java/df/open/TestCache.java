package df.open;

import df.open.spring.ServerApplication;
import df.open.spring.service.ProxyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * author: fuliang
 * date: 2018/3/15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServerApplication.class)
@Rollback
public class TestCache {


    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ProxyService proxyService;

    @Test
    public void testCache() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        System.out.println(cacheNames);
        System.out.println("####################");
        System.out.println(proxyService.getStatus());
        System.out.println("####################");
        System.out.println(proxyService.getStatus());
        System.out.println("####################");
        System.out.println(proxyService.getStatus());

    }


    @Test
    public void testGetPriceConcurency() throws InterruptedException {

        int mult = 100;
        int times = 2;

        final CountDownLatch start = new CountDownLatch(mult);
        final CountDownLatch end = new CountDownLatch(mult);

        for (int i = 0; i < mult; i++) {

            int finalI = i;
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "就绪--" + finalI);
                    start.await();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int m = 0; m < times; m++) {
                    System.out.println(Thread.currentThread().getName() + "--->" + proxyService.getStatus());
                }
                end.countDown();
            }).start();
        }

        for (int i = 0; i < mult; i++) {
            start.countDown();
        }

        end.await();

    }


}
