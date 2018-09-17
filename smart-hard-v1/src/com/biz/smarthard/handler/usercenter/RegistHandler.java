package com.biz.smarthard.handler.usercenter;

import com.biz.smarthard.bean.SmartHardStatus;
import com.biz.smarthard.bean.redis.SHBuffer;
import com.biz.smarthard.bean.redis.SHLock;
import com.biz.smarthard.bean.SHDbsql.Usersql;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.user.User;
import com.biz.smarthard.savedb.Dbsql;
import com.biz.smarthard.utils.JacksonUtil;
import com.biz.smarthard.utils.SmartHardUtil;
import com.sdk.core.cache.jedis.core.type.JLock;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.db.MySqlDao;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.time.DateUtil;
import snowfox.lang.util.Convert;
import snowfox.lang.util.R;
import snowfox.lang.util.Strings;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegistHandler {

    private static final Logger log = LoggerFactory.getLogger(RegistHandler.class);

    /**
     * 用户注册
     * @param recMap
     * @param sendMap
     * @return
     */
    public static String registUser(Map<String, Object> recMap, Map<String, Object> sendMap){
        String userToken = "";
        try {
            String androidID = Convert.toString(recMap.get("android_id"));
            if (Strings.isEmpty(androidID)){
                sendMap.put("msg","android_id error");
                return "";
            }
            String curDay = DateUtil.today();

            //根据androidID判断用户是否注册过
            userToken = getUserByAndroidId(androidID,curDay);

            if (Strings.isEmpty(userToken)){
                //如果获取不到userToken，则进行注册
                User user = new User();
                user.setUserToken(R.UU16());
                user.setAndroidID(androidID);
                user.setClientIp(Convert.toString(recMap.get("client_ip")));
                user.setCountryCode(Convert.toString(recMap.get("country_code")));
                user.setCreateTime(DateUtil.now());
                user.setModifyTime(DateUtil.now());
                user.stat();

                //注册完成将用户信息加入当日缓存
                user.setCurDayRegUser(curDay);
                user.setUserData(curDay);
                userToken = user.getUserToken();
            }

        }catch (Exception e){
            log.error("RegistHandler doRegistUserV2 error " + ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            sendMap.put("msg","server seror");
            return "";
        }
        return userToken;
    }

    public static String getUserByAndroidId(String androidId, String curDay) throws SQLException {
        String userToken = "";
        //先从当日缓存中获取
        IHash<String, User> curDayRegUserHash = JedisonDao.getBuffer().getHash(SHBuffer.SHCurDayRegUser + curDay);
        User user = curDayRegUserHash.hget(androidId);
        if (user == null){
            //如果从今日缓存中获取不到，就从mysql数据库里查询
            Object[] param = {androidId};
            Map<String, Object> userMap = MySqlDao.getDao().queryOne(Usersql.QUERY_USER_BY_AndroidID,param);
            user = SmartHardUtil.mapToBean(userMap,User.class);
        }
        if (user !=null){
            userToken = user.getUserToken();
        }
        return userToken;
    }

}
