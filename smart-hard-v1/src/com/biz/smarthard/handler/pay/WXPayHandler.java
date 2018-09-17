package com.biz.smarthard.handler.pay;

import com.biz.smarthard.bean.redis.SHData;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.pay.OrderDetail;
import com.biz.smarthard.entity.pay.Packages;
import com.biz.smarthard.entity.pay.TradeDetail;
import com.biz.smarthard.entity.user.User;
import com.biz.smarthard.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.http.HttpToolkit;
import com.sdk.core.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.codec.MD5;
import snowfox.lang.time.DateFormats;
import snowfox.lang.time.DateUtil;
import snowfox.lang.util.Convert;
import snowfox.lang.util.R;
import snowfox.lang.util.Strings;

import java.io.IOException;
import java.util.*;

public class WXPayHandler {

    private static final Logger log = LoggerFactory.getLogger(WXPayHandler.class);
    public static Properties properties = PropertyUtil.getInstance();
    public static final String APPID = properties.getProperty("WXPay.appid");
    public static final String KEY = properties.getProperty("WXPay.key");
    public static final String MCHID = properties.getProperty("WXPay.mch_id");
    public static final String WXUrl = properties.getProperty("WXPay.unifiedorderUrl");

    public static boolean WXOrderQuery(Map<String, Object> recMap, Map<String, Object> sendMap){
        boolean ret = true;
        try {

            if(SmartHardUtil.checkUsertoken(recMap,sendMap,"user_token")){
                return false;
            }
            String param = getOrderQueryParam(recMap);

            String response = HttpToolkit.doPost(PropertyUtil.getInstance().getProperty("WXPay.orderqueryUrl"),param);
            Map<String,Object> resMap = JacksonUtil.xml2Obj(response);
            OrderDetail orderDetail = JacksonUtil.jackson.withCamel2Lower()
                    .withIgnoreUnknowPro()
                    .obj2Bean(resMap, OrderDetail.class);
            IHash<String,Object> orderDetailHash = JedisonDao.getData().getHash(SHData.Data_Cur_OrderDetail + DateUtil.today());
            orderDetailHash.hset(orderDetail.out_trade_no,orderDetail);

            sendMap.put("order_detail",orderDetail);

        }catch (Exception e){
            e.printStackTrace();
            log.error("SH OrderQuery error : " + e);
        }
        return ret;
    }

    private  static String getOrderQueryParam(Map<String, Object> recMap){
        String param = "";
        try {
            Map<String,Object> reqMap = (Map<String, Object>) recMap.get("reqMap");

            SortedMap<Object,Object> paramMap = new TreeMap<Object,Object>();
            //微信开放平台审核通过的应用APPID
            paramMap.put("appid", APPID);
            //微信支付分配的商户号
            paramMap.put("mch_id",MCHID);
            //微信订单号 优先使用
            String transaction_id = Convert.toString(reqMap.get("transaction_id"));
            paramMap.put("transaction_id",transaction_id);
            if (Strings.isEmpty(transaction_id)){
                paramMap.put("out_trade_no",Convert.toString(reqMap.get("out_trade_no")));
            }
            paramMap.put("nonce_str",R.UU16());
            String sign = PayCommonUtil.createSign(paramMap);
            paramMap.put("sign",sign);

            param = PayCommonUtil.getRequestXml(paramMap);

        }catch (Exception e){
            e.printStackTrace();
            log.error("SH getUnifiedOrderParam error : " + e);
        }
        return param;
    }

    public static boolean WXnotify(Map<String, Object> recMap, Map<String, Object> sendMap){
        boolean ret = true;
        try{
            String outTradeNo = Convert.toString(recMap.get("out_trade_no"));
            IHash<String,Map<String,Object>> orderHash = JedisonDao.getData().getHash(SHData.Data_Cur_OrderMap + DateUtil.today());
            Map<String,Object> orderMap = orderHash.hget(outTradeNo);
            if (orderMap !=null){
                if (!Convert.toBool(orderMap.get("is_do"))){
                    //对支付结果通知的内容做签名验证,并校验返回的订单金额是否与商户侧的订单金额一致
                    String totalFee = Convert.toString(recMap.get("total_fee"));
                    String checkTotalFee = Convert.toString(orderMap.get("total_fee"));
                    if (!totalFee.equals(checkTotalFee)){
                        sendMap.put("return_msg", "总金额错误");
                        return false;
                    }
                    if (!PayCommonUtil.isTenpaySign(recMap)){
                        sendMap.put("return_msg", "签名失败");
                        return false;
                    }

                    orderMap.put("is_do",true);
                }
            }else {
                sendMap.put("return_msg", "订单不存在");
                return false;
            }
        }catch (Exception e){
            sendMap.put("return_msg", "服务器异常");
            return false;
        }
        return ret;
    }

