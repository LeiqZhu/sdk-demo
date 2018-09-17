package com.biz.smarthard.utils;

import com.biz.smarthard.bean.SmartHardStatus;
import com.biz.smarthard.bean.redis.SHData;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.user.User;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.http.HttpClientUtil;
import com.sdk.core.json.JsonUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.util.Convert;
import snowfox.lang.util.Objects;
import snowfox.lang.util.Strings;

import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartHardUtil {

    private static final Logger log = LoggerFactory.getLogger(SmartHardUtil.class);

    public static String checkDeviceId(Map<String,Object> dataMap){
        String deviceID = Convert.toString(dataMap.get("device_id"));
        if (Strings.isEmpty(deviceID)){
            return "";
        }
        int len = 16 - deviceID.length();
        if (len>0){
            for (int i = 1; i<=len; i++){
                deviceID = "0" + deviceID;
            }
        }
        dataMap.put("device_id",deviceID);
        return deviceID;
    }

    public static boolean checkUsertoken(Map<String, Object> recMap,
                                         Map<String, Object> sendMap,
                                         String userToken) throws SQLException {
        // 判断参数
        if (userToken==null || userToken.length() ==0) {
            return true;
        }
        // 请求参数中的String的参数去空格
        stringTrim(recMap);

        User user = User.getCurCacheUserByUserToken(Objects.toString(recMap.get("user_token")));

        if (user==null){
            sendMap.put("msg", SmartHardStatus.NOT_USER_TOKEN);
            return false;
        }
        recMap.put("userMap",user);

        return true;
    }

    public static void checkPackages(Map<String,Object> map){
        String pkStr = Convert.toString(map.get("packages"));
        pkStr = pkStr.substring(0,pkStr.length());
        map.put("packages",pkStr);
    }

    /**
     * 判断参数是否为null
     *
     * @param recMap
     * @param sendMap
     * @param params
     * @return
     */
    public static boolean isParamNull(Map<String, Object> recMap,
                                      Map<String, Object> sendMap,
                                      String... params) {

        for (String param : params) {
            Object obj = recMap.get(param);
            if (obj == null || Strings.isEmpty(Objects.toString(obj))) {
                sendMap.put("msg", SmartHardStatus.PARAM_ERROR + "_" + param);
                return true;
            }
        }
        return false;
    }

    /**
     * 请求参数中的String的参数去空格
     *
     * @param recMap
     */
    public static void stringTrim(Map<String, Object> recMap) {

        try {
            if (recMap == null) {
                return;
            }
            for (String param : recMap.keySet()) {
                if (recMap.get(param) == null) {
                    continue;
                }
                if (recMap.get(param) instanceof String) {
                    recMap.put(param, Objects.toString(recMap.get(param)).trim());
                }
            }
        }
        catch (Exception e) {
            log.error("stringTrim error:" + e);
        }
    }

    /**
     * 检验imei,如果正确返回0
     *
     * @param code
     * @return
     */
    public static int checkImei(String code) {
        if (15 != code.length()) {
            return -1;
        }
        int total = 0, sum1 = 0, sum2 = 0;
        int temp = 0;
        char[] chs = code.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            int num = chs[i] - '0'; // ascii to num
            // System.out.println(num);
            /* (1)将奇数位数字相加(从1开始计数) */
            if (i % 2 == 0) {
                sum1 = sum1 + num;
            }
            else {
                /* (2)将偶数位数字分别乘以2,分别计算个位数和十位数之和(从1开始计数) */
                temp = num * 2;
                if (temp < 10) {
                    sum2 = sum2 + temp;
                }
                else {
                    sum2 = sum2 + temp + 1 - 10;
                }
            }
        }
        total = sum1 + sum2;
        /* 如果得出的数个位是0则校验位为0,否则为10减去个位数 */
        if (total % 10 == 0) {
            return 0;
        }
        else {
            return (10 - (total % 10));
        }

    }

    /**
     * 日期格式转换yyyy-MM-dd'T'HH:mm:ss.SSSXXX (yyyy-MM-dd'T'HH:mm:ss.SSSZ) TO
     * yyyy-MM-dd HH:mm:ss
     *
     * @throws ParseException
     */
    public static String dealDateFormat(String oldDateStr) throws ParseException {
        // 此格式只有 jdk 1.7才支持 yyyy-MM-dd'T'HH:mm:ss.SSSXXX

        String str = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        Date date = sd.parse(oldDateStr);
        str = sdf.format(date);
        // Date date3 = df2.parse(date1.toString());
        return str;
    }

    /**
     * 读取文件
     *
     * @param path
     * @return
     */
    public static String readFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        String laststr = "";
        try {
            // System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                laststr = laststr + tempString;
            }
            reader.close();
        }
        catch (IOException e) {
            log.error("reader close error" + e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e1) {
                    log.error("reader close error" + e1);
                }
            }
        }
        return laststr;
    }

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {

        int diff = 0;
        try {
            if (version1 == null || version2 == null) {
                return -1;
            }
            String[] versionArray1 = version1.split("\\.");// 注意此处为正则匹配，不能用.；
            String[] versionArray2 = version2.split("\\.");
            int idx = 0;
            int minLength = Math.min(versionArray1.length, versionArray2.length);// 取最小长度值
            int maxLength = Math.max(versionArray1.length, versionArray2.length);// 取最大长度值

            // 判断最小长度的版本号,位数不够的补0
            if (versionArray1.length == minLength) {
                for (int i = minLength; i < maxLength; i++) {
                    version1 = version1 + ".0";
                }
                versionArray1 = version1.split("\\.");
            }
            else {
                for (int i = minLength; i < maxLength; i++) {
                    version2 = version2 + ".0";
                }
                versionArray2 = version2.split("\\.");
            }

            while (idx < maxLength
                    && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0// 先比较长度
                    && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {// 再比较字符
                ++idx;
            }
            // 如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
            diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        }
        catch (Exception e) {
            log.error("compareVersion e ", e);
        }

        return diff;
    }

    /**
     * 将Map转化成Bean
     *
     * @param <T>
     * @param map
     * @param className
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> className) {
        try {
            if (map != null) {
                ObjectMapper mapper = new ObjectMapper(); // 转换器

                // 允许json字符串中如果比对象属性多
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                // 转换带下划线的属性为驼峰属性
                mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
                mapper.setSerializationInclusion(Include.NON_NULL);

                String json = mapper.writeValueAsString(map);
                T t = null;

                if (className == null) {
                    t = mapper.readValue(json, new TypeReference<Object>() {});
                }
                else {
                    t = (T) mapper.readValue(json, className);
                }
                return t;
            }
        }
        catch (Exception e) {
            log.error("mapToObj error :" + e);
        }
        return null;
    }

    /**
     * bean对象转化成Map
     *
     * @param t
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T t) {

        Map<String, Object> map = null;
        try {
            if (t != null) {
                String json = JsonUtil.objectToJson(t);
                map = JsonUtil.jsonToObject(json);
            }
        }
        catch (Exception e) {
            log.error("beanToMap error :" + e);
        }
        return map;
    }

    public static void main(String[] args) throws IOException {
        String xml = "<xml>\n" +
                "  <appid><![CDATA[wx2421b1c4370ec43b]]></appid>\n" +
                "  <attach><![CDATA[支付测试]]></attach>\n" +
                "  <bank_type><![CDATA[CFT]]></bank_type>\n" +
                "  <fee_type><![CDATA[CNY]]></fee_type>\n" +
                "  <is_subscribe><![CDATA[Y]]></is_subscribe>\n" +
                "  <mch_id><![CDATA[10000100]]></mch_id>\n" +
                "  <nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str>\n" +
                "  <openid><![CDATA[oUpF8uMEb4qRXf22hE3X68TekukE]]></openid>\n" +
                "  <out_trade_no><![CDATA[1409811653]]></out_trade_no>\n" +
                "  <result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign>\n" +
                "  <sub_mch_id><![CDATA[10000100]]></sub_mch_id>\n" +
                "  <time_end><![CDATA[20140903131540]]></time_end>\n" +
                "  <total_fee>1</total_fee><coupon_fee><![CDATA[10]]></coupon_fee>\n" +
                "<coupon_count><![CDATA[1]]></coupon_count>\n" +
                "<coupon_type><![CDATA[CASH]]></coupon_type>\n" +
                "<coupon_id><![CDATA[10000]]></coupon_id>\n" +
                "<coupon_fee><![CDATA[100]]></coupon_fee>\n" +
                "  <trade_type><![CDATA[JSAPI]]></trade_type>\n" +
                "  <transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id>\n" +
                "</xml>";
        Map<String,Object> map = JacksonUtil.xml2Obj(xml);
        SortedMap<Object, Object> signMap = new TreeMap<>();
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            signMap.put(entry.getKey(),entry.getValue());
        }
        System.out.println(PayCommonUtil.createSign(signMap));
    }
}
