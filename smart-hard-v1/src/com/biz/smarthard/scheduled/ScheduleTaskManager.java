/**
 * 
 */
package com.biz.smarthard.scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 *
 */
public class ScheduleTaskManager {

    // 单例
    private static ScheduleTaskManager taskManager;

    // 线程池
    private ScheduledExecutorService scheduledExecutorService;

    public ScheduleTaskManager() {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(20);
    }

    // 获取线程池管理对象
    public static ScheduleTaskManager newInstance() {
        if (taskManager == null) {
            synchronized (ScheduleTaskManager.class) {
                if (taskManager == null) {
                    taskManager = new ScheduleTaskManager();
                }
            }
        }
        return taskManager;
    }

    /**
     * 开启定时任务
     * 
     * @param command
     * @param initialDelay
     * @param delay
     * @param unit
     */
    public void scheduleWithFixedDelay(Runnable command,
                                       long initialDelay,
                                       long delay,
                                       TimeUnit unit) {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    }

    /**
     * 开启定时任务
     * 
     * @param command
     * @param initialDelay
     * @param delay
     * @param unit
     */
    public void scheduleAtFixedRate(Runnable command,
                                    long initialDelay,
                                    long delay,
                                    TimeUnit unit) {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, delay, unit);
        }
    }

    /**
     * 取消任务
     * 
     * @param command
     */
    public void cancel(Runnable command) {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.submit(command).cancel(true);
        }
    }

    /**
     * 销毁线程管理类
     */
    public void destory() {

        if (scheduledExecutorService != null) {
            // 关闭线程池
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
        taskManager = null;
    }

}
