package com.biz.smarthard.handler.command;

import com.biz.smarthard.bean.SmartHardStatus;
import com.sdk.core.json.JsonUtil;
import com.sdk.server.RequestHandlerV2;
import com.sdk.server.UrlMap;

import java.util.HashMap;
import java.util.Map;

public class CommandServer {

    /**
     * 硬件指令
     *
     */
    public static class CommandRequest extends RequestHandlerV2 {
        @Override
        public void post(Map<String, Object> recMap) {

            if (recMap==null){
                recMap = new HashMap<>();
            }
            try {
                log.debug("MF CommandRequest recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = CommandHandler.doCommand(recMap,sendMap);
                if (ret){
                    sendMap.put("msg", "");
                }
                log.debug("MF CommandRequest sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("MF CommandRequest error: " + e);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    public static void initUrlMap() {

        //硬件指令统一接口
        UrlMap.urlMap.put("/sdk/smarthard/command/hardware", CommandRequest.class);

    }
}
