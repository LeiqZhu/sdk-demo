/**
 * 
 */
package com.biz.smarthard.bean.redis;


import com.biz.smarthard.entity.RedisId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public class SHId {

    public static Map<String, RedisId> redisIdMap = new HashMap<>();

    static {

        redisIdMap.put(SHId.incrUserId, new RedisId("user_id", "sh_user","default-db"));
        redisIdMap.put(SHId.incrRealInfoId, new RedisId("realinfo_id", "sh_realinfo","default-db"));
        redisIdMap.put(SHId.incrTradeId, new RedisId("id", "sh_pay_trade","default-db"));
    }

    public static final String incrUserId = "smarthard:id:userId";

    public static final String incrRealInfoId = "smarthard:id:realInfoId";

    public static final String incrTradeId = "smarthard:id:tradeId";

}
