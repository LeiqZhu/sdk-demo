/**
 * 
 */
package com.biz.smarthard.entity;

import com.biz.smarthard.db.DbDao;
import com.biz.smarthard.db.JedisonDao;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 *
 */
public class RedisId {

    static final Logger log = LoggerFactory.getLogger(RedisId.class);

    private String mysqlId;

    private String mysqlName;

    private String tableName;

    public String getMysqlId() {
        return mysqlId;
    }

    public void setMysqlId(String mysqlId) {
        this.mysqlId = mysqlId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getMysqlName() {
        return mysqlName;
    }

    public void setMysqlName(String mysqlName) {
        this.mysqlName = mysqlName;
    }

    public RedisId() {
        super();
    }

    public RedisId(String mysqlId, String tableName) {
        new RedisId(mysqlId, tableName, "default-db");
    }

    public RedisId(String mysqlId, String tableName, String mysqlName) {
        super();
        this.mysqlId = mysqlId;
        this.mysqlName = mysqlName;
        this.tableName = tableName;
    }

    public void initId(String redisKey) {
        try {
            Long maxDbId = DbDao.getDb(this.getMysqlName()).getLongValue("select max("
                                                                         + this.getMysqlId()
                                                                         + ") from "
                                                                         + this.getTableName());

            Long maxRedisId = JedisonDao.getConfig().getAtomicLong(redisKey).get();

            if (maxDbId > maxRedisId) {
                JedisonDao.getConfig().getAtomicLong(redisKey).addAndGet(maxDbId - maxRedisId);
            }
        }
        catch (Exception e) {
            log.error("Entity {} initData error {}:",
                      redisKey,
                      ExceptionUtils.getFullStackTrace(e));
        }
    }
}
