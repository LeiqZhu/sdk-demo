package com.biz.smarthard.entity.pay;

import java.io.Serializable;

/**
 * 微信统一下单接口请求实体对象
 */
public class UnifiedOrder implements Serializable{
    /**
     * 公共号ID(微信分配的公众账号 ID)
     */
    public String appid = "";
    /**
     * 商户号(微信支付分配的商户号)
     */
    public String mch_id = "";
    /**
     * 微信支付分配的终端设备号
     */
    public String device_info = "";
    /**
     * 随机字符串，不长于 32 位
     */
    public String nonce_str = "";
    /**
     * 签名
     */
    public String sign = "";
    /**
     * 商品描述
     */
    public String body = "";
    /**
     * 附加数据，原样返回
     */
    public String attach = "";
    /**
     * 商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一,详细说明
     */
    public String out_trade_no = "";
    /**
     * 订单总金额，单位为分，不能带小数点
     */
    public int total_fee = 0;
    /**
     * 终端IP
     */
    public String spbill_create_ip = "";
    /**
     * 订 单 生 成 时 间 ，
     * 格 式 为yyyyMMddHHmmss，
     * 如 2009 年12 月 25 日 9 点 10 分 10 秒表示为 20091225091010。
     * 时区为 GMT+8 beijing。该时间取自商户服务器
     */
    public String time_start = "";
    /**
     * 交易结束时间
     */
    public String time_expire = "";
    /**
     * 商品标记 商品标记，该字段不能随便填，不使用请填空，使用说明详见第 5 节
     */
    public String goods_tag = "";
    /**
     * 接收微信支付成功通知
     */
    public String notify_url = "";
    /**
     * JSAPI、NATIVE、APP
     */
    public String trade_type = "";
    /**
     * 用户标识 trade_type 为 JSAPI时，此参数必传
     */
    public String openid = "";
    /**
     * 只在 trade_type 为 NATIVE时需要填写。
     */
    public String product_id = "";
}
