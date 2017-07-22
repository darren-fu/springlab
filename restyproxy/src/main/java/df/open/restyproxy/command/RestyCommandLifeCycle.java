package df.open.restyproxy.command;

/**
 * RestyCommand生命周期
 * Created by darrenfu on 17-7-22.
 */
public interface RestyCommandLifeCycle {

    /**
     * 获取RestyCommand执行状态
     *
     * @return the boolean
     */
    RestyCommandStatus getStatus();


    /**
     * Ready.
     */
    void ready();

    /**
     * Start.
     */
    void start();

    /**
     * RestyCommand执行成功
     */
    void success();

    /**
     * RestyCommand执行失败
     *
     * @param executeException the execute exception
     */
    void failed(Exception executeException);


    /**
     * 获取导致command失败异常
     *
     * @return the fail exception
     */
    Exception getFailException();


}
