package com.biz.smarthard.entity.ver;

import com.biz.smarthard.bean.SHConfig;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.bean.SHDbsql.VerUpdate;
import com.biz.smarthard.utils.ReloadUtil;
import com.biz.smarthard.entity.SHEntity;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.util.Convert;
import snowfox.lang.util.Objects;

import java.util.Map;

public class VerLib implements SHEntity<VerLib> {

    private static final Logger log = LoggerFactory.getLogger(VerLib.class);

    /**
     * VerLib
     */
    private static final long serialVersionUID = 1006L;

    /**
     内核版本id
     */
    private Long libId;

    /**
     * 内核版本名称
     */
    private String libName;

    /**
     * 内核版本号
     */
    private String libVersion;

    /**
     * 下载地址
     */
    private String url;

    /**
     * 循环冗余校验码
     */
    private Long crc32;

    /**
     * 详细描述
     */
    private String desc;

    public Long getLibId() {
        return libId;
    }

    public void setLibId(Long libId) {
        this.libId = libId;
    }

    public String getLibName() {
        return libName;
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }

    public String getLibVersion() {
        return libVersion;
    }

    public void setLibVersion(String libVersion) {
        this.libVersion = libVersion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getCrc32() {
        return crc32;
    }

    public void setCrc32(Long crc32) {
        this.crc32 = crc32;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public VerLib() {
        super();
    }

    public VerLib(Long libId,
                  String libName,
                  String libVersion,
                  int sdkId,
                  Long pkgId,
                  String url,
                  Long crc32,
                  String desc) {
        super();
        this.libId = libId;
        this.libName = libName;
        this.libVersion = libVersion;
        this.url = url;
        this.crc32 = crc32;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "VerLib [libId="
               + libId
               + ", libName="
               + libName
               + ", libVersion="
               + libVersion
               + ", url="
               + url
               + ", crc32="
               + crc32
               + ", desc="
               + desc
               + "]";
    }

    @Override
    public VerLib Map2Bean(Map<String, Object> map) {

        VerLib verLib = new VerLib();

        try {
            verLib.setCrc32(Convert.toLong(map.get("crc32")));
            verLib.setDesc(Objects.toString(map.get("desc")));
            verLib.setLibId(Objects.toLong(map.get("lib_id")));
            verLib.setLibName(Objects.toString(map.get("lib_name")));
            verLib.setUrl(Objects.toString(map.get("url")));
            verLib.setLibVersion(Objects.toString(map.get("libVersion")));
        }
        catch (Exception e) {
            log.error("Entity VerLib Map2Bean error:" + e);
        }

        return verLib;
    }

    @Override
    public void reloadTable() {

        try {
            new ReloadUtil<>(VerUpdate.QUERY_VER_UPDATE_SQL,
                             SHConfig.VerUpdate,
                             VerLib.class,
                             "channel",
                             "device"
                            ).reload(false);
        }
        catch (Exception e) {
            log.error("Entity VerLib reloadTable error:" + e);
        }

    }

    public static void main(String[] args) throws Exception {
         new VerLib().reloadTable();
    }

}
