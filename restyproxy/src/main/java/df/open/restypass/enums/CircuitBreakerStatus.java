package df.open.restypass.enums;

/**
 * The enum BreakerStatus.
 */
public enum CircuitBreakerStatus {
    /**
     * Open status.
     */
    OPEN(),
    /**
     * Break status.
     */
    BREAK(),
    /**
     * HalfOpen status.
     */
    HALF_OPEN(),

    /**
     * 强制短路
     */
    FORCE_BREAK()


}
