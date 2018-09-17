package com.biz.smarthard.entity.user;

import com.biz.smarthard.bean.redis.SHBuffer;
import com.biz.smarthard.bean.redis.SHId;
import com.biz.smarthard.entity.AddToRedis;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdk.core.buffer.db.DBOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.util.Convert;
import snowfox.lang.util.Objects;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@JsonInclude(Include.NON_EMPTY)
public class RealInfo extends AddToRedis{

    static Logger log = LoggerFactory.getLogger(RealInfo.class);

    private static final long serialVersionUID = 1000L;

    private Long realinfoId;

    private String userToken;

    /**
     * 设备ID 长度16不足16高位补0
     */
    private String deviceId;

    /**
     * 身份证号
     */
    private String idNum;

    /**
     * 姓名
     */
    private String realname;

    /**
     * 住址
     */
    private String addr;

    /**
     * 电话
     */
    private String phone;

    /**
     * 身份证照片 正面
     */
    private String idPicFront;

    /**
     * 身份证照片 背面
     */
    private String idPicBack;

    /**
     * 身份证照片 手持
     */
    private String idPicInhand;

    private String createTime;

    private String modifyTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getRealInfoId() {
        return getId();
    }

    public void setRealInfoId(Long realInfoId) {
        this.realinfoId = realInfoId;
        this.setId(realInfoId);
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdPicFront() {
        return idPicFront;
    }

    public void setIdPicFront(String idPicFront) {
        this.idPicFront = idPicFront;
    }

    public String getIdPicBack() {
        return idPicBack;
    }

    public void setIdPicBack(String idPicBack) {
        this.idPicBack = idPicBack;
    }

    public String getIdPicInhand() {
        return idPicInhand;
    }

    public void setIdPicInhand(String idPicInhand) {
        this.idPicInhand = idPicInhand;
    }

    @Override
    public void setId(Long id) {
        this.realinfoId = id;
        super.setId(id);
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public RealInfo() {
    }

    @Override
    public void setRedisId() {
        this.redisId = SHId.incrRealInfoId;
    }

    @Override
    public void setRedisKey() {
        this.redisKey = SHBuffer.SHRealInfoInsertMap;
    }

    @Override
    public void setInsertSql() {
        this.insertSql = "INSERT IGNORE INTO sh_realinfo(realinfo_id,user_token," +
                "device_id,id_num,realname,addr,phone,id_pic_front,id_pic_back,id_pic_inhand,create_time,modify_time ) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)";
    }

    @Override
    public Object[] evalParam(DBOperation opr, byte[] keyValue) {

        RealInfo realInfo = (RealInfo) decodeToObject(opr, keyValue);

        Object[] params = {realInfo.getRealInfoId(),
                realInfo.getUserToken(),
                realInfo.getDeviceId(),
                realInfo.getIdNum(),
                realInfo.getRealname(),
                realInfo.getAddr(),
                realInfo.getPhone(),
                realInfo.getIdPicFront(),
                realInfo.getIdPicBack(),
                realInfo.getIdPicInhand(),
                realInfo.getCreateTime(),
                realInfo.getModifyTime()};
        return params;
    }

    public static RealInfo mapToBean(Map<String,Object> map){
        RealInfo realInfo = new RealInfo();
        realInfo.setRealInfoId(Convert.toLong(map.get("realinfo_id")));
        realInfo.setDeviceId(Objects.toString(map.get("device_id")));
        realInfo.setAddr(Objects.toString(map.get("addr")));
        realInfo.setIdNum(Objects.toString(map.get("id_num")));
        realInfo.setIdPicBack(Objects.toString(map.get("id_pic_back")));
        realInfo.setIdPicFront(Objects.toString(map.get("id_pic_front")));
        realInfo.setIdPicInhand(Objects.toString(map.get("id_pic_inhand")));
        realInfo.setPhone(Objects.toString(map.get("phone")));
        realInfo.setRealname(Objects.toString(map.get("realname")));
        return realInfo;
    }

    public static void toImg(byte[] data, String path){
        if(data.length<3||path.equals("")){
            return;
        }
        try{
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            log.debug("Make Picture success,Please find image in :======" + path);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    public static byte[] toByte(String path){
        byte[] data = null;
        FileImageInputStream input = null;
        try {
            input = new FileImageInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        }
        catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }

}
