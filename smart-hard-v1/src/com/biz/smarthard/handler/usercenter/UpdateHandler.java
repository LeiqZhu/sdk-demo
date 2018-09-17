package com.biz.smarthard.handler.usercenter;

import com.biz.smarthard.bean.SHConfig;
import com.biz.smarthard.bean.SmartHardStatus;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.ver.VerLib;
import com.biz.smarthard.utils.SmartHardUtil;
import com.sdk.core.cache.type.IHash;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.nutz.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.util.Convert;
import snowfox.lang.util.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateHandler.class);

    // 内核更新
    public static boolean UpdateRequest(Map<String, Object> recMap, Map<String, Object> sendMap) {

        try {
            // 检查 参数 user_token
            //if (!SmartHardUtil.checkUsertoken(recMap, sendMap, "user_token")) {
            //    return false;
            //}
            String token = Convert.toString(recMap.get("user_token"));
            String channel = Convert.toString(recMap.get("channel"));
            String device = Convert.toString(recMap.get("device"));
            if (Strings.isEmpty(channel) || Strings.isEmpty(device)) {
                sendMap.put("msg", SmartHardStatus.PARAM_ERROR);
                return false;
            }
            sendMap.put("libs", getNewLib(channel, device, recMap));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("FAQRequest error :", ExceptionUtils.getFullStackTrace(e));
            sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            return false;
        }
    }

    private static List<Map<String,Object>> getNewLib(String channel,
                                                       String device,
                                                       Map<String, Object> recMap) throws Exception {

        List<Map<String, Object>> recList = Objects.toObj(recMap.get("libs"),
                new ArrayList<Map<String, Object>>());
        List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
        if (recList == null) {
            return resList;
        }
        IHash<String, Map<String, Object>> updateLibHash = JedisonDao.getData().getHash(SHConfig.VerUpdate);
        String key = channel + "_" + device;

        VerLib verLib = (VerLib) updateLibHash.hget(key);
        if (verLib == null) {
            return resList;
        }
        String url = "";
        long crc32 = 0L;
        String libVersion = "";
        String libName = "";
        for (Map<String, Object> map : recList) {
            if (map != null) {
                String version = Convert.toString(map.get("libVersion"));
                int data = 0;

                // 将版本进行比较
                try {
                    data = SmartHardUtil.compareVersion(verLib.getLibVersion(), version);
                }
                catch (Exception e) {
                    log.error("compareVersion error!");
                }
                Map<String, Object> lib = new HashMap<>();

                // 若不需要更新，则不下发url，其他的版本参数正常下发
                if (data > 0) {
                    lib.put("lib_name",verLib.getLibName());
                    lib.put("lib_version", verLib.getLibVersion());
                    lib.put("crc32", verLib.getCrc32());
                    lib.put("lib_url", verLib.getUrl());
                    lib.put("desc",verLib.getDesc());
                    resList.add(lib);
                }
            }
        }
        return resList;
    }
}
