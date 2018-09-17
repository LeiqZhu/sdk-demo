package com.biz.smarthard.handler.command;

import com.biz.smarthard.bean.SmartHardStatus;
import com.biz.smarthard.entity.pay.TradeDetail;
import com.biz.smarthard.utils.ShaUtil;
import com.biz.smarthard.utils.SmartHardUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sdk.core.http.HttpClientUtil;
import com.sdk.core.json.JsonUtil;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.time.DateUtil;
import snowfox.lang.util.Convert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    static Logger log = LoggerFactory.getLogger(CommandHandler.class);

    private static final String BASE_URL = "http://mapi.cloudtone.com.cn:8790";

    private static final String UID = "04b659f0-f9d1-11e7-aeae-68f7288efbcf";
    private static final String KEY = "43edab9e-f9d1-11e7-a4eb-68f7288efbcf43edab9e-f9d1-11e7-a4eb-68f7288efbcf";

    public static boolean doRecharge(TradeDetail tradeDetail) throws IOException {

        boolean ret = true;
        //套餐购买
        String url = "/mapi_v1/device_recharge";
        Map<String, Object> data = new HashMap<>();
        data.put("id",tradeDetail.getId());
        data.put("ts",tradeDetail.getPayTime());
        data.put("id",tradeDetail.getId());
        data.put("order_no",tradeDetail.getOutTradeNo());
        data.put("device_id",tradeDetail.getDeviceId());
        data.put("packages",tradeDetail.getPackages());

        String resStr = doMifiPost(url, data, "utf-8");

        Map<String,Object> resultMap = JsonUtil.jsonToObject(resStr);

        String result = Convert.toString(resultMap.get("result"));
        if ("0".equals(result)){
            tradeDetail.setRechargeState(1);
            tradeDetail.setPayTime(DateUtil.now());
        }else {
            ret = false;
        }
        return ret;
    }

    public static String doMifiPost(String url,Map<String,Object> contentMap,String charSet) throws JsonProcessingException, UnsupportedEncodingException {

        byte[] result = null;
        try{
            contentMap.remove("client_ip");
            contentMap.remove("client_ua");
            contentMap.remove("country_code");

            String str = JsonUtil.objectToJson(contentMap);
            //System.out.println(str);

            SmartHardUtil.checkDeviceId(contentMap);

            str = JsonUtil.objectToJson(contentMap);
            System.out.println(str);
            byte[] content = str.getBytes(charSet);

            String sign = "";
            sign = ShaUtil.encryptHmacSHA512ToString(str,KEY);
//            sign = HMAC.encryptHMAC(str,KEY);
            System.out.println(sign);

            Header[] headers = new Header[]{new BasicHeader("Content-type", "application/x-www-form-urlencoded")
                    ,new BasicHeader("uid",UID)
                    ,new BasicHeader("sign",sign)};

            result = HttpClientUtil.doPost(BASE_URL + url, content, headers,5000,5000);

        }catch (Exception e){
            log.error("SmartHardUtil doPost error:" + e);
        }
        return new String(result,charSet);
    }

    /**
     * @param recMap
     * @param sendMap
     * @return
     */
    public static boolean doCommand(Map<String, Object> recMap, Map<String, Object> sendMap) {
        Map<String,Object> map = new HashMap<>();
        try {

            // 检查 参数 user_token
            //if (!SmartHardUtil.checkUsertoken(recMap,sendMap,"user_token")) {
            //    return false;
            //}

            String channel = Convert.toString(recMap.get("channel"));
            String device = Convert.toString(recMap.get("device"));
            String command = Convert.toString(recMap.get("command"));
            Map<String,Object> dataMap = (Map<String, Object>) recMap.get("data");

            switch (channel){
                case "cloudtone":
                    map = operate(device,command,dataMap);
                    break;
                default:
                    break;
            }
            sendMap.putAll(map);

        }catch (Exception e){
            log.error("MF doCommand error: " + e);
            sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            return false;
        }
        return true;
    }

    public static Map<String,Object> operate(String device,String command,Map<String,Object> data){
        Map<String,Object> resultMap = new HashMap<>();
        try {
            String result = "";
            switch (device) {
                case "mifi":
                    result = CommandHandler.doMifiPost("/mapi_v1/" + command, data, "utf-8");
                    break;
                default:
                    break;
            }
            System.out.println(result);
            resultMap = JsonUtil.jsonToObject(result);
        }catch (Exception e){
            log.error("MF operate error: " + e);
        }
        return resultMap;
    }
}

