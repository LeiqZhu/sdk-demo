/**
 * 
 */
package com.biz.smarthard.entity;

import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.utils.SmartHardUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sdk.core.buffer.db.DBOperation;
import com.sdk.core.buffer.db.ObjectCombineDbParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.util.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 */
@JsonInclude(Include.NON_EMPTY)
public abstract class AddToRedis extends ObjectCombineDbParam implements Serializable{
    
    /**
     * AddToRedis
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(AddToRedis.class);
    
    private Long id;
    
    @JsonIgnore
    protected String redisId;

    @JsonIgnore
    protected String redisKey;

    @JsonIgnore
    protected String insertSql;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRedisId() {
        this.setRedisId();
        return this.redisId;
    }

    public abstract void setRedisId();

    public String getRedisKey() {
        this.setRedisKey();
        return this.redisKey;
    }

    public String getInsertSql() {
        this.setInsertSql();
        return insertSql;
    }

    public abstract void setRedisKey();

    public abstract void setInsertSql();

    public AddToRedis stat() {
        try {

            if (Strings.isNotEmpty(this.getRedisKey()) && Strings.isNotEmpty(this.getRedisId())) {

                this.setId(JedisonDao.getConfig().getAtomicLong(this.getRedisId()).incrAndGet());

                JedisonDao.getBuffer().getLists(this.getRedisKey()).rpush(this);
            }
        }
        catch (Exception e) {
            log.error("Statistics stat error :" + e);
        }
        return this;
    }
    
    /**
     * 一次批量插入
     * @param list
     */
    public static void stat(List<? extends AddToRedis> list) {
        try {

            Map<String, List<AddToRedis>> map = new HashMap<>();

            list.forEach(stat -> {

                String redisKey = stat.getRedisKey();
                String redisId = stat.getRedisId();

                if (Strings.isNotEmpty(redisKey) && Strings.isNotEmpty(redisId)) {

                    stat.setId(JedisonDao.getConfig().getAtomicLong(redisId).incrAndGet());

                    List<AddToRedis> stats = map.get(redisKey);
                    if (stats == null) {
                        stats = new ArrayList<>();
                    }

                    stats.add(stat);
                    map.put(redisKey, stats);
                }
            });

            for (Map.Entry<String, List<AddToRedis>> entry : map.entrySet()) {
                JedisonDao.getBuffer().getLists(entry.getKey()).rpush(entry.getValue());
            }

        }
        catch (Exception e) {
            log.error("Statistics stat error :" + e);
        }
    }

    @Override
    public Object[] evalParam(DBOperation opr, byte[] keyValue) {

        List<Object> list = (List<Object>) SmartHardUtil.beanToMap(decodeToObject(opr, keyValue))
                                                         .values();

        return list.toArray();
    }
}
