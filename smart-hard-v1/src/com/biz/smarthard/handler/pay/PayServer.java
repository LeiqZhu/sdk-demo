package com.biz.smarthard.handler.pay;

import com.biz.smarthard.bean.SmartHardStatus;
import com.biz.smarthard.bean.redis.SHData;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.user.User;
import com.biz.smarthard.handler.SmartHardRequestHandler;
import com.biz.smarthard.handler.usercenter.RegistHandler;
import com.biz.smarthard.handler.usercenter.UpdateHandler;
import com.biz.smarthard.handler.usercenter.UserCenterHandler;
import com.biz.smarthard.utils.JacksonUtil;
import com.biz.smarthard.utils.PayCommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.json.JsonUtil;
import com.sdk.server.RequestHandler;
import com.sdk.server.RequestHandlerV2;
import com.sdk.server.UrlMap;
import snowfox.lang.time.DateUtil;
import snowfox.lang.util.Convert;
import snowfox.lang.util.Strings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class PayServer {

    /**
     * 支付宝App支付接口
     * 通过此接口传入订单参数，同时唤起支付宝客户端
     *
     */
    public static class AliPayAppTradePay extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

            try {
                log.debug("SH AliPayRequest recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = true;

                if (ret) {
                    sendMap.put("result",0);
                    sendMap.put("msg", SmartHardStatus.SUCCESS);
                }else {
                    sendMap.put("result",-1);
                }

                log.debug("SH AliPayRequest sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH AliPayRequest error: " + e);
                sendMap.put("result",-1);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    /**
     * 交易关闭接口
     * 通过此接口关闭此前已创建的交易，关闭后，用户将无法继续付款。
     * 仅能关闭创建后未支付的交易
     */
    public static class AliPayTradeClose extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

        }
    }

    /**
     * 交易状态查询接口
     * 通过此接口查询某笔交易的状态，交易状态：
     * 交易创建，等待买家付款；
     * 未付款交易超时关闭，或支付完成后全额退款；
     * 交易支付成功；交易结束，不可退款。
     */
    public static class AliPayTradeQuery extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {
            try {
                log.debug("SH AliPayTradeQuery recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = AliPayHandler.tradeQuery(recMap, sendMap);

                if (ret) {
                    sendMap.put("result",0);
                }else {
                    sendMap.put("result",-1);
                }

                log.debug("SH AliPayTradeQuery sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH AliPayTradeQuery error: " + e);
                sendMap.put("result",-1);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    /**
     * 交易退款接口
     * 通过此接口对单笔交易完成退款操作
     */
    public static class AliPayTradeRefund extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

        }
    }

    /**
     * 退款查询接口
     * 查询退款订单的状态
     */
    public static class AliPayTradeRefundQuery extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

        }
    }

    /**
     * 账单查询接口
     * 调用此接口获取账单的下载链接
     */
    public static class AliPayDataBillQuery extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

        }
    }

    /**
     * AliPayTradeNotify
     *
     */
    public static class AliPayTradeNotify extends RequestHandler {
        @Override
        public void post() {
            try {
                Map<String, String> recMap = new HashMap();

                byte[] content = this.getHttpContent();

                log.info("收到支付宝异步回调通知 ： " + new String(content));

                //recMap = JsonUtil.jsonToObject(content);

                recMap = PayCommonUtil.str2Map(new String(content));

                log.debug("SH AliPayTradeNotify recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = AliPayHandler.tradeNotify(recMap, sendMap);
                //boolean ret = AliPayHandler.tradeNotifyTest(recMap, sendMap);

                if (ret) {
                    sendMap.put("response","success");
                }else {
                    sendMap.put("response","failure");
                }

                log.debug("SH AliPayTradeNotify sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH AliPayTradeNotify error: " + e);
                sendMap.put("response","failure");
            }
            write(sendMap);
        }
    }

    /**
     * WXUnifiedOrder
     *
     */
    public static class WXUnifiedOrder extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

            try {
                log.debug("SH WXPayRequest recMap: " + JsonUtil.objectToJson(recMap));



                log.debug("SH WXPayRequest sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH WXPayRequest error: " + e);
                sendMap.put("result",-1);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    /**
     * WXOrderQuery
     *
     */
    public static class WXOrderQuery extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {

            try {
                log.debug("SH WXOrderQuery recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = WXPayHandler.WXOrderQuery(recMap, sendMap);

                if (ret) {
                    sendMap.put("result",0);
                    sendMap.put("msg", SmartHardStatus.SUCCESS);
                }else {
                    sendMap.put("result",-1);
                }

                log.debug("SH WXOrderQuery sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH WXOrderQuery error: " + e);
                sendMap.put("result",-1);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    /**
     * WXNotify
     *
     */
    public static class WXNotify extends RequestHandlerV2 {
        public void doSHPost(Map<String, Object> recMap) {

            try {
                log.debug("SH WXnotify recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = WXPayHandler.WXnotify(recMap, sendMap);

                if (ret) {
                    sendMap.put("return_code","SUCCESS");
                    sendMap.put("return_msg", "OK");
                }else {
                    sendMap.put("return_code","FAIL");
                }

                log.debug("SH WXnotify sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH WXnotify error: " + e);
                sendMap.put("return_code","FAIL");
                sendMap.put("return_msg", "服务器异常");
            }
            String return_code = Convert.toString(sendMap.get("return_code"));
            String return_msg = Convert.toString(sendMap.get("return_msg"));
            String response = PayCommonUtil.setXML(return_code,return_msg);
            write(response);
        }

        @Override
        public void post(byte[] content) throws UnsupportedEncodingException {
            Map<String, Object> map = null;
            try {
                String conStr = new String(content,"UTF-8");
                map = JacksonUtil.xml2Obj(conStr);
            }catch (Exception e){

            }
            doSHPost(map);
        }
    }

    /**
     * AppNotify
     *
     */
    public static class AppNotify extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {
            try {
                log.debug("SH AppNotify recMap: " + JsonUtil.objectToJson(recMap));

                IHash<String,Object> notyfyHash = JedisonDao.getData().getHash(SHData.Data_App_Notify);

                notyfyHash.hset(DateUtil.now(),recMap);

                sendMap.put("audit_status","success");

                log.debug("SH AppNotify sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH AppNotify error: " + e);
                sendMap.put("audit_status","failure");
            }
            write(sendMap);
        }
    }

    /**
     * PayRequest
     *
     */
    public static class PayRequest extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {
            try {
                log.debug("SH PayRequest recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = PayHandler.payRequest(recMap,sendMap);

                if (ret) {
                    sendMap.put("result",0);
                    sendMap.put("msg", SmartHardStatus.SUCCESS);
                }else {
                    sendMap.put("result",-1);
                }

                log.debug("SH PayRequest sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH PayRequest error: " + e);
                sendMap.put("result",-1);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    /**
     * TradeQuery
     *
     */
    public static class TradeQuery extends SmartHardRequestHandler {
        @Override
        public void SHPost(Map<String, Object> recMap) {
            try {
                log.debug("SH TradeQuery recMap: " + JsonUtil.objectToJson(recMap));

                boolean ret = PayHandler.tradeQuery(recMap,sendMap);

                if (ret) {
                    sendMap.put("result",0);
                    sendMap.put("msg", SmartHardStatus.SUCCESS);
                }else {
                    sendMap.put("result",-1);
                }

                log.debug("SH TradeQuery sendMap: " + JsonUtil.objectToJson(sendMap));
            }
            catch (Exception e) {
                log.error("SH TradeQuery error: " + e);
                sendMap.put("result",-1);
                sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            }
            write(sendMap);
        }
    }

    public static void initUrlMap() {

        //支付宝App支付接口
        UrlMap.urlMap.put("/smarthard/alipay/trade/app/pay",AliPayAppTradePay.class);

        //支付宝交易关闭接口
        UrlMap.urlMap.put("/smarthard/alipay/trade/close",AliPayTradeClose.class);

        //支付宝交易状态查询接口
        UrlMap.urlMap.put("/smarthard/alipay/trade/query",AliPayTradeQuery.class);

        //交易退款接口
        UrlMap.urlMap.put("/smarthard/alipay/trade/refund",AliPayTradeRefund.class);

        //退款查询接口
        UrlMap.urlMap.put("/smarthard/alipay/trade/refund/query",AliPayTradeRefundQuery.class);

        //账单查询接口
        UrlMap.urlMap.put("/smarthard/alipay/data/dataservice/bill/downloadurl/query",AliPayDataBillQuery.class);

        //支付宝异步支付通知接口
        UrlMap.urlMap.put("/smarthard/alipay/trade/notify",AliPayTradeNotify.class);

        //应用网关
        UrlMap.urlMap.put("/smarthard/notify",AppNotify.class);


        //微信异步支付通知接口
        UrlMap.urlMap.put("/smarthard/wxpay/WXNotify",WXNotify.class);

        //微信统一下单接口
        UrlMap.urlMap.put("/smarthard/wxpay/WXUnifiedOrder",WXUnifiedOrder.class);

        //微信查询支付结果接口
        UrlMap.urlMap.put("/smarthard/wxpay/WXOrderQuery",WXOrderQuery.class);

        //支付统一接口
        UrlMap.urlMap.put("/smarthard/pay",PayRequest.class);

        //订单查询接口
        UrlMap.urlMap.put("/smarthard/trade/query",TradeQuery.class);

    }

    public static void main(String[] args) throws IOException {
//        String content = "{\"id\":\"id_123456\",\"ts\":1517467796159,\"userName\": \"user001\",\"deviceId\": \"08666660301111111\"}";
//        String result = SmartHardUtil.doMifiPost("/mapi_v1/device_auth",JsonUtil.jsonToObject(content),"utf-8");
//        System.out.println(result);

        IHash<String, User> rUserToken = JedisonDao.getData().getHash(SHData.Data_Cur_UserId_UserMap);

        System.out.println(rUserToken.hlen());

        System.out.println(rUserToken.hkeys());

        System.out.println(rUserToken.hget("VtSOvtEtQc6nwHbEIqfYsA"));

    }

}
