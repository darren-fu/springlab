package df.open.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Set;

/**
 * author: fuliang
 * date: 2017/2/23
 */
public class test {
    public static void main(String[] args) throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = resourcePatternResolver.getResources("classpath*:df/**/*.class");
        System.out.println("scan finish...");
        for (Resource resource : resources) {
            System.out.println(resource);
        }


        ClassPathScanningCandidateComponentProvider scan = new ClassPathScanningCandidateComponentProvider(true);

        Set<BeanDefinition> df = scan.findCandidateComponents("df");
        System.out.println("scan finish...");

        for (BeanDefinition beanDefinition : df) {
            System.out.println(beanDefinition);
        }

    }
}
