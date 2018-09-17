package com.biz.smarthard.entity.pay;

import java.util.Date;

public class AliPayNotice {
    /**
     * ID
     */
    private Integer id;

    /**
     * 通知时间
     */
    private Date notifyTime;

    /**
     * 通知类型
     */
    private String notifyType;

    /**
     * 通知校验ID
     */
    private String notifyId;

    /**
     * 支付宝分配给开发者的应用Id
     */
    private String appId;

    /**
     * 编码格式
     */
    private String charset;

    /**
     * 接口版本
     */
    private String version;

    /**
     * 签名类型
     */
    private String signType;

    /**
     * 签名
     */
    private String sign;

    /**
     * 支付宝交易号
     */
    private String tradeNo;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 商户业务号
     */
    private String outBizNo;

    /**
     * 买家支付宝用户号
     */
    private String buyerId;

    /**
     * 买家支付宝账号
     */
    private String buyerLogonId;

    /**
     * 卖家支付宝用户号
     */
    private String sellerId;

    /**
     * 卖家支付宝账号
     */
    private String sellerEmail;

    /**
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 订单金额
     */
    private Double totalAmount;

    /**
     * 实收金额
     */
    private Double receiptAmount;

    /**
     * 开票金额
     */
    private Double invoiceAmount;

    /**
     * 付款金额
     */
    private Double buyerPayAmount;

    /**
     * 集分宝金额
     */
    private Double pointAmount;

    /**
     * 总退款金额
     */
    private Double refundFee;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 商品描述
     */
    private String body;

    /**
     * 交易创建时间
     */
    private Date gmtCreate;

    /**
     * 交易付款时间
     */
    private Date gmtPayment;

    /**
     * 交易退款时间
     */
    private Date gmtRefund;

    /**
     * 交易结束时间
     */
    private Date gmtClose;

    /**
     * 支付金额信息
     */
    private String fundBillList;

    /**
     * 回传参数
     */
    private String passbackParams;

    /**
     * 优惠券信息
     */
    private String voucherDetailList;

    /**
     * 数据插入时间
     */
    private Date createTime;

    /**
     * ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 通知时间
     */
    public Date getNotifyTime() {
        return notifyTime;
    }

    /**
     * 通知时间
     */
    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    /**
     * 通知类型
     */
    public String getNotifyType() {
        return notifyType;
    }

