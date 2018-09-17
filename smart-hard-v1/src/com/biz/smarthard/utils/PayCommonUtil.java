package com.biz.smarthard.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import ch.qos.logback.core.joran.spi.XMLUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sdk.core.json.JsonUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.ssl.SSLContexts;
import snowfox.lang.codec.Base64;
import snowfox.lang.codec.MD5;
import snowfox.lang.time.DateFormats;
import snowfox.lang.util.R;

public class PayCommonUtil {
    public static final String TIME = "yyyyMMddHHmmss";

    public static Properties properties = PropertyUtil.getInstance();


    /**
     * 创建支付宝交易对象
     */
    public static AlipayClient getAliClient() {
        AlipayClient alipayClient = new DefaultAlipayClient(properties.getProperty("AliPay.payURL"),
                properties.getProperty("AliPay.APP_ID"),
                properties.getProperty("AliPay.APP_PRIVATE_KEY"), "json", "utf-8",
                properties.getProperty("AliPay.ALIPAY_PUBLIC_KEY"), "RSA2");
        return alipayClient;
    }

    /**
     * 创建微信交易对象
     */
    public static SortedMap<Object, Object> getWXPrePayID()
    {
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("appid", PropertyUtil.getInstance().getProperty("WxPay.appid"));
        parameters.put("mch_id", PropertyUtil.getInstance().getProperty("WxPay.mchid"));
        parameters.put("nonce_str", PayCommonUtil.CreateNoncestr());
        parameters.put("fee_type", "CNY");
        parameters.put("notify_url", PropertyUtil.getInstance().getProperty("WxPay.notifyurl"));
        parameters.put("trade_type", "APP");
        return parameters;
    }

    /**
     * 再次签名，支付
     */
    //public static SortedMap<Object, Object> startWXPay(String result)
    //{
    //    try
    //    {
    //        Map<String, String> map = XMLUtil.doXMLParse(result);
    //        SortedMap<Object, Object> parameterMap = new TreeMap<Object, Object>();
    //        parameterMap.put("appid", PropertyUtil.getInstance().getProperty("WxPay.appid"));
    //        parameterMap.put("partnerid", PropertyUtil.getInstance().getProperty("WxPay.mchid"));
    //        parameterMap.put("prepayid", map.get("prepay_id"));
    //        parameterMap.put("package", "Sign=WXPay");
    //        parameterMap.put("noncestr", PayCommonUtil.CreateNoncestr());
    //        // 本来生成的时间戳是13位，但是ios必须是10位，所以截取了一下
    //        parameterMap.put("timestamp",
    //                Long.parseLong(String.valueOf(System.currentTimeMillis()).toString().substring(0, 10)));
    //        String sign = PayCommonUtil.createSign( parameterMap);
    //        parameterMap.put("sign", sign);
    //        return parameterMap;
    //    } catch (Exception e)
    //    {
    //        e.printStackTrace();
    //    }
    //    return null;
    //}

