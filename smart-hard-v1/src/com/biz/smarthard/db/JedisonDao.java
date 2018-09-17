package com.biz.smarthard.db;

import com.sdk.core.cache.jedis.JedisDao;

public class JedisonDao {
    public static JedisDao getDefault() {
        return JedisDao.getDao();
    }

    public static JedisDao getCache() {
        return JedisDao.getDao("cache-data-jedis");
    }

    public static JedisDao getCommon() {
        return JedisDao.getDao();
    }

    public static JedisDao getConfig() {
        return JedisDao.getDao();
    }

    public static JedisDao getBuffer() {
        return JedisDao.getDao();
    }

    public static JedisDao getData() {
        return JedisDao.getDao();
    }

    public static JedisDao getRedis(String dbname, String defaultName) {
        JedisDao dao = JedisDao.getDao(dbname);
        if (dao != null) {
            return dao;
        }
        if (defaultName != null && defaultName.length() > 0) {
            return JedisDao.getDao(defaultName);
        }
        else {
            throw new RuntimeException("Jedis Config: " + dbname + " is not found.");
        }
    }

    public static JedisDao getRedis(String dbname) {
        return getRedis(dbname, "default-jedis");
    }
}
