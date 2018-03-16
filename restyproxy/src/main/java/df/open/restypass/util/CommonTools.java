package df.open.restypass.util;

import java.util.Collection;
import java.util.Map;

/**
 * 通用工具
 * Created by darrenfu on 17-7-19.
 */
public class CommonTools {

    /**
     * Empty to null string.
     *
     * @param string the string
     * @return the string
     */
    public static String emptyToNull(String string) {
        return string == null || string.isEmpty() ? null : string;
    }

    /**
     * Is empty boolean.
     *
     * @param coll the coll
     * @return the boolean
     */
    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * Is empty boolean.
     *
     * @param map the map
     * @return the boolean
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }
}
