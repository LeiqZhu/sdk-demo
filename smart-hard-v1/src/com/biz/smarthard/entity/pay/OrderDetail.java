package com.biz.smarthard.entity.pay;

import java.io.Serializable;

/**
 * 微信订单明细实体对象
 */
public class OrderDetail implements Serializable{
    /**
     * 返回状态码，SUCCESS/FAIL 此字段是通信标识，非交易标识，交易是否成功需要查看trade_state来判断
     */
    public String return_code = "";
    /**
     * 返回信息返回信息，如非空，为错误原因 签名失败 参数格式校验错误
     */
    public String return_msg = "";

    /**
     * 公共号ID(微信分配的公众账号 ID)
     */
    public String appid = "";

    /**
     * 商户号(微信支付分配的商户号)
     */
    public String mch_id = "";

    /**
     * 随机字符串，不长于32位
     */
    public String nonce_str = "";

    /**
     * 签名
     */
    public String sign = "";

    /**
     * 业务结果,SUCCESS/FAIL
     */
    public String result_code = "";

    /**
     * 错误代码
     */
    public String err_code = "";

    /**
     * 错误代码描述
     */
    public String err_code_des = "";

    /**
     交易状态
     SUCCESS—支付成功
     REFUND—转入退款
     NOTPAY—未支付
     CLOSED—已关闭
     REVOKED—已撤销
     USERPAYING--用户支付中
     NOPAY--未支付(输入密码或确认支付超时) PAYERROR--支付失败(其他原因，如银行返回失败)
     */
    public String trade_state = "";

    /**
     * 微信支付分配的终端设备号
     */
    public String device_info = "";

    /**
     * 用户在商户appid下的唯一标识
     */
    public String openid = "";

    /**
     * 用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效
     */
    public String is_subscribe = "";

    /**
     * 交易类型,JSAPI、NATIVE、MICROPAY、APP
     */
    public String trade_type = "";

    /**
     * 银行类型，采用字符串类型的银行标识
     */
    public String bank_type = "";

    /**
     * 订单总金额，单位为分
     */
    public String total_fee = "";

    /**
     * 现金券支付金额<=订单总金额，订单总金额-现金券金额为现金支付金额
     */
    public String coupon_fee = "";

    /**
     * 货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY
     */
    public String fee_type = "";

    /**
     * 微信支付订单号
     */
    public String transaction_id = "";

    /**
     * 商户系统的订单号，与请求一致。
     */
    public String out_trade_no = "";

    /**
     * 商家数据包，原样返回
     */
    public String attach = "";

    /**
     * 支付完成时间，格式为yyyyMMddhhmmss，如2009年12月27日9点10分10秒表示为20091227091010。
     时区为GMT+8 beijing。该时间取自微信支付服务器
     */
    public String time_end = "";

    /**
     * 订单对应的device_id
     */
    public String device_id;

    /**
     * 订单是否已处理
     * @return
     */
    public boolean is_do;

    public boolean isIs_do() {
        return is_do;
    }

    public void setIs_do(boolean is_do) {
        this.is_do = is_do;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_des() {
        return err_code_des;
    }

    public void setErr_code_des(String err_code_des) {
        this.err_code_des = err_code_des;
    }

    public String getTrade_state() {
        return trade_state;
    }

    public void setTrade_state(String trade_state) {
        this.trade_state = trade_state;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getIs_subscribe() {
        return is_subscribe;
    }

    public void setIs_subscribe(String is_subscribe) {
        this.is_subscribe = is_subscribe;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getBank_type() {
        return bank_type;
    }

    public void setBank_type(String bank_type) {
        this.bank_type = bank_type;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getCoupon_fee() {
        return coupon_fee;
    }

    public void setCoupon_fee(String coupon_fee) {
        this.coupon_fee = coupon_fee;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
