package com.biz.smarthard.bean;

public class SHDbsql {

    /**
     * faqContent
     *
     */
    public static class FAQ{
        public static final  String QUERYAQ_ALL_SQL = "SELECT project,question,answer "
                + " FROM sh_faq where stat=1";
        public static final  String QUERYAQVideo_ALL_SQL = "SELECT device,video_url "
                + " FROM sh_faq_video ";
    }

    /**
     * sh_ver_update
     *
     */
    public static class VerUpdate{
        public static final String QUERY_VER_UPDATE_SQL = "SELECT t0.lib_id,t0.channel,t0.device,t2.lib_name," +
                "t2.lib_version,t2.url,t2.crc32,t2.desc " +
                "FROM sh_ver_update t0 " +
                "LEFT JOIN sh_ver_libs t2 ON t0.lib_id = t2.lib_id " +
                "WHERE " +
                "t0.status = 1 AND t2.status = 1 ORDER BY channel";
    }

    /**
     *sh_ver_libs
     *
     */
    public static class LibUrl{
        public static final String QUERY_One_SQL = "SELECT url from sh_ver_libs WHERE down_lib=1";
    }


    public static class Usersql{
        public static final String QUERY_USER_BY_AndroidID = "SELECT * FROM sh_user WHERE android_id=?";
    }

    public static class RealInfosql{
        public static final String QUERY_RealInfo_BY_DeviceD = "SELECT * FROM sh_realinfo WHERE device_id=?";
    }
}
