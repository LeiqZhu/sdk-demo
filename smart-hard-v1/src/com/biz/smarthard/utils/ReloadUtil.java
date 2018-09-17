package com.biz.smarthard.utils;

import com.biz.smarthard.db.JedisonDao;
import com.sdk.core.cache.jedis.core.type.JKeys;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.cache.type.ISets;
import com.sdk.core.db.MySqlDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.util.Convert;
import snowfox.lang.util.Objects;
import snowfox.lang.util.Strings;

import java.util.*;

public class ReloadUtil<T> {

    private static final Logger log = LoggerFactory.getLogger(ReloadUtil.class);

    private String sql;

    private String redisKey;

    private Class<T> className;

    private String[] hashKey;

    private List<Map<String, Object>> sqlMapList;

    private IHash<Object, Object> redisHash;

    private Map<String, Object> dataMap = new HashMap<>();

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public Class<T> getClassName() {
        return className;
    }

    public void setClassName(Class<T> className) {
        this.className = className;
    }

    public String[] getHashKey() {
        return hashKey;
    }

    public void setHashKey(String[] hashKey) {
        this.hashKey = hashKey;
    }

    public List<Map<String, Object>> getSqlMapList() {
        return sqlMapList;
    }

    public void setSqlMapList(List<Map<String, Object>> sqlMapList) {
        this.sqlMapList = sqlMapList;
    }

    public IHash<Object, Object> getRedisHash() {
        return redisHash;
    }