    public static boolean payRequest(Packages pk, String deviceId, User user, Map<String, Object> sendMap,String clientIP){

        boolean ret = true;
        try {

            String param = getUnifiedOrderParam(pk,user,deviceId,clientIP);

            String response = HttpToolkit.doPost(WXUrl,param);
            Map<String,Object> resMap = JacksonUtil.xml2Obj(response);

            String prepayid = Convert.toString(resMap.get("prepayid"));

            String returnCode = Convert.toString(resMap.get("return_code"));
            if ("SUCCESS".equals(returnCode.toUpperCase())){
                SortedMap<Object, Object> signMap = new TreeMap<>();
                signMap.put("appid",Convert.toString(resMap.get("appid")));
                signMap.put("partnerid",Convert.toString(resMap.get("partnerid")));
                signMap.put("prepayid",Convert.toString(resMap.get("prepayid")));
                signMap.put("noncestr",Convert.toString(resMap.get("noncestr")));
                signMap.put("package",Convert.toString(resMap.get("package")));
                String sign = PayCommonUtil.createSign(signMap);
                resMap.put("sign",sign);
            }
            sendMap.putAll(resMap);
            if (Strings.isNotEmpty(prepayid)){

                resMap.put("device_id",deviceId);

                IHash<String,Map<String,Object>> orderHash = JedisonDao.getData().getHash(SHData.Data_Cur_OrderMap + DateUtil.today());
                IHash<String,Map<String,String>> deviceOrderHash = JedisonDao.getData().getHash(SHData.Data_Device_Order );
                Map<String,String> checkMap = deviceOrderHash.hget(deviceId);
                String outTradeNo = checkMap.get("out_trade_no");
                String total_fee = checkMap.get("total_fee");
                resMap.put("total_fee",total_fee);
                resMap.put("is_do",false);
                orderHash.hset(outTradeNo, resMap);
                if (orderHash.ttl() == -1){
                    orderHash.expire(DateUtil.calcNext00Diff() + 600);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("SH unifiedOrder error : " + e);
        }
        return ret;

    }
    private  static String getUnifiedOrderParam(Packages pk, User user, String deviceId, String clientIP){
        String param = "";
        try {
            SortedMap<Object,Object> paramMap = new TreeMap<Object,Object>();
            //微信开放平台审核通过的应用APPID
            paramMap.put("appid", APPID);
            //微信支付分配的商户号
            paramMap.put("mch_id",MCHID);
            //终端设备号(门店号或收银设备ID)，默认请传"WEB"
            //paramMap.put("device_info","1230000109");
            //随机字符串，不长于32位。
            paramMap.put("nonce_str", R.UU16());
            //签名类型
            paramMap.put("sign_type","MD5");
            //商品描述 : APP——需传入应用市场上的APP名字-实际商品名称，天天爱消除-游戏充值。
            String subject = pk.getPackage_name();
            paramMap.put("body", subject);
            //商品详情
            String description = pk.getPackage_name();
            paramMap.put("detail",description);
            //附加数据
            //paramMap.put("attach",Convert.toString(reqMap.get("attach")));
            //商户订单号 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
            String outTradeNo = PayCommonUtil.getOutTradeNo();
            paramMap.put("out_trade_no",outTradeNo);
            //货币类型 默认人民币：CNY
            paramMap.put("fee_type","CNY");
            //总金额 前端默认单位为元，微信 是分为单位
            String totalAmount = Convert.toString(pk.getPrice() * pk.getCount());
            int total_fee = Convert.toInt(totalAmount) * 100;
            paramMap.put("total_fee",total_fee);
            //终端IP
            paramMap.put("spbill_create_ip",clientIP);
            //交易起始时间
            //paramMap.put("time_start", DateFormats.formatDT(new Date()));
            //交易结束时间
            //paramMap.put("time_expire",DateFormats.formatDT(new Date()));
            //订单优惠标记
            //paramMap.put("goods_tag","1230000109");
            //通知地址
            paramMap.put("notify_url","http://"+ PayCommonUtil.getAddressIP() + "/sdk/smarthard/WXnotify");
            //交易类型
            paramMap.put("trade_type","APP");
            //指定支付方式
            //paramMap.put("limit_pay","1230000109");
            //场景信息
            //paramMap.put("scene_info","1230000109");
            //签名
            paramMap.put("sign",PayCommonUtil.createSign(paramMap));

            param = PayCommonUtil.getRequestXml(paramMap);

            //param = JsonUtil.objectToJson(paramMap);

            log.debug("请求参数:" + JsonUtil.objectToJson(paramMap));

            IHash<String,Object> deviceOrderHash = JedisonDao.getData().getHash(SHData.Data_Device_Order);

            TradeDetail tradeDetail = new TradeDetail();
            tradeDetail.setId(tradeDetail.getId());
            tradeDetail.setUserId(user.getUserId());
            tradeDetail.setDeviceId(deviceId);
            tradeDetail.setBody(description);
            tradeDetail.setSubject(subject);
            tradeDetail.setTotalAmount(totalAmount);
            tradeDetail.setOutTradeNo(outTradeNo);
            tradeDetail.setTradeType(2);
            tradeDetail.setTradeState(1);
            tradeDetail.setCreateTime(DateUtil.now());
            tradeDetail.setRechargeState(0);

            deviceOrderHash.hset(deviceId,tradeDetail);
            if (deviceOrderHash.ttl() == -1){
                deviceOrderHash.expire(DateUtil.calcNext00Diff() + 600);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("SH getUnifiedOrderParam error : " + e);
        }
        return param;
    }

    public static void main(String[] args) {
        Map<String,Object> reqMap = new HashMap<>();
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("device_id","8666660301111111");
        Map<String,Object> recMap = new HashMap<>();
        Map<String,Object> sendMap = new HashMap<>();
        recMap.put("client_ip","157.37.200.88");
        reqMap.put("body","天天爱消除-游戏充值");
        reqMap.put("detail","商品详情");
        reqMap.put("attach","附加数据");
        //总金额
        reqMap.put("total_fee","1.0");


        recMap.put("reqMap",reqMap);
        recMap.put("data",dataMap);
        System.out.println();

        //System.out.println(getUnifiedOrderParam(recMap));


        Map<String,Object> orderMap = new HashMap<>();
        //orderMap.put("total_fee",1);
        //orderMap.put("is_do",false);
        //
        //IHash<String,Map<String,Object>> orderHash = JedisonDao.getData().getHash(SHData.Data_Cur_OrderMap + DateUtil.today());
        //orderHash.hset("1409811653", orderMap);
        //if (orderHash.ttl() == -1){
        //    orderHash.expire(DateUtil.calcNext00Diff() + 600);
        //}
    }
}
