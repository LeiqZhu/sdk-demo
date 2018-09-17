package com.biz;

import com.biz.smarthard.InitConfig;
import com.biz.smarthard.db.JedisonDao;
import com.sdk.core.cache.jedis.core.type.JAtomicLong;
import com.sdk.core.cache.jedis.core.type.JLock;
import com.sdk.core.codec.SFoxAes;
import com.sdk.server.ServerContext;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.time.DateFormats;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AccessServer {
    static final Logger log = LoggerFactory.getLogger(AccessServer.class);

    public static void reloadconf() throws Exception {
        InitConfig.reloadConf(null, true);
    }

    public static Map<String, Object> reloadAllconf() throws Exception {

        Map<String, Object> errorMap = new HashMap<>();
        try {
            InitConfig.reloadConf(null, true);
        }
        catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] st = e.getStackTrace();

            String errorMsg = e.getMessage() + "\r\n";

            for (StackTraceElement stackTraceElement : st) {
                String exclass = stackTraceElement.getClassName();
                String file = stackTraceElement.getFileName();
                int line = stackTraceElement.getLineNumber();
                errorMsg = errorMsg + exclass + "(" + file + " at line:" + line + ")\r\n";
            }
            errorMap.put("ReloadError", errorMsg);
        }

        return errorMap;
    }

    public static Map<String, Object> reloadAllData() throws Exception {

        Map<String, Object> errorMap = new HashMap<String, Object>();

        try {
            InitConfig.initData();
        }
        catch (Exception e) {
            ExceptionUtils.getFullStackTrace(e);
            StackTraceElement[] st = e.getStackTrace();
            String errorMsg = e.getMessage() + "\r\n";
            for (StackTraceElement s : st) {

                String exclass = s.getClassName();
                String file = s.getFileName();
                int line = s.getLineNumber();
                errorMsg = errorMsg + exclass + "(" + file + " at line:" + line + ")\r\n";
            }
            errorMap.put("ReloadError", errorMsg);
        }
        return errorMap;
    }

    public static boolean reloadconfByname(String funcStr, String tableName) throws Exception {

        if ("smarthard".equals(funcStr)) {
            String[] arrNames = null;
            if (tableName != null && tableName.length() > 0) {
                if (!tableName.equals("allTable")) {
                    arrNames = tableName.split(",");
                }
            }
            return InitConfig.reloadconfByName(arrNames);
        }
        else {
            return false;
        }

    }

    public static void initconf() throws Exception {

        try {
            JAtomicLong initCacheLastTime = JedisonDao.getConfig()
                                                      .getAtomicLong("control:init:initCacheLastTime");
            JLock lock = JedisonDao.getConfig().getLock("control:init:initCacheLock");
            long lastInitTime = initCacheLastTime.get();

            long curTime = System.currentTimeMillis();
            String date = DateFormats.formatDT(new java.util.Date(curTime));
            log.info("initconf lastInitTime: " + date);

            if (lastInitTime == 0
                || curTime
                   - lastInitTime > ServerContext.httpServer.getConfig().initCacheSpanTime) {

                boolean isLocked = lock.tryLock(0, 300, TimeUnit.SECONDS);
                if (isLocked) {
                    try {
                        System.out.println("cache locked!");
                        lastInitTime = initCacheLastTime.get();
                        if (lastInitTime == 0
                            || curTime
                               - lastInitTime > ServerContext.httpServer.getConfig().initCacheSpanTime) {

                            log.info("initconf start!");
                            long time1 = System.currentTimeMillis();

                            System.out.println("cache start!");
                            doInitConf();

                            initCacheLastTime.set(curTime);

                            long time2 = System.currentTimeMillis();
                            System.out.println("cache finished! comsume time: "
                                               + (time2 - time1)
                                               + "ms");
                            log.info("initconf finish! comsume time: " + (time2 - time1) + "ms");
                        }
                        else {
                            System.out.println("cache locked but span time not enough!");
                        }
                    }
                    finally {
                        lock.unlock();
                    }
                }
                else {
                    System.out.println("cache ignore!");
                }
            }
            else {
                System.out.println("cache span time not enough!");
            }
        }
        finally {
        }
    }

    private static void doInitConf() throws Exception {
        // sdkServer initconf缓存
        try {
            InitConfig.initconf();
        }
        catch (Exception e) {
            System.out.print("InitConf error : ");
            e.printStackTrace();
        }

    }

    public static void startSchedule() {

        Thread payThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // PaySchedule.run();
            }
        });
        payThread.setDaemon(true);
        payThread.start();

    }

    public static void initUrlMap() {

    }

    public static void main(String[] args) {
        final String systemPWD = "Fqhx@sdk@server";

        final String MSG_AES_PASSWORD = "xyzG7FBook9OverZ";
        try {
            System.out.println( SFoxAes.encrypt(MSG_AES_PASSWORD, systemPWD));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
