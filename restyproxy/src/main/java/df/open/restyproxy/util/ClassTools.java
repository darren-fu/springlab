package df.open.restyproxy.util;

/**
 * Class工具类
 * Created by darrenfu on 17-7-23.
 */
public class ClassTools {

    public static <T> T castTo(Object obj, Class<T> clz) {
        return clz.cast(obj);
    }
}
