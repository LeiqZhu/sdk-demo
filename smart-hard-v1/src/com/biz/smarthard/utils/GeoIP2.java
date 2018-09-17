package com.biz.smarthard.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nutz.lang.Strings;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GeoIP2 {

    private static Log log = LogFactory.getLog(GeoIP2.class);

    private static final String fileNameCountry = "/../GeoIP2-Country.mmdb";

    private static Map<String, String> countryMap = new HashMap<>();
    private static Map<String, String> languegaMap = new HashMap<>();

    // This creates the DatabaseReader object, which should be reused across
    private static DatabaseReader geoIpCountryReader = null;

    static {
        try {
            // 国家数据库
            File databaseCountry = new File(System.getProperty("user.dir") + fileNameCountry);
            geoIpCountryReader = new DatabaseReader.Builder(databaseCountry).build();

            for (Locale locale : Locale.getAvailableLocales()) {
                if (Strings.isEmpty(locale.getCountry())) {
                    continue;
                }
                countryMap.put(locale.getCountry(),
                        locale.getLanguage() + "_" + locale.getCountry());
                languegaMap.put(locale.getCountry(), locale.getLanguage());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 是否能成功读取国家数据库
     *
     * @return
     */
    public static boolean reloadCountryDbFile() {
        try {
            File databaseCountry = new File(System.getProperty("user.dir") + fileNameCountry);
            geoIpCountryReader = new DatabaseReader.Builder(databaseCountry).build();
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getIsoCodeByIp(String ip) {
        try {
            if (geoIpCountryReader != null) {
                InetAddress ipAddress = InetAddress.getByName(ip);

                // Replace "city" with the appropriate method for your database,
                // e.g.,
                // "country".
                CountryResponse response = geoIpCountryReader.country(ipAddress);
                if (response != null) {
                    Country country = response.getCountry();

                    String countryCode = country.getIsoCode();

                    country.getNames().get("zh-CN");
                    return countryCode;

                }
            }
        }
        catch (Exception e) {
            log.error("GeoIP2 error:", e);
        }
        return "unknow";
    }

    public static void main(String[] args) {
        String country = getIsoCodeByIp("47.29.193.5");
        System.out.println(country);
    }
}
