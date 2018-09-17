/**
 *
 */
package com.biz.smarthard.scheduled;

import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.savedb.SaveServer;
import com.biz.smarthard.savedb.SaveToDBServer;
import com.sdk.core.buffer.db.SaveCacheToDB;

import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
public class ScheduleServer {

    /**
     * save2Db
     */
    public static void save2Db() {

        new ScheduledHandler("Save2DB", "") {

            @Override
            public void doHandler() {

                SaveCacheToDB.doQueueKeyName(JedisonDao.getBuffer(),
                        SaveServer.lstUpdateDBOperation,
                        SaveToDBServer.ModuleName.SH);
                SaveCacheToDB.newBaseSaveToDB(JedisonDao.getBuffer(),
                        SaveServer.lstUpdateDBOperation,
                        SaveToDBServer.ModuleName.SH);
            }
        }.withThreadPool(2, 5, 6, TimeUnit.MINUTES).startSchedule();

    }

    public static void doSchedule() {
        save2Db();
    }

    public static void main(String[] args) {
        doSchedule();
    }



}
