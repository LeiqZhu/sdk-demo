/**
 * 
 */
package com.biz.smarthard.scheduled;

import com.biz.smarthard.bean.redis.SHSysConfig;
import com.biz.smarthard.db.JedisonDao;
import com.sdk.core.cache.jedis.core.type.JLock;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.taskmanager.FixedTaskManager;
import org.nutz.lang.Strings;
import snowfox.lang.util.Convert;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 *
 */
public abstract class ScheduledHandler {


    public static Map<String, WorkThread> workThreadMap = new HashMap<>();

    // 上锁时间
    private int getLeaseTime;

    // 间隔时间
    private Long IntervalTime;

    // 线程上锁
    private String lock;

    // 上次执行线程的时间
    private String lastGetTime;

    // 每次执行线程的间隔新闻
    private String configGetInterval;

    // 线程池线程数量
    private int threadNum;

    // 第一次执行时间
    private long initialDelay;

    // 每次执行的间隔
    private long delay;

    private TimeUnit unit;

    // 被锁对象
    private String locked;

    // 上次处理时间
    private long lastTime;

    public int getGetLeaseTime() {
        return getLeaseTime;
    }

    public void setGetLeaseTime(int getLeaseTime) {
        this.getLeaseTime = getLeaseTime;
    }

    public Long getIntervalTime() {
        return IntervalTime;
    }

    public void setIntervalTime(Long intervalTime) {
        IntervalTime = intervalTime;
    }

    public ScheduledHandler withIntervalTime(Long intervalTime) {
        IntervalTime = intervalTime;
        return this;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public String getLastGetTime() {
        return lastGetTime;
    }

    public void setLastGetTime(String lastGetTime) {
        this.lastGetTime = lastGetTime;
    }

    public String getConfigGetInterval() {
        return configGetInterval;
    }

    public void setConfigGetInterval(String configGetInterval) {
        this.configGetInterval = configGetInterval;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public ScheduledHandler(int getLeaseTime,
                            long intervalTime,
                            String lastGetTime,
                            String configGetInterval,
                            String lock,
                            String locked) {
        this.getLeaseTime = getLeaseTime;
        this.IntervalTime = intervalTime;
        this.lock = lock;
        this.lastGetTime = lastGetTime;
        this.configGetInterval = configGetInterval;
        this.locked = locked;
    }

    public ScheduledHandler(String name, String locked) {
        this.getLeaseTime = 6 * 60 * 1000;
        this.IntervalTime = 6 * 60 * 1000L;
        this.lock = "qt:Lock:" + name;
        this.lastGetTime = name;
        this.configGetInterval = name;
        this.locked = locked;
    }

    /**
     * 线程参数
     * 
     * @return
     */
    public ScheduledHandler withThreadPool(int threadNum,
                                           long initialDelay,
                                           long delay,
                                           TimeUnit unit) {
        this.threadNum = threadNum;
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.unit = unit;

        return this;
    }

    public abstract void doHandler();

    /**
     * 开启定时任务
     */
    public void startSchedule() {

        WorkThread scheduleThread = new WorkThread(this);

        ScheduleTaskManager.newInstance().scheduleWithFixedDelay(scheduleThread,
                                                                 this.initialDelay,
                                                                 this.delay,
                                                                 this.unit);

        workThreadMap.put(this.lock + this.locked, scheduleThread);

    }

    /**
     * 开启固定线程数线程池
     */
    public void startFixed() {

        WorkThread workThread = new WorkThread(this);

        // 开启固定线程数线程池
        FixedTaskManager.me().execute(workThread);

    }

    public class WorkThread implements Runnable {

        private ScheduledHandler handler;

        public WorkThread(ScheduledHandler handler) {
            super();
            this.handler = handler;
        }

        @Override
        public void run() {

            doBefore();

            if (Strings.isEmpty(handler.locked)) {
                // 如果没有被锁对象
                handler.setLocked("default");
            }
            else {
                handler.setLock(handler.lock + handler.locked);
            }

            // 获取当前时间
            long curTime = System.currentTimeMillis();

            // 获取上次执行时间
            IHash<String, Object> configData = JedisonDao.getConfig()
                                                         .getHash(SHSysConfig.LastTime
                                                                  + handler.lastGetTime);
            handler.lastTime = Convert.toLong(configData.hget(handler.locked));

            // 获取每次线程执行的最小间隔时间
            IHash<String, Object> config = JedisonDao.getConfig()
                                                     .getHash(SHSysConfig.IntervalTime
                                                              + handler.configGetInterval);
            Long getNewsInterval = Convert.toLong(config.hget(handler.locked));

            if (curTime - handler.lastTime >= getNewsInterval) {

                // 获取锁
                JLock lock = JedisonDao.getBuffer().getLock(handler.lock);

                // 上锁
                boolean isLocked = lock.tryLock(0, handler.getLeaseTime, TimeUnit.MILLISECONDS);

                // 判断是否上锁
                if (isLocked) {

                    try {

                        // 如果没有设置最小间隔时间,则取代码设置
                        if (getNewsInterval == null || getNewsInterval == 0) {
                            getNewsInterval = handler.IntervalTime;
                            config.hset(handler.locked, getNewsInterval);
                        }

                        handler.doHandler();

                        // 如果没有上次更新时间,则设置当前时间
                        if (handler.lastTime == 0) {
                            handler.lastTime = curTime;
                        }

                        configData.hset(handler.locked, curTime);

                    }
                    finally {
                        // 释放锁
                        lock.unlock();
                    }

                }

            }

            // 每次轮询结束都会执行
            doAfter();

        }

    }

    /**
     * 每次轮询开始都会执行
     */
    public void doBefore() {

    }

    /**
     * 每次轮询结束都会执行
     */
    public void doAfter() {

    }
}
