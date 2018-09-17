package com.biz;

import com.biz.smarthard.savedb.SaveToDBServer;
import com.biz.smarthard.utils.GeoIP2;
import com.sdk.core.codec.SFoxAes;
import com.sdk.core.json.JsonUtil;
import com.sdk.server.HttpServer;
import com.sdk.server.RequestHandlerV2;
import com.sdk.server.ServerContext;
import com.sdk.server.UrlMap;
import snowfox.lang.util.Strings;

import java.util.Date;
import java.util.Map;

public class GBManager {

    private static final String systemPWD = "Fqhx@sdk@server";

    private static final String MSG_AES_PASSWORD = "xyzG7FBook9OverZ";

    public static class SaveToDB extends RequestHandlerV2 {
        @Override
        public void get() {

            if (this.getParameter("pwd", "").equals(systemPWD)) {

                String ret = SaveToDBServer.updateDB();

                write("Server SaveToDB ! " + new Date(System.currentTimeMillis()) + "\r\n" + ret);
            }
            else {
                write("Not allowed!");
            }
        }
    }
    /**
     * 
     * reload所有缓存(手动reload用，密码不加密)
     * 
     */
    public static class ReloadAllConf extends RequestHandlerV2 {

        @Override
        public void get() {
            try {
                if (this.getParameter("pwd").equals(systemPWD)) {

                    Map<String, Object> errorMap = AccessServer.reloadAllconf();
                    if (errorMap == null || errorMap.isEmpty()) {
                        write("reloadAllConf Success by manual operation! "
                                + new Date(System.currentTimeMillis()));
                    }
                    else {
                        write(errorMap.toString());
                    }
                    log.debug("reloadAllConf Success by Manual Operation");
                }
                else {
                    write("Not allowed!");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                write("Not allowed!");
            }

        }
    }

    /**
     * 是否能成功读取国家数据库
     * 
     */
    public static class ReloadGeoIP extends RequestHandlerV2 {

        @Override
        public void get() {
            try {
                String aesPwd = this.getParameter("pwd");
                String pwd = SFoxAes.decrypt(MSG_AES_PASSWORD, aesPwd);
                if (pwd.equals(systemPWD)) {
                    if (GeoIP2.reloadCountryDbFile()) {
                        write("ReloadGeoIP success");
                    }
                    else {
                        write("ReloadGeoIP false");
                    }
                }
                else {
                    write("Invalid parameter ");
                }
            }
            catch (Exception e) {
                log.error("reload Fail", e);
                write("fail");
            }
        }
    }

    public static class ShowAllUrl extends RequestHandlerV2 {
        @Override
        public void get() {
            try {
                String pwd = this.getParameter("pwd");
                if (pwd.equals(systemPWD)) {
                    write(ServerContext.UrlCountMap2.toString());
                }
                else {
                    write("Invalid parameter ");
                }
            }
            catch (Exception e) {
                log.error("ShowUrlCount Fail", e);
                write("fail");
            }
        }
    }

    public static class ClearAllUrl extends RequestHandlerV2 {

        @Override
        public void get() {
            try {
                String pwd = this.getParameter("pwd");
                if (pwd.equals(systemPWD)) {
                    String url = this.getParameter("url");
                    if (url == null || url.length() == 0) {
                        ServerContext.UrlCountMap2.clear();
                    }
                    else {
                        ServerContext.resetUrlRequest(url);
                    }
                    write(JsonUtil.objectToJson(ServerContext.UrlCountMap2));
                }
                else {
                    write("Invalid parameter ");
                }
            }
            catch (Exception e) {
                log.error("ShowUrlCount Fail", e);
                write("fail");
            }
        }
    }

    /**
     * 根据功能块和表名reload多个表缓存
     * 
     */
    public static class ReloadTableList extends RequestHandlerV2 {
        @Override
        public void get() {
            try {
                String aesPwd = this.getParameter("pwd");
                String pwd = SFoxAes.decrypt(MSG_AES_PASSWORD, aesPwd);
                String funcStr = this.getParameter("function");
                String tableListStr = this.getParameter("tblist");
                if (pwd.equals(systemPWD) && Strings.isNotEmpty(tableListStr)) {
                    if (AccessServer.reloadconfByname(funcStr, tableListStr)) {
                        write("ok");
                    }
                    else {
                        write("reloadconfBytblist false");
                    }
                }
                else {
                    write("Invalid parameter ");
                }
            }
            catch (Exception e) {
                log.error("reload Fail", e);
                write("fail");
            }
        }
    }


    public static void initUrlMap() {

        UrlMap.urlMap.put("/saveToDB", SaveToDB.class);

        UrlMap.urlMap.put("/reloadAllConf", ReloadAllConf.class);

        UrlMap.urlMap.put("/reloadGeoIp", ReloadGeoIP.class);

        UrlMap.urlMap.put("/reloadConf/tbList", ReloadTableList.class);

        UrlMap.urlMap.put("/showAllUrl", ShowAllUrl.class);

        UrlMap.urlMap.put("/clearAllUrl", ClearAllUrl.class);

    }

    public static void main(String[] args) throws Exception {
        System.out.println(SFoxAes.encrypt(MSG_AES_PASSWORD, "Fqhx@sdk@server"));
        HttpServer httpServer = new HttpServer();
        SaveToDBServer.updateDB();
    }

}
