package df.open.spring.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.lang.reflect.Method;

/**
 * author: fuliang
 * date: 2018/3/15
 */

//@EnableCaching(mode = AdviceMode.PROXY,order = Ordered.LOWEST_PRECEDENCE)
public class CacheConfig extends CachingConfigurerSupport {


    @Configuration
    @ConditionalOnMissingClass(value = {"org.springframework.cache.aspectj.AspectJCachingConfiguration"})
    @EnableCaching(mode = AdviceMode.PROXY, order = 0)
    public static class CacheConfigProxy extends CacheConfig {
        @Bean
        public Object typeConfig(){
            System.out.println("CacheConfigProxy...");
            return new Object();
        }
    }

    @Configuration
    @ConditionalOnClass(name = {"org.aspectj.weaver.AnnotationAJ","org.springframework.cache.aspectj.AspectJCachingConfiguration"})
    @EnableCaching(mode = AdviceMode.ASPECTJ, order = 0)
    public static class CacheConfigAspectj extends CacheConfig {
        @Bean
        public Object typeConfig(){
            System.out.println("CacheConfigAspectj...");
            return new Object();
        }
    }


    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Bean
//    @ConditionalOnExpression("'${#redisConnectionFactory}'!= null")
    @ConditionalOnProperty("spring.redis.host")
    public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }

    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(StringRedisTemplate redisTemplate) {
        System.out.println("cacheManager");
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        //设置缓存过期时间
        rcm.setDefaultExpiration(10);//秒

        return rcm;
    }
}
