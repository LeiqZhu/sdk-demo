package com.biz.smarthard.bean.redis;

/**
 * @author Administrator
 *
 */
public class SHData {

    // -------------------------------------User---------------------------------------------------------------------

    /**
     * 缓存user_token-user_id
     */

    /**
     * 缓存user_token-user_id
     */
    public static final String Data_UserId_Token = "sh:Data:User:UserIdToken";

    /**
     * user_id-userMap当日用户缓存
     */
    public static final String Data_Cur_UserId_UserMap = "sh:Data:User:UserMap:";


    /**
     * 当日支付订单信息
     */
    public static final String Data_Cur_OrderMap = "sh:Data:Pay:OrderMap:";

    /**
     * 当日支付订单查询结果
     */
    public static final String Data_Cur_OrderDetail = "sh:Data:Pay:OrderDetail:";

    /**
     * deviceId对应商户订单信息 + deviceId
     *
     */
    public static final String Data_Device_Order = "sh:Data:Pay:DeviceOrder:";

    /**
     * 所有deviceid对应的所有订单
     *
     */
    public static final String Data_All_Device_Order = "sh:Data:Pay:DeviceOrder";

    /**
     * 应用网关接口消息
     */
    public static final String Data_App_Notify = "sh:Data:AppNotify";

}
