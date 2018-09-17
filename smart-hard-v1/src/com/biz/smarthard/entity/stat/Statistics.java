/**
 * 
 */
package com.biz.smarthard.entity.stat;

import com.biz.smarthard.entity.AddToRedis;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
@JsonInclude(Include.NON_EMPTY)
    public abstract class Statistics extends AddToRedis implements Serializable, Cloneable {

    /**
     * Statistics
     */
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String appId;

    private String clientIp;

    private String countryCode;

    private String imei;

    private String createTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public Statistics() {
        super();
    }

    public Statistics(String appId, String imei, String createTime) {
        super();
        this.appId = appId;
        this.imei = imei;
        this.createTime = createTime;
    }

    public Statistics(Long userId,
                      String appId,
                      String clientIp,
                      String countryCode,
                      String imei,
                      String createTime) {
        super();
        this.userId = userId;
        this.appId = appId;
        this.clientIp = clientIp;
        this.countryCode = countryCode;
        this.imei = imei;
        this.createTime = createTime;
    }

}
