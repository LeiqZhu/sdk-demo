/**
 * 
 */
package com.biz.smarthard.bean.redis;

/**
 * @author Administrator
 *
 */
public class SHSysConfig {

    /**
     * 总配置
     */
    public static final String allConfigData = "config:config_data";

    /**
     * 总配置
     */
    public static final String allConfig = "config:config_all";

    /**
     * 上次获取新闻时间
     */
    public static final String lastGetNewsTime = "config:configdata:lastGetNewsTime";

    /**
     * 每次获取新闻间隔时间
     */
    public static final String configGetNewsInterval = "config:getNewsInterval"; // 毫秒

    /**
     * 每次获取save2DB间隔时间
     */
    public static final String configUpdateDBInterval = "config:updateDBInterval"; // 毫秒

    /**
     * 上次save2DB时间
     */
    public static final String lastUpdateDBTime = "config:configdata:lastUpdateDBTime";

    /**
     * 每次间隔时间
     */
    public static final String IntervalTime = "config:Scheduled:Interval:";

    /**
     * 每次间隔时间
     */
    public static final String LastTime = "config:Scheduled:LastTime:";
}
