package df.open.restyproxy.wrapper.spring;

import df.open.restyproxy.base.RestyRequestTemplate;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;

import static df.open.restyproxy.util.CommonTools.emptyToNull;

/**
 * Created by darrenfu on 17-7-19.
 */
public class RequestParamProcessor implements AnnotationProcessor {

    private static final Class<RequestParam> ANNOTATION = RequestParam.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean process(RestyRequestTemplate requestTemplate, Annotation annotation) {
        RequestParam requestParam = ANNOTATION.cast(annotation);
        String name = requestParam.value();
        if (emptyToNull(name) != null) {

        }

        return false;
    }
}
