package com.biz.smarthard.handler.pay;

import com.biz.smarthard.bean.redis.SHData;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.pay.Packages;
import com.biz.smarthard.entity.pay.TradeDetail;
import com.biz.smarthard.entity.user.User;
import com.biz.smarthard.utils.Comparators;
import com.biz.smarthard.utils.JacksonUtil;
import com.biz.smarthard.utils.SmartHardUtil;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.db.MySqlDao;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.time.DateUtil;
import snowfox.lang.util.Convert;
import snowfox.lang.util.Objects;
import snowfox.lang.util.Strings;

import java.util.*;

public class PayHandler {

    private static final Logger log = LoggerFactory.getLogger(PayHandler.class);

    private static final int AliPay = 1;
    private static final int WXPay = 2;

    public static boolean tradeQuery(Map<String, Object> recMap, Map<String, Object> sendMap){
        boolean flag = true;
        try {
            List<TradeDetail> orderList = new ArrayList<>();
            Map<String,Object> dataMap = (Map<String, Object>) recMap.get("data");
            String deviceId = SmartHardUtil.checkDeviceId(dataMap);
            if (Strings.isEmpty(deviceId)){
                sendMap.put("orderList",orderList);
                sendMap.put("msg","device_id param error");
                flag = false;
            }
            //判断out_trade_no
            String out_trade_no = Convert.toString(dataMap.get("out_trade_no"));
            if (Strings.isEmpty(out_trade_no)){
                //若没有要查询的订单号，带需要查询数量num,则查询N条记录

                IHash<String,List<String>> allDeviceOrderHash = JedisonDao.getData().getHash(SHData.Data_All_Device_Order);
                //对订单号排序找出最前面的n个
                List<String> deviceOrderList = allDeviceOrderHash.hget(deviceId);
                if (deviceOrderList==null || deviceOrderList.size()==0){
                    sendMap.put("msg","this device no trade");
                    sendMap.put("orderList",orderList);
                    return false;
                }
                Collections.sort(deviceOrderList,new Comparators.OutTradeNoComparator());

                int n = Convert.toInt(dataMap.get("num"),0);
                if (n<=0){
                    n = deviceOrderList.size();
                }
                int i = 1;
                for (Iterator<String> iter = deviceOrderList.iterator(); iter.hasNext() && i <=n;){
                    TradeDetail tradeDetail = findTradeDetail(iter.next(),deviceId);
                    orderList.add(tradeDetail);
                }

            }else {
                TradeDetail tradeDetail = findTradeDetail(out_trade_no,deviceId);
                orderList.add(tradeDetail);
            }
            sendMap.put("orderList",orderList);
        }catch (Exception e){
            log.error("PayHandler tradeQuery error " + ExceptionUtils.getFullStackTrace(e));
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    static final String Query_Trade_By_NO = "SELECT * FROM sh_pay_trade WHERE out_trade_no=?";
    public static TradeDetail findTradeDetail(String out_trade_no, String deviceId) throws Exception {
        //有订单号,先查询当天该deviceid下的订单
        IHash<String,TradeDetail> curDeviceOrderHash = JedisonDao.getData().getHash(SHData.Data_Device_Order + deviceId + DateUtil.today());
        TradeDetail tradeDetail = curDeviceOrderHash.hget(out_trade_no);
        if (tradeDetail == null){
            //如果redis中没有，就从数据库查
            Object[] param = {out_trade_no};
            Map<String,Object> queryMap = MySqlDao.getDao().queryOne(Query_Trade_By_NO,param);
            if(queryMap!=null){
                tradeDetail = TradeDetail.map2Bean(queryMap);
            }
        }
        return tradeDetail;
    }

    public static boolean payRequest(Map<String, Object> recMap, Map<String, Object> sendMap){

        try {
            boolean ret = true;
            if(!SmartHardUtil.checkUsertoken(recMap,sendMap,"user_token")){
                return false;
            }

            Map<String,Object> data = (Map<String, Object>) recMap.get("data");

            String client_ip = Convert.toString(recMap.get("client_ip"));

            if (data==null || data.size()<0){
                sendMap.put("msg","order error");
                return false;
            }
            List<Map<String,Object>> packages = Convert.toList(data.get("packages"));
            if (packages.isEmpty()){
                sendMap.put("msg","order error");
                return false;
            }
            int payType = Convert.toInt(data.get("payType"));
            User user = (User) recMap.get("userMap");
            String deviceId = SmartHardUtil.checkDeviceId(data);

            for (Map<String, Object> pk :  packages) {
                Packages pkBean = SmartHardUtil.mapToBean(pk,Packages.class);
                switch (payType){
                    case WXPay:
                        ret = WXPayHandler.payRequest(pkBean, deviceId, user, sendMap, client_ip);
                        break;
                    case  AliPay:
                        ret = AliPayHandler.payRequest(pkBean, deviceId, user, sendMap);
                        break;
                    default:
                        break;
                }
            }
            return ret;
        }catch (Exception e){
            e.printStackTrace();
            sendMap.put("msg","server error");
            return false;
        }
    }
}