    /**
     * 通知类型
     */
    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType == null ? null : notifyType.trim();
    }

    /**
     * 通知校验ID
     */
    public String getNotifyId() {
        return notifyId;
    }

    /**
     * 通知校验ID
     */
    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId == null ? null : notifyId.trim();
    }

    /**
     * 支付宝分配给开发者的应用Id
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 支付宝分配给开发者的应用Id
     */
    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    /**
     * 编码格式
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 编码格式
     */
    public void setCharset(String charset) {
        this.charset = charset == null ? null : charset.trim();
    }

    /**
     * 接口版本
     */
    public String getVersion() {
        return version;
    }

    /**
     * 接口版本
     */
    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    /**
     * 签名类型
     */
    public String getSignType() {
        return signType;
    }

    /**
     * 签名类型
     */
    public void setSignType(String signType) {
        this.signType = signType == null ? null : signType.trim();
    }

    /**
     * 签名
     */
    public String getSign() {
        return sign;
    }

    /**
     * 签名
     */
    public void setSign(String sign) {
        this.sign = sign == null ? null : sign.trim();
    }

    /**
     * 支付宝交易号
     */
    public String getTradeNo() {
        return tradeNo;
    }

    /**
     * 支付宝交易号
     */
    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    /**
     * 商户订单号
     */
    public String getOutTradeNo() {
        return outTradeNo;
    }

    /**
     * 商户订单号
     */
    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
    }

    /**
     * 商户业务号
     */
    public String getOutBizNo() {
        return outBizNo;
    }

    /**
     * 商户业务号
     */
    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo == null ? null : outBizNo.trim();
    }

    /**
     * 买家支付宝用户号
     */
    public String getBuyerId() {
        return buyerId;
    }

    /**
     * 买家支付宝用户号
     */
    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId == null ? null : buyerId.trim();
    }

    /**
     * 买家支付宝账号
     */
    public String getBuyerLogonId() {
        return buyerLogonId;
    }

    /**
     * 买家支付宝账号
     */
    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId == null ? null : buyerLogonId.trim();
    }

    /**
     * 卖家支付宝用户号
     */
    public String getSellerId() {
        return sellerId;
    }

    /**
     * 卖家支付宝用户号
     */
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId == null ? null : sellerId.trim();
    }

    /**
     * 卖家支付宝账号
     */
    public String getSellerEmail() {
        return sellerEmail;
    }

    /**
     * 卖家支付宝账号
     */
    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail == null ? null : sellerEmail.trim();
    }

    /**
     * 交易状态
     */
    public String getTradeStatus() {
        return tradeStatus;
    }

    /**
     * 交易状态
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus == null ? null : tradeStatus.trim();
    }

    /**
     * 订单金额
     */
    public Double getTotalAmount() {
        return totalAmount;
    }

    /**
     * 订单金额
     */
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 实收金额
     */
    public Double getReceiptAmount() {
        return receiptAmount;
    }

    /**
     * 实收金额
     */
    public void setReceiptAmount(Double receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    /**
     * 开票金额
     */
    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * 开票金额
     */
    public void setInvoiceAmount(Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * 付款金额
     */
    public Double getBuyerPayAmount() {
        return buyerPayAmount;
    }

    /**
     * 付款金额
     */
    public void setBuyerPayAmount(Double buyerPayAmount) {
        this.buyerPayAmount = buyerPayAmount;
    }

    /**
     * 集分宝金额
     */
    public Double getPointAmount() {
        return pointAmount;
    }

    /**
     * 集分宝金额
     */
    public void setPointAmount(Double pointAmount) {
        this.pointAmount = pointAmount;
    }

    /**
     * 总退款金额
     */
    public Double getRefundFee() {
        return refundFee;
    }

    /**
     * 总退款金额
     */
    public void setRefundFee(Double refundFee) {
        this.refundFee = refundFee;
    }

    /**
     * 订单标题
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 订单标题
     */
    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    /**
     * 商品描述
     */
    public String getBody() {
        return body;
    }

    /**
     * 商品描述
     */
    public void setBody(String body) {
        this.body = body == null ? null : body.trim();
    }

    /**
     * 交易创建时间
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 交易创建时间
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * 交易付款时间
     */
    public Date getGmtPayment() {
        return gmtPayment;
    }

    /**
     * 交易付款时间
     */
    public void setGmtPayment(Date gmtPayment) {
        this.gmtPayment = gmtPayment;
    }

    /**
     * 交易退款时间
     */
    public Date getGmtRefund() {
        return gmtRefund;
    }

    /**
     * 交易退款时间
     */
    public void setGmtRefund(Date gmtRefund) {
        this.gmtRefund = gmtRefund;
    }

    /**
     * 交易结束时间
     */
    public Date getGmtClose() {
        return gmtClose;
    }

    /**
     * 交易结束时间
     */
    public void setGmtClose(Date gmtClose) {
        this.gmtClose = gmtClose;
    }

    /**
     * 支付金额信息
     */
    public String getFundBillList() {
        return fundBillList;
    }

    /**
     * 支付金额信息
     */
    public void setFundBillList(String fundBillList) {
        this.fundBillList = fundBillList == null ? null : fundBillList.trim();
    }

    /**
     * 回传参数
     */
    public String getPassbackParams() {
        return passbackParams;
    }

    /**
     * 回传参数
     */
    public void setPassbackParams(String passbackParams) {
        this.passbackParams = passbackParams == null ? null : passbackParams.trim();
    }

    /**
     * 优惠券信息
     */
    public String getVoucherDetailList() {
        return voucherDetailList;
    }

    /**
     * 优惠券信息
     */
    public void setVoucherDetailList(String voucherDetailList) {
        this.voucherDetailList = voucherDetailList == null ? null : voucherDetailList.trim();
    }

    /**
     * 数据插入时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 数据插入时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
