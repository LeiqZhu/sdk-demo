package com.biz.smarthard;


import com.biz.GBManager;
import com.biz.smarthard.bean.SmartHardStatus;
import com.biz.smarthard.handler.command.CommandServer;
import com.biz.smarthard.handler.pay.PayServer;
import com.biz.smarthard.handler.usercenter.UserCenterServer;
import com.sdk.core.json.JsonUtil;
import com.sdk.server.RequestHandlerV2;
import com.sdk.server.UrlMap;

public class SmartHardServer {

    public static class Index extends RequestHandlerV2 {
        @Override
        public void get() {
            write(this.getServer().getConfig().defaultWelcomeInfo
                    + ": "
                    + this.getServer().getConfig().port);
        }

        @Override
        public void post() {
            try {
                String str = this.getPostStr();

                long time2 = System.currentTimeMillis();
                JsonUtil.jsonToObject(str);
                long time3 = System.currentTimeMillis();
                log.debug("JsonUtil.jsonToObject Time consuming " + (time3 - time2) + "ms");

                sendMap.put("msg", "");
            }
            catch (Exception e) {
                log.error("Decode error:", e);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    public static void initUrlMap() {

        UrlMap.urlMap.put("/", Index.class);

        GBManager.initUrlMap();

        UserCenterServer.initUrlMap(); // 用户中心

        CommandServer.initUrlMap();//硬件指令

        PayServer.initUrlMap();//支付

    }
}
