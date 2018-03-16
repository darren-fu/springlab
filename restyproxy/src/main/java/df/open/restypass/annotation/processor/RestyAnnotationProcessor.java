package df.open.restypass.annotation.processor;

import df.open.restypass.command.RestyCommandConfig;

import java.lang.annotation.Annotation;

/**
 * Resty的注解解析接口
 * Created by darrenfu on 17-6-24.
 */
@SuppressWarnings("UnusedReturnValue")
public interface RestyAnnotationProcessor {

    /**
     * 解析注解，值放入 RestyCommandConfig中
     *
     * @param annotation the annotation
     * @param properties the properties
     * @return the resty command config
     */
    RestyCommandConfig processor(Annotation annotation, RestyCommandConfig properties);

}
