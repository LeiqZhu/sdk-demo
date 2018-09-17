package com.biz.smarthard.entity.user;

import com.biz.smarthard.bean.redis.SHBuffer;
import com.biz.smarthard.bean.redis.SHData;
import com.biz.smarthard.bean.redis.SHId;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.AddToRedis;
import com.biz.smarthard.entity.SHEntity;
import com.biz.smarthard.utils.JacksonUtil.BeanOnBean;
import com.biz.smarthard.utils.SmartHardUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sdk.core.buffer.db.DBOperation;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.db.MySqlDao;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.nutz.dao.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.time.DateUtil;
import snowfox.lang.util.Objects;
import snowfox.lang.util.R;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@JsonInclude(Include.NON_EMPTY)
public class User extends AddToRedis {

    private static final Logger log = LoggerFactory.getLogger(User.class);

    private static final long serialVersionUID = 1000L;

    private Long userId;// 用户id

    private String userToken;// 用户token

    private String clientIp;// 注册用户IP地址

    private String countryCode;// 注册用户国家

    private String androidId;//注册的手机ID

    private String modifyTime;

    private String createTime;

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidID(String androidId) {
        this.androidId = androidId;
    }

    public Long getUserId() {
        return Objects.toLong(userId);
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserToken() {
        return Objects.toString(userToken);
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getClientIp() {
        return Objects.toString(clientIp);
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getCountryCode() {
        return Objects.toString(countryCode);
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getModifyTime() {
        return Objects.toString(modifyTime, DateUtil.now());
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public void setRedisId() {
        this.redisId = SHId.incrUserId;
    }

    @Override
    public void setRedisKey() {
        this.redisKey = SHBuffer.SHUserInsertMap;
    }

    @Override
    public void setInsertSql() {
        this.insertSql = "INSERT IGNORE INTO sh_user(user_id,client_ip," +
                "country_code,modify_time,create_time,user_token,android_id ) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    public void setId(Long id) {
        this.setUserId(id);
        super.setId(id);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public User() {
        super();
    }

    /**
     * 设置用户缓存
     *
     * @param curDay
     */
    public void setUserData(String curDay) {

        try {
            IHash<String, User> rUserCacheMap = JedisonDao.getCache()
                    .getHash(SHData.Data_Cur_UserId_UserMap + curDay);

            rUserCacheMap.hset(this.getUserToken(),this);

            if (rUserCacheMap.ttl() == -1){
                rUserCacheMap.expire(DateUtil.calcNext00Diff() + 600);
            }
        }
        catch (Exception e) {
            log.error("Entity User setUserData error:" + e);
        }
    }

    private static final String Query_User_By_UserToken = "SELECT * FROM sh_user WHERE user_token = ?";
    public static User getCurCacheUserByUserToken(String uerToken) throws SQLException {
        IHash<String, User> rUserCacheMap = JedisonDao.getCache()
                .getHash(SHData.Data_Cur_UserId_UserMap + DateUtil.today());
        User user = rUserCacheMap.hget(uerToken);

        if (user == null){
            Object[] param = {uerToken};
            Map<String, Object> userMap = MySqlDao.getDao().queryOne(Query_User_By_UserToken,param);
            user = SmartHardUtil.mapToBean(userMap,User.class);
            if (user != null){
                rUserCacheMap.hset(uerToken,user);
                if (rUserCacheMap.ttl() == -1){
                    rUserCacheMap.expire(DateUtil.calcNext00Diff());
                }
            }
        }
        return user;
    }

    public void setCurDayRegUser(String curDay){
        try {
            IHash<String, User> curDayRegUserHash = JedisonDao.getBuffer().getHash(SHBuffer.SHCurDayRegUser + curDay);

            curDayRegUserHash.hset(this.getAndroidId(),this);

            if (curDayRegUserHash.ttl() == -1) {
                curDayRegUserHash.expire(DateUtil.calcNext00Diff() + 600);
            }
        }catch (Exception e){
            log.error("User setCurDayRegUser error " + ExceptionUtils.getFullStackTrace(e));
        }
    }

    @Override
    public Object[] evalParam(DBOperation opr, byte[] keyValue) {

        User user = (User) decodeToObject(opr, keyValue);

        Object[] params = {user.getUserId(),
                user.getClientIp(),
                user.getCountryCode(),
                user.getModifyTime(),
                user.getCreateTime(),
                user.getUserToken(),
                user.getAndroidId()};
        return params;
    }
}
