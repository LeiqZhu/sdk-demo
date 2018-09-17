package com.biz.smarthard.handler.pay;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.biz.smarthard.bean.SmartHardStatus;
import com.biz.smarthard.bean.redis.SHBuffer;
import com.biz.smarthard.bean.redis.SHData;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.pay.OrderDetail;
import com.biz.smarthard.entity.pay.Packages;
import com.biz.smarthard.entity.pay.TradeDetail;
import com.biz.smarthard.entity.user.User;
import com.biz.smarthard.handler.command.CommandHandler;
import com.biz.smarthard.utils.PayCommonUtil;
import com.biz.smarthard.utils.PropertyUtil;
import com.biz.smarthard.utils.RSAUtil;
import com.biz.smarthard.utils.SmartHardUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.json.JsonUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.time.DateUtil;
import snowfox.lang.util.Convert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.*;


public class AliPayHandler {

    private static final Logger log = LoggerFactory.getLogger(AliPayHandler.class);

    private static final String alipaypublicKey = PropertyUtil.getInstance().getProperty("AliPay.ALIPAY_PUBLIC_KEY");

    public static boolean tradeQuery(Map<String, Object> recMap, Map<String, Object> sendMap){
        // 查询状态
        boolean flag = true;
        String trade_status = "";
        String out_trade_no = "";
        try{

            Map<String,Object> dataMap = (Map<String, Object>) recMap.get("data");
            String deviceId = Convert.toString(dataMap.get("device_id"));
            out_trade_no = Convert.toString(dataMap.get("out_trade_no"));
            if (SmartHardUtil.isParamNull(dataMap,sendMap,"device_id","out_trade_no")){
                flag = false;
            }

            Map<String,Object> resMap = tradeQueryOperate(out_trade_no);

            Map<String,String> tradeResMap = (Map<String, String>) resMap.get("alipay_trade_query_response");

            String code = tradeResMap.get("code");

            if ("10000".equals(code)){
                trade_status = tradeResMap.get("trade_status");

                sendMap.put("msg", SmartHardStatus.SUCCESS);
            }else {
                sendMap.put("msg",tradeResMap.get("msg"));
            }

        }catch (Exception e){
            log.error("AliPayHandler tradeQuery error " + ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            sendMap.put("msg","server error");
            return false;
        }finally {
            sendMap.put("trade_status",trade_status);
            sendMap.put("out_trade_no",out_trade_no);
        }
        return flag;
    }
    public static Map<String,Object> tradeQueryOperate(String outTradeNo){
        Map<String,Object> resMap = new HashMap<>();
        try{
            //通过工具类实例化客户端，读取参数文件pay.properties
            AlipayClient alipayClient = PayCommonUtil.getAliClient();

            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.query
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

            //设置业务参数
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();

            model.setOutTradeNo(outTradeNo);

            request.setBizModel(model);

            //通过alipayClient调用API，获得对应的response类
            AlipayTradeQueryResponse response = alipayClient.execute(request);

            //System.out.print(response.getBody());

            //根据response中的结果继续业务逻辑处理
            resMap = JsonUtil.jsonToObject(response.getBody());

        }catch (Exception e){
            log.error("AliPayHandler tradeQueryOperate error " + ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
        }
        return resMap;
    }

    /**
     * 生成APP支付订单
     */
    public static boolean payRequest(Packages pk, String deviceId, User user, Map<String, Object> sendMap) {

        boolean ret = true;

        try{
            //通过工具类实例化客户端，读取参数文件pay.properties
            AlipayClient alipayClient = PayCommonUtil.getAliClient();

            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

            //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            //商品信息
            String description = pk.getDescription();
            model.setBody(description);
            //商品名称
            String subject = Convert.toString(pk.getPackage_name());
            model.setSubject(subject);
            //商户订单号(自动生成)
            String outTradeNo = PayCommonUtil.getOutTradeNo();
            model.setOutTradeNo(outTradeNo);
            //交易超时时间
            model.setTimeoutExpress("30m");
            //支付金额
            String totalAmount = Convert.toString(pk.getPrice() * pk.getCount());
            //model.setTotalAmount(totalAmount);
            model.setTotalAmount("0.01");
            //销售产品码
            model.setProductCode("QUICK_MSECURITY_PAY");
            request.setBizModel(model);
            //回调地址
            //String notifyUrl = "http://" + PayCommonUtil.getAddressIP() + "/sdk/smarthard/pay/AliNotify";
            String notifyUrl = "http://" + "api.quicktouch.adflash.cn:20178" + "/smarthard/alipay/trade/notify";
            request.setNotifyUrl(notifyUrl);
            //log.debug("notifyUrl : ========" + notifyUrl);
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            String orderString = response.getBody();

            sendMap.put("out_trade_no",outTradeNo);
            sendMap.put("payType",1);
            sendMap.put("orderString",orderString);

            log.debug("AliPayHandler getAliPayOrderString {} {} {} {} {} orderString: {}", DateUtil.now(),description, subject ,outTradeNo , totalAmount ,orderString);

            //System.out.println(orderString);

            IHash<String,TradeDetail> deviceOrderHash = JedisonDao.getData().getHash(SHData.Data_Device_Order + deviceId + DateUtil.today());

            IHash<String,Map<String,String>> orderHash = JedisonDao.getData().getHash(SHData.Data_Cur_OrderMap + DateUtil.today());

            TradeDetail tradeDetail = new TradeDetail();
            tradeDetail.setId(tradeDetail.getId());
            tradeDetail.setUserId(user.getUserId());
            tradeDetail.setDeviceId(deviceId);
            tradeDetail.setBody(description);
            tradeDetail.setSubject(subject);
            tradeDetail.setTotalAmount(totalAmount);
            tradeDetail.setOutTradeNo(outTradeNo);
            tradeDetail.setTradeType(1);
            tradeDetail.setTradeState(1);
            tradeDetail.setCreateTime(DateUtil.now());
            tradeDetail.setRechargeState(0);
            List<Map<String, Object>> packages = new ArrayList<>();
            Map<String,Object> p = new HashMap<>();
            p.put("package_id",pk.getPackage_id());
            p.put("count",pk.getCount());
            p.put("start_time",-1);
            Map<String,String> resMap = new HashMap<>();
            packages.add(p);
            tradeDetail.setPackages(packages);
            resMap.put("device_id",deviceId);

            deviceOrderHash.hset(outTradeNo,tradeDetail);
            orderHash.hset(outTradeNo, resMap);

            JedisonDao.getBuffer().getLists(SHBuffer.SHTradeInsertMap).rpush(tradeDetail);

            //if (deviceOrderHash.ttl() == -1){
            //    deviceOrderHash.expire(DateUtil.calcNext00Diff() + 600);
            //}
            //if (orderHash.ttl() == -1){
            //    orderHash.expire(DateUtil.calcNext00Diff() + 600);
            //}

            //添加该设备订单list

            IHash<String,List<String>> allDeviceOrderHash = JedisonDao.getData().getHash(SHData.Data_All_Device_Order);
            List<String> deviceOrderList = allDeviceOrderHash.hget(deviceId);
            if (deviceOrderList ==null){
                List<String> list = new ArrayList<>();
                list.add(outTradeNo);
                allDeviceOrderHash.hset(deviceId,list);
            }else {
                deviceOrderList.add(outTradeNo);
            }

        }catch (Exception e){
            e.printStackTrace();
            log.error("AliPayHandler getAliPayOrderString error : " + e);
            sendMap.put("msg","server error");
            return false;
        }
        return  ret;
    }

    public static boolean tradeNotify(Map<String, String> recMap, Map<String, Object> sendMap){
        boolean ret = true;
        try{
            //异步返回结果的验签
            //第一步： 在通知返回参数列表中，除去sign、sign_type两个参数外，凡是通知返回回来的参数皆是待验签的参数

            String receipt_amount = Convert.toString(recMap.get("receipt_amount"));
            String out_trade_no = Convert.toString(recMap.get("out_trade_no"));
            String app_id = Convert.toString(recMap.get("app_id"));
            String seller_id = Convert.toString(recMap.get("seller_id"));
            String trade_status = Convert.toString(recMap.get("trade_status"));

            //for (Iterator iter = recMap.keySet().iterator(); iter.hasNext();) {
            //    String name = (String) iter.next();
            //    String[] values = (String[]) recMap.get(name);
            //    String valueStr = "";
            //    for (int i = 0; i < values.length; i++) {
            //        valueStr = (i == values.length - 1) ? valueStr + values[i]
            //                : valueStr + values[i] + ",";
            //    }
            //    //乱码解决，这段代码在出现乱码时使用。
            //    //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            //    params.put(name, valueStr);
            //}
            //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
            //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
            boolean signVerified  = AlipaySignature.rsaCheckV1(recMap, alipaypublicKey, "utf-8","RSA2");

            if(signVerified){
                // TODO 验签成功后
                //按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
                //1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号
                IHash<String,Map<String,String>> orderHash = JedisonDao.getData().getHash(SHData.Data_Cur_OrderMap + DateUtil.today());
                Map<String,String> orderMap = orderHash.hget(out_trade_no);
                if (orderMap ==null || orderMap.size() <=0){
                    sendMap.put("msg","no out_trade_no");
                    return false;
                }
                String deviceId = orderMap.get("device_id");
                IHash<String,TradeDetail> deviceOrderHash = JedisonDao.getData().getHash(SHData.Data_Device_Order + deviceId + DateUtil.today());
                TradeDetail tradeDetail = deviceOrderHash.hget(out_trade_no);
                if (tradeDetail == null){
                    sendMap.put("msg","no out_trade_no");
                    return false;
                }
                //2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额）
                String checkTotalAmount = tradeDetail.getTotalAmount();
                if (!"0.01".equals(receipt_amount)){
                    sendMap.put("msg","no total_amount");
                    return false;
                }
                //3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
                if (!"2088711348888651".equals(seller_id)){
                    sendMap.put("msg","no seller_id");
                    return false;
                }
                //4、验证app_id是否为该商户本身
                if (!PayCommonUtil.checkAppID(app_id)){
                    sendMap.put("msg","no app_id");
                    return false;
                }
                tradeDetail.setTradeState(3);
                if ("TRADE_SUCCESS".equalsIgnoreCase(trade_status)){

                    //套餐购买
                    boolean r = CommandHandler.doRecharge(tradeDetail);

                }

                deviceOrderHash.hset(out_trade_no,tradeDetail);
            }else{
                // TODO 验签失败则记录异常日志，并在response中返回failure.
                sendMap.put("msg","no signVerified");
                return false;
            }
        }catch (Exception e){
            log.error("AliPayHandler tradeNotify error {}", ExceptionUtils.getFullStackTrace(e));
            sendMap.put("msg","server error");
            return false;
        }
        return ret;
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> recMap = new HashMap<>();
        //Map<String, Object> reqMap = new HashMap<>();
        //Map<String, Object> data = new HashMap<>();
        //reqMap.put("detail","test");
        //reqMap.put("body","test");
        //reqMap.put("total_fee",0.01);
        //data.put("device_id","866666030863006");
        //recMap.put("data",data);
        //recMap.put("reqMap",reqMap);
        //recMap.put("token","g7e8kGtnTgeZYvDlflLU3g");
        String json = "{\"alipay_trade_query_response\":{\"code\":\"10000\",\"msg\":\"Success\",\"buyer_logon_id\":\"181****5822\",\"buyer_pay_amount\":\"0.00\",\"buyer_user_id\":\"2088022288788373\",\"invoice_amount\":\"0.00\",\"out_trade_no\":\"app120180424095126730\",\"point_amount\":\"0.00\",\"receipt_amount\":\"0.00\",\"send_pay_date\":\"2018-04-24 09:51:34\",\"total_amount\":\"0.01\",\"trade_no\":\"2018042421001004370503980665\",\"trade_status\":\"TRADE_SUCCESS\"},\"sign\":\"ZH6WfzMH+AUz4RTvIFAuVYiM9aJOMB9iEkGoC8yIiQf4qQbDQ0U4ZcwWwGNJwt9ZXIOcVM4s5vJ72kxI8EZABPaCHTmM0bgCK2K3MqwqgkQx8aoAQ6LgPKGwczprubYSNacxyfAfUxevn758VGMu0Qu6woVbDK4F4rg7RuMBP3Dgt73XZ+izwgMbPZtEzGYDRg2PuQriXaTgOPbT4FIidRdzGM8NVzAhkZYsGX7rLUcjvk1QhECa1jRobKHULqSPqp28Ixybk4iWqJBVgJStAR1dvydnC0dK+lXYZaGGTz6eoJluGaIR1SXzcTY7qzOR+C59LFERKIX/ysnZPKJA1w==\"}";
        recMap = JsonUtil.jsonToObject(json);

        boolean signVerified  = AlipaySignature.rsaCheckV1(recMap, alipaypublicKey, "utf-8","RSA2");
        System.out.println(signVerified);

        //tradeNotifyTest(recMap,new HashMap<>());
        //tradeNotify(recMap,new HashMap<>());
        //
        tradeQueryOperate("app120180424095126730");

    }



}
