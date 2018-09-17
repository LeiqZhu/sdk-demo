package com.biz.smarthard.db;

import com.sdk.core.db.DaoFactory;
import com.sdk.core.db.IDbDao;
import com.sdk.core.db.MySqlDao;

public class DbDao {
    public static IDbDao getDefault() {
        return MySqlDao.getDao();
    }
    public static IDbDao getLog() {
        return (MySqlDao) DaoFactory.getInstance().getDao("sh-log-db");
    }

    public static IDbDao getDb(String dbname) {
        IDbDao dao = MySqlDao.getDao(dbname);
        if (dao != null) {
            return dao;
        }
        return MySqlDao.getDao();
    }

    public static IDbDao getData() {
        return MySqlDao.getDao();
    }

    public static IDbDao getCommon() {
        return MySqlDao.getDao();
    }

}
