package df.open.restyproxy.command.update;

/**
 * 配置更新接口
 * Created by darrenfu on 17-7-27.
 */
public interface Updater<T> {
    boolean refresh(T t);
}
