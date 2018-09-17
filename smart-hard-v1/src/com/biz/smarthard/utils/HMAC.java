package com.biz.smarthard.utils;

import com.sdk.core.codec.SFoxRsa;
import org.nutz.lang.Strings;
import snowfox.lang.encrypt.Rsa;
import snowfox.lang.util.R;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
     * Created by xiang.li on 2015/2/27.
     */
    public class HMAC {
        /**
         * 定义加密方式
         * MAC算法可选以下多种算法
         * <pre>
         * HmacMD5
         * HmacSHA1
         * HmacSHA256
         * HmacSHA384
         * HmacSHA512
         * </pre>
         */
        private final static String KEY_MAC_512= "HmacSHA512";
        private final static String KEY_MAC_256= "HmacSHA256";

        /**
         * 全局数组
         */
        private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

        /**
         * 构造函数
         */
        public HMAC() {

        }

        /**
         * BASE64 加密
         * @param key 需要加密的字节数组
         * @return 字符串
         * @throws Exception
         */
        public static String encryptBase64(byte[] key) throws Exception {
            return (new BASE64Encoder()).encodeBuffer(key);
        }

        /**
         * BASE64 解密
         * @param key 需要解密的字符串
         * @return 字节数组
         * @throws Exception
         */
        public static byte[] decryptBase64(String key) throws Exception {
            return (new BASE64Decoder()).decodeBuffer(key);
        }

        /**
         * 初始化HMAC密钥
         * @return
         */
        public static String init(String algorithm) {
            SecretKey key;
            String str = "";
            try {
                KeyGenerator generator = KeyGenerator.getInstance(algorithm);
                key = generator.generateKey();
                str = encryptBase64(key.getEncoded());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str;
        }

        /**
         * HMAC加密
         * @param data 需要加密的字节数组
         * @param key 密钥
         * @return 字节数组
         */
        public static byte[] encryptHMAC(byte[] data, String key, String algorithm) {
            SecretKey secretKey;
            byte[] bytes = null;
            try {
                secretKey = new SecretKeySpec(decryptBase64(key), algorithm);
                Mac mac = Mac.getInstance(secretKey.getAlgorithm());
                mac.init(secretKey);
                bytes = mac.doFinal(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bytes;
        }

        /**
         * HMAC加密
         * @param data 需要加密的字符串
         * @param key 密钥
         * @return 字符串
         */
        public static String encryptHMAC512(String data, String key) {
            if (Strings.isEmpty(data)) {
                return null;
            }
            byte[] bytes = encryptHMAC(data.getBytes(), key, KEY_MAC_512);
            return byteArrayToHexString(bytes);
        }


        /**
         * 将一个字节转化成十六进制形式的字符串
         * @param b 字节数组
         * @return 字符串
         */
        private static String byteToHexString(byte b) {
            int ret = b;
            //System.out.println("ret = " + ret);
            if (ret < 0) {
                ret += 256;
            }
            int m = ret / 16;
            int n = ret % 16;
            return hexDigits[m] + hexDigits[n];
        }

        /**
         * 转换字节数组为十六进制字符串
         * @param bytes 字节数组
         * @return 十六进制字符串
         */
        private static String byteArrayToHexString(byte[] bytes) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(byteToHexString(bytes[i]));
            }
            return sb.toString();
        }

        /**
         * 测试方法
         * @param args
         */
        public static void main(String[] args) throws Exception {
            String key = HMAC.init(KEY_MAC_512);
            System.out.println("Mac密钥:" + key);
            String word = "123";
            System.out.println(encryptHMAC512(word, key));

            System.out.println(R.UU16());

            System.out.println(new String(Base64.getEncoder().encode(SFoxRsa.UserPriKey.getEncoded())));
            System.out.println(new String(Base64.getEncoder().encode(SFoxRsa.UserPubKey.getEncoded())));

        }
    }
