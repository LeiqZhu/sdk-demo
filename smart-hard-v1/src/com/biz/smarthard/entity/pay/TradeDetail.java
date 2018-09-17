package com.biz.smarthard.entity.pay;

import com.biz.smarthard.bean.redis.SHBuffer;
import com.biz.smarthard.bean.redis.SHId;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.AddToRedis;
import com.biz.smarthard.utils.JacksonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sdk.core.buffer.db.DBOperation;
import com.sdk.core.json.JsonUtil;
import snowfox.lang.util.Convert;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TradeDetail extends AddToRedis{

    private static final long serialVersionUID = 1L;

    //用户id
    private Long userId;
    //设备id
    private String deviceId;
    //订单详情
    private String body;
    //订单主题
    private String subject;
    //订单金额
    private String totalAmount;
    //支付平台订单号
    private String tradeNo;
    //商户订单号
    private String outTradeNo;
    //订单类型 目前有 1ali 2wx
    private Integer tradeType;
    /**
     * 订单状态
     * WAIT_BUYER_PAY	1交易创建，等待买家付款
     * TRADE_CLOSED	    2未付款交易超时关闭，或支付完成后全额退款
     * TRADE_SUCCESS	3交易支付成功
     * TRADE_FINISHED	4交易结束，不可退款
     */
    private Integer tradeState;
    //充值状态 1 已充值 0 待充值 -1 充值失败
    private int rechargeState;
    //订单创建时间
    private String createTime;
    //支付完成时间
    private String payTime;
    //购买套餐时的包装内容
    private List<Map<String,Object>> packages;

    public List<Map<String, Object>> getPackages() {
        return packages;
    }

    public void setPackages(List<Map<String, Object>> packages) {
        this.packages = packages;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public int getRechargeState() {
        return rechargeState;
    }

    public void setRechargeState(int rechargeState) {
        this.rechargeState = rechargeState;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String finishTime) {
        this.payTime = finishTime;
    }

    public TradeDetail() {
    }

    @Override
    public Long getId() {
        return JedisonDao.getConfig().getAtomicLong(this.getRedisId()).incrAndGet();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public Integer getTradeState() {
        return tradeState;
    }

    public void setTradeState(Integer tradeState) {
        this.tradeState = tradeState;
    }

    @Override
    public void setRedisId() {
        this.redisId = SHId.incrTradeId;
    }

    @Override
    public void setRedisKey() {
        this.redisKey = SHBuffer.SHTradeInsertMap;
    }

    @Override
    public void setInsertSql() {
        this.insertSql = "INSERT IGNORE INTO smart_hard.sh_pay_trade (" +
                "id," +
                "user_id," +
                "device_id," +
                "body," +
                "subject," +
                "total_amount," +
                "trade_no," +
                "out_trade_no," +
                "trade_type," +
                "recharge_state," +
                "trade_state," +
                "create_time," +
                "pay_time," +
                "packages" +
                ")" +
                "VALUES" +
                " ( ?,?,?,?,?, ?,?,?,?,?, ?,?,?,? )";
    }

    @Override
    public Object[] evalParam(DBOperation opr, byte[] keyValue){
        TradeDetail tradeDetail = (TradeDetail) decodeToObject(opr, keyValue);

        Object[] params = new Object[0];
        try {
            params = new Object[]{tradeDetail.getId(),
                    tradeDetail.getUserId(),
                    tradeDetail.getDeviceId(),
                    tradeDetail.getBody(),
                    tradeDetail.getSubject(),
                    tradeDetail.getTotalAmount(),
                    tradeDetail.getTradeNo(),
                    tradeDetail.getOutTradeNo(),
                    tradeDetail.getTradeType(),
                    tradeDetail.getRechargeState(),
                    tradeDetail.getTradeState(),
                    tradeDetail.getCreateTime(),
                    tradeDetail.getPayTime(),
                    JsonUtil.objectToJson(tradeDetail.getPackages())
            };
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return params;
    }

    public static TradeDetail map2Bean(Map<String,Object> map){
        TradeDetail tradeDetail = new TradeDetail();
        tradeDetail.setId(Convert.toLong(map.get("id")));
        tradeDetail.setUserId(Convert.toLong(map.get("user_id")));
        tradeDetail.setDeviceId(Convert.toString(map.get("device_id")));
        tradeDetail.setBody(Convert.toString(map.get("body")));
        tradeDetail.setSubject(Convert.toString(map.get("subject")));
        tradeDetail.setTotalAmount(Convert.toString(map.get("total_amount")));
        tradeDetail.setTradeNo(Convert.toString(map.get("trade_no")));
        tradeDetail.setOutTradeNo(Convert.toString(map.get("out_trade_no")));
        tradeDetail.setTradeType(Convert.toInt(map.get("trade_type")));
        tradeDetail.setRechargeState(Convert.toInt(map.get("recharge_state")));
        tradeDetail.setTradeState(Convert.toInt(map.get("trade_state")));
        tradeDetail.setCreateTime(Convert.toString(map.get("create_time")));
        tradeDetail.setPayTime(Convert.toString(map.get("pay_time")));
        try {
            tradeDetail.setPackages(JacksonUtil.jackson.json2Obj(Convert.toString(map.get("packages"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tradeDetail;
    }
}
