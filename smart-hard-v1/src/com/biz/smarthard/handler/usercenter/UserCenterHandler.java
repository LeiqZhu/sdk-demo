package com.biz.smarthard.handler.usercenter;

import com.biz.smarthard.bean.SHConfig;
import com.biz.smarthard.bean.SmartHardStatus;
import com.biz.smarthard.bean.redis.SHBuffer;
import com.biz.smarthard.bean.SHDbsql.RealInfosql;
import com.biz.smarthard.db.JedisonDao;
import com.biz.smarthard.entity.user.RealInfo;
import com.biz.smarthard.utils.JacksonUtil;
import com.biz.smarthard.utils.SmartHardUtil;
import com.sdk.core.cache.jedis.JedisDao;
import com.sdk.core.cache.type.IHash;
import com.sdk.core.db.MySqlDao;
import com.sdk.server.HttpServer;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.time.DateUtil;
import snowfox.lang.util.Convert;
import snowfox.lang.util.R;
import snowfox.lang.util.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UserCenterHandler {

    private static final Logger log = LoggerFactory.getLogger(UserCenterHandler.class);

    public static boolean FAQRequest(Map<String, Object> recMap, Map<String, Object> sendMap){

        List<Map<String,Object>> list = new ArrayList<>();
        String video_url = null;
        try {
            String device = Convert.toString(recMap.get("device"));

            IHash<String,List<Map<String,Object>>> AQHash = JedisDao.getDao().getHash(SHConfig.AQ);

            IHash<String,Map<String,Object>> AQVHash = JedisDao.getDao().getHash(SHConfig.AQV);
            Map<String,Object> videoMap = new HashMap<>();
            if (AQVHash!=null){
                videoMap = AQVHash.hget(device);
                video_url = Convert.toString(videoMap.get("video_url"));
            }

            if (AQVHash!=null){
                list = AQHash.hget(device);
            }

            if (list==null|| list.size()==0){
                sendMap.put("msg", "no FAQ");
                return false;
            }
            sendMap.put("video_url",video_url);
            sendMap.put("list",list);
        }catch (Exception e){
            e.printStackTrace();
            log.error("FAQRequest error :", ExceptionUtils.getFullStackTrace(e));
            sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
            return false;
        }
        return true;
    }

    public  static boolean realInfoRequest(Map<String, Object> recMap, Map<String, Object> sendMap){
        Long realInfoId = 0L;
        try {
            //实名认证的逻辑
            //对该设备device_id进行实名

            Map<String,Object> dataMap = (Map<String, Object>) recMap.get("data");
            String deviceId = SmartHardUtil.checkDeviceId(dataMap);
            String userToken = Convert.toString(recMap.get("user_token"));

            if (Strings.isEmpty(deviceId)){
                sendMap.put("msg","device_id error");
                return false;
            }

            String curDay = DateUtil.today();


            realInfoId = getRealInfoByDeviceId(dataMap,userToken,deviceId,curDay);

            sendMap.put("ts", DateUtil.now());
            sendMap.put("id", R.UU16());

        }catch (Exception e){
            e.printStackTrace();
            log.error("realInfoRequest error :", ExceptionUtils.getFullStackTrace(e));
            sendMap.put("msg", SmartHardStatus.SERVER_ERROR);
        }
        return realInfoId>0;
    }

    public static Long getRealInfoByDeviceId(Map<String,Object> dataMap, String userToken, String deviceId, String curDay) throws SQLException, IOException {
        Long realInfoId = 0L;
        //先从当日缓存中获取
        IHash<String, RealInfo> curDayRealInfoHash = JedisonDao.getBuffer().getHash(SHBuffer.SHCurDayRealInfo + curDay);
        RealInfo realInfo = curDayRealInfoHash.hget(deviceId);
        if (realInfo == null){
            //如果从今日缓存中获取不到，就从mysql数据库里查询
            Object[] param = {deviceId};
            Map<String, Object> realInfoMap = MySqlDao.getDao().queryOne(RealInfosql.QUERY_RealInfo_BY_DeviceD,param);
            realInfo = RealInfo.mapToBean(realInfoMap);
        }
        if (realInfo !=null) {
            realInfoId = realInfo.getRealInfoId();
            realInfo = JacksonUtil.jackson
                    .withCamel2Lower()
                    .withIgnoreUnknowPro()
                    .obj2Bean(dataMap, RealInfo.class);
            realInfo.setUserToken(userToken);
            realInfo.setModifyTime(DateUtil.now());
            realInfo.setRealInfoId(realInfoId);

            JedisonDao.getBuffer().getLists(SHBuffer.SHRealInfoUpdateMap).rpush(realInfo);
        }else {
            realInfo = JacksonUtil.jackson
                    .withCamel2Lower()
                    .withIgnoreUnknowPro()
                    .obj2Bean(dataMap, RealInfo.class);
            realInfo.setUserToken(userToken);
            realInfo.setCreateTime(DateUtil.now());
            realInfo.setModifyTime(DateUtil.now());

            realInfo.stat();
            realInfoId = realInfo.getRealInfoId();
        }
        curDayRealInfoHash.hset(deviceId,realInfo);
        return realInfoId;
    }


    public static void main(String[] args) throws Exception {
        HttpServer server = new HttpServer();

        //String s = readFileByLines("C:\\Users\\HASEE\\Desktop\\reqMap.txt");
        //Map<String,Object> map = JsonUtil.jsonToObject(s);
//        getSmaatoAdContent(map.get("reqMap"));

    }
    public static String readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        String s = new String();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                s += tempString;
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
