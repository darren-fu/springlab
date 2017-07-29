package df.open.restypass.util;

/**
 * StringBuilder工厂类
 * Created by darrenfu on 17-7-1.
 */
public class StringBuilderFactory {

    public static final StringBuilderFactory DEFAULT = new StringBuilderFactory();

    private final ThreadLocal<StringBuilder> pool = ThreadLocal.withInitial(() -> new StringBuilder(512));

    /**
     * BEWARE: MUSN'T APPEND TO ITSELF!
     *
     * @return a pooled StringBuilder
     */
    public StringBuilder stringBuilder() {
        StringBuilder sb = pool.get();
        sb.setLength(0);
        return sb;
    }
}
