package com.biz.smarthard.savedb;

import com.biz.smarthard.bean.redis.SHSysConfig;
import com.biz.smarthard.db.JedisonDao;
import com.sdk.core.buffer.db.SaveCacheToDB;
import com.sdk.core.cache.jedis.JedisDao;
import com.sdk.core.cache.type.IHash;
import com.sdk.server.HttpServer;
import com.sdk.server.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SaveToDBServer extends SaveCacheToDB {

    static final Logger log = LoggerFactory.getLogger(SaveToDBServer.class);

    public static class ModuleName {
        public static final String SH = "smarthard";
        public static final String Dispatch = "Dispatch";
    }

    
    static {
        mThreadPool.put(ModuleName.Dispatch + "_" + ModuleName.SH,
                        newWorkerThreadPool(20,
                                            ConfigData.getDispatcherNumData(JedisonDao.getBuffer())));
        mThreadPool.put(ModuleName.SH, newWorkerThreadPool(20, 50));
        
    }

    public static String updateDB() {

        System.out.println("UpdateDB");

        String ret = "Server: " + ServerContext.httpServer.getConfig().port + "\r\n";

        JedisDao rb = JedisonDao.getBuffer();
        IHash<String, Object> h = rb.getHash(SHSysConfig.allConfigData);
        try {

            SaveCacheToDB.doQueueKeyName(rb, SaveServer.lstUpdateDBOperation, ModuleName.SH);
            SaveCacheToDB.newBaseSaveToDB(rb,
                                          SaveServer.lstUpdateDBOperation,
                                          ModuleName.SH);
            boolean isUpdated = true;
            if (isUpdated) {
                h.hset(SHSysConfig.lastUpdateDBTime, System.currentTimeMillis());
                ret += "save db starting\r\n";
            }
            else {
                ret += "save db not starting\r\n";
            }
        }
        catch (Exception e) {
            String err = "UpdateDB error : " + e;
            System.out.print(err);
            ret += err + "\r\n";
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        System.out.println(updateDB());
    }

}
