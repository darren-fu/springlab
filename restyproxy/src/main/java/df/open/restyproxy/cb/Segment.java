package df.open.restyproxy.cb;

import df.open.restyproxy.util.CacheLongAdder;
import lombok.ToString;

import java.util.Date;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 执行结果统计段
 * Created by darrenfu on 17-7-24.
 */
public class Segment {


    private Deque<SegmentMetrics> metricsDeque;

    /**
     * 段开始时间
     */
    private Date start;

    /**
     * 段结束时间
     */
    private Date end;


    /**
     * Instantiates a new Segment.
     */
    public Segment() {
        this.start = new Date();
        this.metricsDeque = new ConcurrentLinkedDeque<>();
        SegmentMetrics newMetrics = new SegmentMetrics();
        metricsDeque.addFirst(newMetrics);
    }

    private ReentrantLock lock = new ReentrantLock();

    /**
     * Store segment metrics.
     *
     * @param success the success
     * @return the segment metrics
     */
    public SegmentMetrics store(boolean success) {
        SegmentMetrics firstMetrics = getFirstMetrics();
        if (success) {
            firstMetrics.success();
        } else {
            firstMetrics.fail();
        }
        return firstMetrics;
    }

    /**
     * Add first metrics segment metrics.
     *
     * @return the segment metrics
     */
    public SegmentMetrics addFirstMetrics() {
        SegmentMetrics newMetrics = new SegmentMetrics();
        metricsDeque.addFirst(newMetrics);
        return newMetrics;
    }

    /**
     * Gets first metrics.
     *
     * @return the first metrics
     */
    public SegmentMetrics getFirstMetrics() {
        SegmentMetrics metrics = this.metricsDeque.peekFirst();
        if (metrics == null) {
            lock.lock();
            try {
                SegmentMetrics first = this.metricsDeque.peekFirst();
                if (first != null) {
                    metrics = first;
                } else {
                    SegmentMetrics newMetrics = new SegmentMetrics();
                    metricsDeque.offerFirst(newMetrics);
                    metrics = newMetrics;
                }

            } finally {
                lock.unlock();
            }
        }
        return metrics;
    }


    /**
     * 计数器
     */
    @ToString(exclude = {"lock"})
    protected class SegmentMetrics {
        /**
         * 总数
         */
        private LongAdder total;

        /**
         * 失败数量
         */
        private LongAdder fail;

        /**
         * 失败比例 （性能考虑，失败比例只在增加fail记录时才重新计算）
         */
        private Integer failPercentage;

        /**
         * 最近一次记录时间戳
         */
        private Long last;

        /**
         * 最近一次失败记录的时间戳
         */
        private Long lastFail;

        /**
         * 失败次数，快速返回
         */
        private Long failCount;

        /**
         * 锁
         */
        private ReentrantLock lock;


        /**
         * Instantiates a new Segment metricsDeque.
         */
        public SegmentMetrics() {
            this.total = new CacheLongAdder();
            this.fail = new CacheLongAdder();
            this.failPercentage = 0;
            this.last = null;
            this.lastFail = null;
            this.failCount = null;
            this.lock = new ReentrantLock();
        }

        /**
         * 失败总数
         *
         * @return the long
         */
        public Long failCount() {
            if (failCount == null) {
                failCount = fail.longValue();
            }
            return failCount;
        }

        /**
         * 失败比例 (%)
         *
         * @return the integer
         */
        public Integer failPercentage() {
            return failPercentage;
        }

        /**
         * 失败记录
         */
        public void fail() {
            fail.increment();
            total.increment();

            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                lastFail = last = System.currentTimeMillis();
                failCount = fail.longValue();
                failPercentage = Math.toIntExact(failCount * 100 / total.longValue());
            } finally {
                lock.unlock();
            }
        }


        /**
         * 成功记录
         */
        public void success() {
            total.increment();
            last = System.currentTimeMillis();
        }


        /**
         * Last long.
         *
         * @return the long
         */
        public Long last() {
            return this.last;
        }
    }

}
