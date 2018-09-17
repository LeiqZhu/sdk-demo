package com.biz.smarthard.entity;

import java.io.Serializable;
import java.util.Map;

public interface SHEntity<T> extends Serializable{
    
    
    /**
     *  将Map转化成Bean
     * @param map
     * @return
     */
    public  T Map2Bean(Map<String, Object> map);
    
    
    /**
     * reload全表
     */
    public void reloadTable();
    
}