    /**
     * 创建随机数
     *
     * @return
     */
    public static String CreateNoncestr()
    {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < 16; i++)
        {
            Random rd = new Random();
            res += chars.charAt(rd.nextInt(chars.length() - 1));
        }
        return res;
    }

    /**
     * 获取商户订单
     * 当前时间：app1 + 年月日小时分秒 + yyyyMMddHHmmss3位随机数
     */
    public static String getOutTradeNo(){
        return "app1" + DateFormats.format(new Date(),"yyyyMMddHHmmss") + R.random(100,1000);
    }

    /**
     * 是否签名正确,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     *
     * @return boolean
     */
    public static boolean isTenpaySign(Map<String, Object> packageParams)
    {
        SortedMap<Object, Object> map = new TreeMap<>();
        for (Map.Entry<String,Object> entry : packageParams.entrySet()) {
            map.put(entry.getKey(),entry.getValue());
        }
        StringBuffer sb = new StringBuffer();
        Set es = map.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (!"sign".equals(k) && null != v && !"".equals(v))
            {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + PropertyUtil.getInstance().getProperty("WXPay.key"));
        System.out.println(sb);
        // 算出摘要
        String mysign = MD5.hex(sb.toString()).toUpperCase();
        String tenpaySign = ((String) packageParams.get("sign"));
        // System.out.println(tenpaySign + " " + mysign);
        return tenpaySign.equals(mysign);
    }

    public static Map<String,String> str2Map(String str) throws UnsupportedEncodingException {
        Map<String,String> map = new HashMap<>();
        String[] strs = str.split("&");
        for (String s : strs) {
            String[] kvs = s.split("=");
            String key = URLDecoder.decode(kvs[0],"utf-8");
            String value = URLDecoder.decode(kvs[1],"utf-8");
            map.put(key,value);
        }
        return map;
    }

    /**
     * @Description：创建sign签名
     *            编码格式
     * @param parameters
     *            请求参数
     * @return
     */
    public static String createSign(SortedMap<Object, Object> parameters)
    {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k))
            {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + PropertyUtil.getInstance().getProperty("WXPay.key"));
        String sign = MD5.hex(sb.toString()).toUpperCase();
        return sign;
    }

    /**
     * @Description：将请求参数转换为xml格式的string
     * @param parameters
     *            请求参数
     * @return
     */
    public static String getRequestXml(SortedMap<Object, Object> parameters)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k))
            {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else
            {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * @Description：返回给微信的参数
     * @param return_code
     *            返回编码
     * @param return_msg
     *            返回信息
     * @return
     */
    public static String setXML(String return_code, String return_msg)
    {
        return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg
                + "]]></return_msg></xml>";
    }

    /**
     * 发送https请求
     *
     * @param requestUrl
     *            请求地址
     * @param requestMethod
     *            请求方式（GET、POST）
     * @param outputStr
     *            提交的数据
     * @return 返回微信服务器响应的信息
     */
    public static String httpsRequest(String requestUrl, String requestMethod, String outputStr)
    {
        try
        {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            //TrustManager[] tm =
            //        { new TrustManagerUtil() };
            //SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            //sslContext.init(null, tm, new java.security.SecureRandom());
            SSLContext sslContext = SSLContexts.createDefault();
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            // conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr)
            {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null)
            {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            return buffer.toString();
        } catch (ConnectException ce)
        {
            // log.error("连接超时：{}", ce);
        } catch (Exception e)
        {
            // log.error("https请求异常：{}", e);
        }
        return null;
    }

    /**
     * 发送https请求
     *
     * @param requestUrl
     *            请求地址
     * @param requestMethod
     *            请求方式（GET、POST）
     *            提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpsRequest(String requestUrl, String requestMethod)
    {
        JSONObject jsonObject = null;
        try
        {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            //TrustManager[] tm =
            //        { new TrustManagerUtil() };
            //SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            //sslContext.init(null, tm, new java.security.SecureRandom());
            SSLContext sslContext = SSLContexts.createDefault();
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            // conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(3000);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            // conn.setRequestProperty("content-type",
            // "application/x-www-form-urlencoded");
            // 当outputStr不为null时向输出流写数据
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null)
            {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (ConnectException ce)
        {
            // log.error("连接超时：{}", ce);
        } catch (Exception e)
        {
            System.out.println(e);
            // log.error("https请求异常：{}", e);
        }
        return jsonObject;
    }

    public static String urlEncodeUTF8(String source)
    {
        String result = source;
        try
        {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 接收微信的异步通知
     *
     * @throws IOException
     */
    //public static String reciverWx(HttpServletRequest request) throws IOException
    //{
    //    InputStream inputStream;
    //    StringBuffer sb = new StringBuffer();
    //    inputStream = request.getInputStream();
    //    String s;
    //    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    //    while ((s = in.readLine()) != null)
    //    {
    //        sb.append(s);
    //    }
    //    in.close();
    //    inputStream.close();
    //    return sb.toString();
    //}

    public static boolean checkAppID(String appid){
        return PropertyUtil.getInstance().getProperty("AliPay.APP_ID").equals(appid) ? true : false;
    }

    /**
     * 将日志保存至指定路径
     *
     * @param path
     * @param str
     */
    public static void saveLog(String path, String str)
    {
        File file = new File(path);
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(path);
            fos.write(str.getBytes());
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String getAddressIP(){
        String ip = "";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

    //public static void saveE(String path, Exception exception)
    //{
    //    try {
    //        int i = 1 / 0;
    //    } catch (final Exception e) {
    //        try {
    //            new PrintWriter(new BufferedWriter(new FileWriter(
    //                    path, true)), true).println(new Object() {
    //                public String toString() {
    //                    StringWriter stringWriter = new StringWriter();
    //                    PrintWriter writer = new PrintWriter(stringWriter);
    //                    e.printStackTrace(writer);
    //                    StringBuffer buffer = stringWriter.getBuffer();
    //                    return buffer.toString();
    //                }
    //            });
    //        } catch (IOException e1) {
    //            e1.printStackTrace();
    //        }
    //    }
    //
    //}

    public static void main(String[] args) throws UnsupportedEncodingException, JsonProcessingException {
        String enc = "alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2018041102536554&biz_content=%7B%22body%22%3A%22699%E5%85%83%E4%B8%8D%E9%99%90%E9%87%8F%E5%B9%B4%E5%A5%97%E9%A4%90%22%2C%22out_trade_no%22%3A%22app120180420174044930%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22699%E5%B9%B4%E5%A5%97%E9%A4%90%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%22699%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F10.2.25.122%2Fsdk%2Fsmarthard%2Fpay%2FAliNotify&sign=QaQ8i731K5aWue6jWcPzm0996zQ6Su%2FlUjRnzfe8qdUJLBmNe0DL4SADbtHF7uu7MmgkjkSZAwPPg9DjHR3F%2F4USgrY5icIFfX7S1gsMpbrAEMubhJ5LNZ6OxTD%2F3%2BdiPf13KKabS4SDt7kGzlHaBZoZdigJNcRSunDDaQyi4GyiZW%2FjaHZWFdmLXnpieDrY%2Fi5FFzH9neY9gjf6qQFVWQr3K8c%2Blwvief7E6HI65aAF6A3naCPGkFpmFr2Pg09PE7kRLpRqVGZXWRLJvPC03LYyXsuc05yAGGL8tRW9qZT6vc%2BBNVYRq%2BUBZPwNl2jk4Lw9a0FWb9L5VFSIHjKFdA%3D%3D&sign_type=RSA2&timestamp=2018-04-20+17%3A40%3A44&version=1.0";
        System.out.println(URLDecoder.decode(enc,"utf-8"));
        String s = "fund_bill_list=%5B%7B%22amount%22%3A%220.01%22%2C%22fundChannel%22%3A%22ALIPAYACCOUNT%22%7D%5D";
        System.out.println(str2Map(s));
    }
}