    public void setRedisHash(IHash<Object, Object> redisHash) {
        this.redisHash = redisHash;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public ReloadUtil() {
        super();
    }

    public static ReloadUtil<Object> instance() {
        return new ReloadUtil<>();
    }

    public ReloadUtil(String sql, String redisKey, Class<T> className, String... hashKey) {
        super();
        this.sql = sql;
        this.redisKey = redisKey;
        this.className = className;
        this.hashKey = hashKey;
    }

    public ReloadUtil<T> withParam(String sql,
                                   String redisKey,
                                   Class<T> className,
                                   String... hashKey) {
        this.sql = sql;
        this.redisKey = redisKey;
        this.className = className;
        this.hashKey = hashKey;
        return this;
    }

    // --------------------------------------------reload-----------------------------------------------------------------------------

    public ReloadUtil<T> reload() {
        return reload(false);
    }

    /**
     * 
     * @param isMap2Bean
     *            是否使用Map2Bean方法
     * @param methodNames
     *            反射执行的方法名
     * @return
     */
    public ReloadUtil<T> reload(boolean isMap2Bean, String... methodNames) {

        try {

            String redisKeyNew = this.redisKey + "_news";

            this.redisHash = JedisonDao.getConfig().getHash(redisKeyNew);

            this.sqlMapList = MySqlDao.getDao().queryAll(this.sql);

            Map<Object, Object> redisMap = new HashMap<>();

            for (Map<String, Object> sqlMap : this.sqlMapList) {

                Object key = null;

                if (hashKey.length > 1) {
                    for (String hash : this.hashKey) {
                        String hashString = Convert.toString(sqlMap.get(hash)).trim();
                        if (key == null) {
                            key = hashString;
                        }
                        else {
                            key += "_" + hashString;
                        }
                    }
                }
                else {
                    key = sqlMap.get(hashKey[0]);
                }

                T t = null;

                if (isMap2Bean) {
                    t = Objects.toObj(this.className.getMethod("Map2Bean", Map.class)
                                                    .invoke(this.className.newInstance(), sqlMap),
                                      t);
                }
                else {
                    t = SmartHardUtil.mapToBean(sqlMap, this.className);
                }

                for (String methodName : methodNames) {
                    if (Strings.isNotEmpty(methodName)) {

                        Map<Object, Object> methodMap = Objects.toObj(this.dataMap.get(methodName),
                                                                      new HashMap<>());

                        methodMap.putAll(Objects.toObj(this.className.getMethod(methodName,
                                                                                className,
                                                                                Map.class)
                                                                     .invoke(t, t, methodMap),
                                                       new HashMap<>()));

                        this.dataMap.put(methodName, methodMap);
                    }

                }

                if (t != null) {
                    redisMap.put(key, t);
                }

            }

            redisHash.hmset(new HashMap<>(redisMap));

            log.info("redisKey {} len {}", redisKey, redisHash.hlen());

            if (redisHash.hlen() > 0) {
                redisHash.rename(redisKeyNew, redisKey);
            }
            else {
                redisHash.del(redisKey);
            }

        }
        catch (Exception e) {
            log.error("ReloadUtil reload error :" + e);
        }

        return this;
    }

    public ReloadUtil<T> reloadList(boolean isMap2Bean, String... methodNames) {
        return reloadList("", isMap2Bean, methodNames);
    }

    public ReloadUtil<T> reloadSet(String valueName) {
        return reloadSet(valueName, 0);
    }

    public ReloadUtil<T> reloadSet(String valueName, int expiredTime) {
        return reloadSet(valueName, false, expiredTime);
    }

    public ReloadUtil<T> reloadSet(boolean isMap2Bean) {
        return reloadSet("", isMap2Bean, 0);
    }

    private ReloadUtil<T> reloadSet(String valueName, boolean isMap2Bean, int expiredTime) {

        try {

            List<Map<String, Object>> sqlMapList = MySqlDao.getDao().queryAll(sql);

            Map<Object, List<Object>> hashMap = new HashMap<>();

            JKeys jkey = JedisonDao.getConfig().getKeys(this.redisKey);

            // 匹配该前缀的所有的redisKey
            Set<String> keys = jkey.keys(this.redisKey + "*");

            for (Map<String, Object> sqlMap : sqlMapList) {

                Object key = null;

                for (String hash : hashKey) {
                    Object hashString = sqlMap.get(hash);
                    if (key == null) {
                        key = hashString;
                    }
                    else {
                        key += "_" + hashString;
                    }
                }

                T t = null;

                List<Object> list = hashMap.get(key);

                if (list == null) {
                    list = new ArrayList<>();
                }

                if (Strings.isEmpty(valueName)) {
                    if (isMap2Bean) {
                        t = Objects.toObj(this.className.getMethod("Map2Bean", Map.class)
                                                        .invoke(this.className.newInstance(),
                                                                sqlMap),
                                          t);
                    }
                    else {
                        t = SmartHardUtil.mapToBean(sqlMap, this.className);
                    }
                }
                else {
                    t = Objects.toObj(sqlMap.get(valueName), t);
                }

                if (t != null) {
                    list.add(t);
                    hashMap.put(key, list);
                }

                hashMap.put(key, list);

            }

            for (Map.Entry<Object, List<Object>> entry : hashMap.entrySet()) {

                String key = this.redisKey + entry.getKey();

                String redisKeyNew = key + "_news";

                ISets<Object> sets = JedisonDao.getConfig().getSets(redisKeyNew);

                sets.sadd(entry.getValue());

                log.info("redisKey {} len {}", redisKeyNew, sets.scard());

                if (sets.scard() > 0) {
                    if (expiredTime > 0) {
                        sets.expire(expiredTime);
                    }
                    sets.rename(redisKeyNew, key);
                }
                else {
                    sets.del(key);
                }

            }

            List<Object> keyList = new ArrayList<>(hashMap.keySet());

            keyList.replaceAll(appId -> {
                return this.redisKey + appId;
            });

            keys.removeAll(keyList);
            jkey.del(keys);

        }
        catch (Exception e) {
            log.error("ReloadUtil reloadSet error :" + e);
        }

        return this;
    }

    public ReloadUtil<T> reloadList(String valueName, boolean isMap2Bean, String... methodNames) {

        try {

            String redisKeyNew = redisKey + "_news";

            IHash<Object, Object> redisHash = JedisonDao.getConfig().getHash(redisKeyNew);

            List<Map<String, Object>> sqlMapList = MySqlDao.getDao().queryAll(sql);

            Map<Object, List<Object>> redisMap = new HashMap<>();

            for (Map<String, Object> sqlMap : sqlMapList) {

                Object key = null;

                for (String hash : hashKey) {
                    Object hashString = sqlMap.get(hash);
                    if (key == null) {
                        key = hashString;
                    }
                    else {
                        key += "_" + hashString;
                    }
                }

                List<Object> list = redisMap.get(key);

                if (list == null) {
                    list = new ArrayList<>();
                }

                T t = null;

                if (Strings.isEmpty(valueName)) {
                    if (isMap2Bean) {
                        t = Objects.toObj(this.className.getMethod("Map2Bean", Map.class)
                                                        .invoke(this.className.newInstance(),
                                                                sqlMap),
                                          t);
                    }
                    else {
                        t = SmartHardUtil.mapToBean(sqlMap, this.className);
                    }
                }
                else {
                    t = Objects.toObj(sqlMap.get(valueName), t);
                }

                for (String methodName : methodNames) {
                    if (Strings.isNotEmpty(methodName)) {

                        Map<Object, Object> methodMap = Objects.toObj(this.dataMap.get(methodName),
                                                                      new HashMap<>());

                        methodMap.putAll(Objects.toObj(this.className.getMethod(methodName,
                                                                                className,
                                                                                Map.class)
                                                                     .invoke(t, t, methodMap),
                                                       new HashMap<>()));

                        this.dataMap.put(methodName, methodMap);
                    }

                }

                if (t != null) {
                    list.add(t);
                    redisMap.put(key, list);
                }

            }

            redisHash.hmset(new HashMap<>(redisMap));

            log.info("redisKey {} len {}", redisKey, redisHash.hlen());

            if (redisHash.hlen() > 0) {
                redisHash.rename(redisKeyNew, redisKey);
            }
            else {
                redisHash.del(redisKey);
            }
        }
        catch (Exception e) {
            log.error("ReloadUtil reload error :" + e);
        }

        return this;
    }

    public ReloadUtil<T> withReloadList(String redisKey, String methodName) {

        try {

            String redisKeyNew = redisKey + "_news";

            IHash<String, Object> redisHash = JedisonDao.getConfig().getHash(redisKeyNew);

            HashMap<String, Object> map = Objects.toObj(this.dataMap.get(methodName),
                                                        new HashMap<>());

            redisHash.hmset(map);

            log.info("redisKey {} len {}", redisKey, redisHash.hlen());

            if (redisHash.hlen() > 0) {
                redisHash.rename(redisKeyNew, redisKey);
            }
            else {
                redisHash.del(redisKey);
            }

        }
        catch (Exception e) {
            log.error("ReloadUtil reload error :" + e);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public ReloadUtil<T> withReloadSet(String redisKey, String methodName) {

        try {

            HashMap<Object, Object> map = Objects.toObj(this.dataMap.get(methodName),
                                                        new HashMap<>());

            JKeys jkey = JedisonDao.getConfig().getKeys(redisKey);

            // 匹配该前缀的所有的redisKey
            Set<String> keys = jkey.keys(redisKey + "*");

            for (Map.Entry<Object, Object> entry : map.entrySet()) {

                String key = redisKey + entry.getKey();

                String redisKeyNew = key + "_news";

                ISets<Object> redisSet = JedisonDao.getConfig().getSets(redisKeyNew);

                redisSet.sadd((List<Object>) entry.getValue());

                log.info("redisKey {} len {}", key, redisSet.scard());

                if (redisSet.scard() > 0) {
                    redisSet.rename(redisKeyNew, key);
                }
                else {
                    redisSet.del(key);
                }
            }

            List<Object> keyList = new ArrayList<>(map.keySet());

            keyList.replaceAll(appId -> {
                return redisKey + appId;
            });

            keys.removeAll(keyList);
            jkey.del(keys);

        }
        catch (Exception e) {
            log.error("ReloadUtil reload error :" + e);
        }

        return this;
    }

    public static <T> void reloadList(String sql,
                                      String redisKey,
                                      Class<T> className,
                                      String... hashKey) {

        try {

            String redisKeyNew = redisKey + "_news";

            IHash<String, Object> redisHash = JedisonDao.getConfig().getHash(redisKeyNew);

            List<Map<String, Object>> sqlMapList = MySqlDao.getDao().queryAll(sql);

            Map<String, List<Object>> redisMap = new HashMap<>();

            for (Map<String, Object> sqlMap : sqlMapList) {

                String key = "";

                for (String hash : hashKey) {
                    String hashString = Convert.toString(sqlMap.get(hash)).trim();
                    if (Strings.isEmpty(key)) {
                        key = hashString;
                    }
                    else {
                        key += "_" + hashString;
                    }
                }

                List<Object> list = redisMap.get(key);

                if (list == null) {
                    list = new ArrayList<>();
                }

                T t = SmartHardUtil.mapToBean(sqlMap, className);

                if (t != null) {
                    list.add(t);
                    redisMap.put(key, list);
                }

            }

            redisHash.hmset(new HashMap<>(redisMap));

            if (redisHash.hlen() > 0) {
                redisHash.rename(redisKeyNew, redisKey);
            }
            else {
                redisHash.del(redisKey);
            }
        }
        catch (Exception e) {
            log.error("ReloadUtil reload error :" + e);
        }

    }

}
