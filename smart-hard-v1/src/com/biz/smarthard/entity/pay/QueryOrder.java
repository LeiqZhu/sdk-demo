package com.biz.smarthard.entity.pay;

import java.io.Serializable;

/**
 * 微信订单查询接口请求实体对象
 */
public class QueryOrder implements Serializable {
    /**
     * 公共号ID(微信分配的公众账号 ID)
     */
    public String appid = "";

    /**
     * 商户号(微信支付分配的商户号)
     */
    public String mch_id = "";

    /**
     * 微信订单号，优先使用
     */
    public String transaction_id = "";

    /**
     * 商户系统内部订单号
     */
    public String out_trade_no = "";

    /**
     * 随机字符串，不长于 32 位
     */
    public String nonce_str = "";

    /**
     * 签名，参与签名参数：appid，mch_id，transaction_id，out_trade_no，nonce_str，key
     */
    public String sign = "";
}
