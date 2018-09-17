package com.biz.smarthard.utils;

import java.util.Map;


public interface IGeoIP2 {
    
    
    /**
     *  根据ip获取国家码
     * @param ip
     * @return
     */
    public  String getIsoCodeByIp(String ip);
    
    
    /**
     *  获取国家信息
     * @param ip
     * @return
     */
    public  Map<String,String> getCountryMapByIp(String ip);
    
    
    /**
     *  根据ip获取语言
     * @param ip
     * @return
     */
    public String getLanguegaByIp(String ip);
    
    
    /**
     * 根据ip获取城市码
     * 
     * @param ip
     */
    public  String getCityCodeByIp(String ip);
    
    /**
     *  根据ip获取城市信息
     * @param ip
     * @return
     */
    public Map<String,String> getCityMapByIp(String ip);
    
    /**
     *  根据ip获取经度
     * @param ip
     * @return
     */
    public Map<String,Object> getLocationByIp(String ip);
    
    
    /**
     *  获取ip地址信息
     * @param ip
     * @return
     */
    public Map<String,Object> getAddressAllByIp(String ip);
    
    /**
     * 获取国家所有信息
     * @param ip
     * @return
     */
    public Map<String,Object> getCountryAllByIp(String ip);
    
    /**
     * 获取城市所有信息
     * 
     * @param ip
     * @return
     */
    public Map<String, Object> getCityAllByIp(String ip);
}
