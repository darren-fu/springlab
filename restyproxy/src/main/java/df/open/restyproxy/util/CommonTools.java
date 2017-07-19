package df.open.restyproxy.util;

/**
 * Created by darrenfu on 17-7-19.
 */
public class CommonTools {

    public static String emptyToNull(String string) {
        return string == null || string.isEmpty() ? null : string;
    }
}
