package com.biz.smarthard;

import com.biz.smarthard.bean.SHConfig;
import com.biz.smarthard.bean.SHDbsql;
import com.biz.smarthard.bean.redis.SHId;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.RedisId;
import com.biz.smarthard.entity.conf.FAQ;
import com.biz.smarthard.entity.ver.VerLib;
import com.biz.smarthard.utils.ReloadUtil;
import com.sdk.core.cache.jedis.core.type.JStrings;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.db.MySqlDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.util.Arrays;
import snowfox.lang.util.Convert;

import java.util.*;

public class InitConfig {

    static final Logger log = LoggerFactory.getLogger(InitConfig.class);

	public static void main(String[] args) {
	    try {
	        String[] array = {"sh_ver_libs"};
            reloadConf(array,false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public static void initData() throws Exception {

        for (Map.Entry<String, RedisId> entry : SHId.redisIdMap.entrySet()) {
            entry.getValue().initId(entry.getKey());
        }

    }

    public static void initconf() throws Exception {

        initData();

        reloadConf(null, false);
    }

    public static boolean reloadconfByName(String[] arrayNames) throws Exception {

        try {
            reloadConf(arrayNames, true);
        }
        catch (Exception ex) {
            log.error("reload Exception: ", ex);
            return false;
        }

        return true;
    }

    public static void reloadConf(String[] arrayNames, Boolean isReload) throws Exception {

        List<String> names = null;
        if (arrayNames != null) {
            names = Arrays.toList(arrayNames);
        }

        try {
            if (names != null && names.contains("sh_faq")) {
                // faq内容配置
                new FAQ().reloadTable();
            }
        }
        catch (Exception e) {
            log.error("cached sh_faq error : " + e);
        }

        try {
            if (names != null && names.contains("sh_video_url")) {
                new ReloadUtil<>(
                        SHDbsql.FAQ.QUERYAQVideo_ALL_SQL,
                        SHConfig.AQV,
                        null,
                        "device"
                ).reload();
            }
        }
        catch (Exception e) {
            log.error("cached sh_faq error : " + e);
        }

        try {
            if (names != null && names.contains("sh_ver_update")) {
                new VerLib().reloadTable();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("cache table 'sh_ver_update' error : " + e);
        }

        try {
            if (names != null && names.contains("sh_ver_libs")){

                Map<String,Object> map = MySqlDao.getDao().queryOne(SHDbsql.LibUrl.QUERY_One_SQL,new Object[]{});
                JStrings url = JedisonDao.getConfig().getStrings(SHConfig.Lib_Url);

                url.set(Convert.toString(map.get("url")));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("cache table 'sh_ver_libs' error : " + e);
        }
    }

}
