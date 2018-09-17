package com.biz.smarthard.handler.usercenter;

import com.biz.smarthard.bean.SHConfig;
import com.biz.smarthard.bean.SmartHardStatus;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.user.RealInfo;
import com.biz.smarthard.handler.SmartHardRequestHandler;
import com.biz.smarthard.utils.SmartHardUtil;
import com.sdk.core.cache.jedis.core.type.JStrings;
import com.sdk.core.cache.jedis.core.type.raw.JBucketString;
import com.sdk.core.json.JsonUtil;
import com.sdk.server.RedirectRequestHandler;
import com.sdk.server.UrlMap;
import snowfox.lang.util.Strings;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

public class UserCenterServer {

    /**
     * 1．用户注册
     *
     */
    public static class RegistRequest extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

            try {
                log.debug("SH regist recMap: " + JsonUtil.objectToJson(recMap));

                // 注册用户 ，默认没有返回0
                String userToken = RegistHandler.registUser(recMap, sendMap);

                if (Strings.isNotEmpty(userToken)) {
                    // 注册成功
                    sendMap.put("msg", SmartHardStatus.SUCCESS);
                }
                sendMap.put("user_token", userToken);

                log.debug("SH regist sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH regist error: " + e);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    /**
     * 1．FAQ
     *
     */
    public static class FAQRequest extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

            try {
                log.debug("SH FAQ recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = UserCenterHandler.FAQRequest(recMap, sendMap);

                if (ret) {
                    sendMap.put("msg", SmartHardStatus.SUCCESS);
                }
                log.debug("SH FAQ sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH FAQ error: " + e);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    /**
     * update
     *
     */
    public static class UpdateRequest extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

            try {
                log.debug("SH Update recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = UpdateHandler.UpdateRequest(recMap, sendMap);

                if (ret) {
                    sendMap.put("msg", SmartHardStatus.SUCCESS);
                }

                log.debug("SH Update sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH Update error: " + e);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    /**
     * realinfo
     *
     */
    public static class RealInfoRequest extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

            try {
                log.debug("SH RealInfo recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = UserCenterHandler.realInfoRequest(recMap, sendMap);

                if (ret) {
                    sendMap.put("result",0);
                    sendMap.put("msg", SmartHardStatus.SUCCESS);
                }else {
                    sendMap.put("result",-1);
                }

                log.debug("SH RealInfo sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH RealInfo error: " + e);
                sendMap.put("result",-1);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    /**
     * DownLibRequest 下载内核链接重定向
     *
     */
    public static class DownLibRequest extends RedirectRequestHandler {

        @Override
        public void get() {

            JStrings libUrlString = JedisonDao.getConfig().getStrings(SHConfig.Lib_Url);

            String libUrl = libUrlString.get();

            writeDirect(libUrl);
        }

    }

    public static void initUrlMap() {

        //新用户注册
        UrlMap.urlMap.put("/sdk/smarthard/regist",RegistRequest.class);

        //FAQ
        UrlMap.urlMap.put("/sdk/smarthard/faq",FAQRequest.class);

        //更新
        UrlMap.urlMap.put("/sdk/smarthard/update",UpdateRequest.class);

        //实名认证
        UrlMap.urlMap.put("/sdk/smarthard/realinfo",RealInfoRequest.class);

        //下载内核链接
        UrlMap.urlMap.put("/sdk/smarthard/download",DownLibRequest.class);

    }

    public static void main(String[] args) throws IOException {
//        String content = "{\"id\":\"id_123456\",\"ts\":1517467796159,\"userName\": \"user001\",\"deviceId\": \"08666660301111111\"}";
//        String result = SmartHardUtil.doMifiPost("/mapi_v1/device_auth",JsonUtil.jsonToObject(content),"utf-8");
//        System.out.println(result);

    }

}
