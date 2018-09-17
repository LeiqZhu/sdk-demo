package com.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import org.junit.Test;
import snowfox.lang.util.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AliPayTest {
    private static final String APP_ID = "";
    private static final String APP_PRIVATE_KEY = "";
    private static final String CHARSET = "UTF-8";
    private static final String ALIPAY_PUBLIC_KEY = "";

    public void test1(){
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2");
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("我是测试数据");
        model.setSubject("App支付测试Java");
        model.setOutTradeNo(R.UU16());
        model.setTimeoutExpress("30m");
        model.setTotalAmount("0.01");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl("商户外网可以访问的异步地址");
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    //public void  test2(){
    //    //获取支付宝POST过来反馈信息
    //    Map<String,String> params = new HashMap<String,String>();
    //    Map requestParams = request.getParameterMap();
    //    for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
    //        String name = (String) iter.next();
    //        String[] values = (String[]) requestParams.get(name);
    //        String valueStr = "";
    //        for (int i = 0; i < values.length; i++) {
    //            valueStr = (i == values.length - 1) ? valueStr + values[i]
    //                    : valueStr + values[i] + ",";
    //        }
    //        //乱码解决，这段代码在出现乱码时使用。
    //        //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
    //        params.put(name, valueStr);
    //    }
    //    //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
    //    //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
    //    boolean flag = AlipaySignature.rsaCheckV1(params, alipaypublicKey, charset,"RSA2");
    //}

    @Test
    public void testDecode(){
        String enc = "%E7%BB%AD%E8%B4%B9%E5%85%85%E5%80%BC%EF%BC%8C%E7%AB%8B%E5%8D%B3%E7%94%9F%E6%95%88";
        try {
            System.out.println(new String(enc.getBytes("ISO-8859-1"),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String enc = "续费充值，立即生效";
        try {
            String dec = URLDecoder.decode(enc,"utf-8");
            System.out.println(dec);
            System.out.println(new String(enc.getBytes("gbk"),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
