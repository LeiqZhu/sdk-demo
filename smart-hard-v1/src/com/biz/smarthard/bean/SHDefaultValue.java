package com.biz.smarthard.bean;

public class SHDefaultValue {

    // 资讯列表数量
    public static final int ListNewsNum = 10;

    // Feature资讯数量
    public static final int FeatureNewsNum = 2;

    // 初始化接口轮询时间
    public static final long CheckTime_InitConf = 8 * 60 * 60 * 1000L;

    // 红点接口轮询时间
    public static final long CheckTime_Repoint = 2 * 60 * 60 * 1000L;

    // 统计接口轮询时间
    public static final long CheckTime_Statics = 2 * 60 * 60 * 1000L;

    // 接口轮询时间
    public static final long CheckTime_ChangePage = 8 * 60 * 60 * 1000L;

    // 更新接口轮询时间
    public static final long CheckTime_Update = 8 * 60 * 60 * 1000L;

    // 上锁时间
    public static final int getNewsLeaseTime = 30 * 60 * 1000;

    // 初始化获取新闻时间
    public static final Long initGetNewsInterval = 2 * 60 * 60 * 1000L;

    // 初始化获取saveToDB时间
    public static final long initUpdateDBInterval = 6 * 60 * 1000L;
}
