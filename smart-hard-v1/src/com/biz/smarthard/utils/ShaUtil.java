package com.biz.smarthard.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author mtdhllf
 *         Created on 2018/02/01.
 */
public class ShaUtil {

    private static final char hexDigits[] =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String bytes2HexString(final byte[] bytes) {
        if (bytes == null){
            return null;
        }
        int len = bytes.length;
        if (len <= 0){
            return null;
        }
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    /**
     * HmacSHA512 加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16 进制密文
     */
    public static String encryptHmacSHA512ToString(final String data, final String key) {
        return encryptHmacSHA512ToString(data.getBytes(), key.getBytes());
    }

    /**
     * HmacSHA256 加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16 进制密文
     */
    public static String encryptHmacSHA256ToString(final String data, final String key) {
        return encryptHmacSHA256ToString(data.getBytes(), key.getBytes());
    }

    /**
     * HmacSHA512 加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16 进制密文
     */
    public static String encryptHmacSHA512ToString(final byte[] data, final byte[] key) {
        return bytes2HexString(encryptHmacSHA512(data, key));
    }

    /**
     * HmacSHA512 加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16 进制密文
     */
    public static String encryptHmacSHA256ToString(final byte[] data, final byte[] key) {
        return bytes2HexString(encryptHmacSHA256(data, key));
    }

    /**
     * HmacSHA512 加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    public static byte[] encryptHmacSHA512(final byte[] data, final byte[] key) {
        return hmacTemplate(data, key, "HmacSHA512");
    }

    /**
     * HmacSHA256 加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    public static byte[] encryptHmacSHA256(final byte[] data, final byte[] key) {
        return hmacTemplate(data, key, "HmacSHA256");
    }


    /**
     * Hmac 加密模板
     *
     * @param data      数据
     * @param key       秘钥
     * @param algorithm 加密算法
     * @return 密文字节数组
     */
    private static byte[] hmacTemplate(final byte[] data,
                                       final byte[] key,
                                       final String algorithm) {
        if (data == null || data.length == 0 || key == null || key.length == 0) {
            return null;
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
            return mac.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] encodeHmacSHA512(byte[] data,byte[] key) throws Exception{
        //还原密钥，因为密钥是以byte形式为消息传递算法所拥有
        SecretKey secretKey=new SecretKeySpec(key,"HmacSHA512");
        //实例化Mac
        Mac mac=Mac.getInstance(secretKey.getAlgorithm());
        //初始化Mac
        mac.init(secretKey);
        //执行消息摘要处理
        return mac.doFinal(data);
    }

    public static byte[] initHmacSHA512Key() throws Exception{
        //初始化KeyGenerator
        KeyGenerator keyGenerator=KeyGenerator.getInstance("HmacSHA512");
        //产生密钥
        SecretKey secretKey=keyGenerator.generateKey();
        //获取密钥
        return secretKey.getEncoded();
    }

    public static void main(String[] args) throws Exception {
        String str="123000000000090989999999999999999999999999999999999999999999999999999999999999999999111" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "23000000000090989999999999999999999999999999999999999999999999999999999999999999999" +
                "230000000000909899999999999999999999999999999999999999999999999999999999999999999991";

        //初始化密钥
        byte[] key5=initHmacSHA512Key();
        //获取摘要信息
        byte[] data5=encodeHmacSHA512(str.getBytes(), key5);
        System.out.println("HmacSHA512的密钥:"+key5.toString());
        System.out.println("HmacSHA512算法摘要："+ new String(data5,"utf-8"));
        System.out.println();
    }

}
