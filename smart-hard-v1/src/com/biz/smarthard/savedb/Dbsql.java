package com.biz.smarthard.savedb;

import com.biz.smarthard.entity.user.RealInfo;
import com.sdk.core.buffer.db.DBOperation;
import com.sdk.core.buffer.db.ObjectCombineDbParam;


public class Dbsql {

    public static class shTradeDetailInsert extends ObjectCombineDbParam{

    }

    /**
     * 更新实名认证信息
     *
     * @author Administrator
     *
     */
    public static class sHRealInfoUpdate extends ObjectCombineDbParam {

        public static final String UPDATE_SQL = "UPDATE sh_realinfo SET user_token=?,id_num=?,realname=?,addr=?,phone=?,id_pic_front=?,id_pic_back=?,id_pic_inhand=?,modify_time=? WHERE device_id=?";

        @Override
        public Object[] evalParam(DBOperation opr, byte[] keyValue) {

            RealInfo realInfo = (RealInfo) decodeToObject(opr, keyValue);

            Object[] params = {
                    realInfo.getUserToken(),
                    realInfo.getIdNum(),
                    realInfo.getRealname(),
                    realInfo.getAddr(),
                    realInfo.getPhone(),
                    realInfo.getIdPicFront(),
                    realInfo.getIdPicBack(),
                    realInfo.getIdPicInhand(),
                    realInfo.getModifyTime(),
                    realInfo.getDeviceId()};
            return params;
        }

    }
    /**
     * 轮询时间配置
     *
     * @author Administrator
     *
     */
    public static class mfCheckTime {
        public static final String QUERY_ALL_SQL = "SELECT id,app_id,check_id,check_time,state,descr,check_name FROM sh_check_time";

        public static final String QUERY_RELOAD = "SELECT app_id,check_id,check_time FROM qt_check_time WHERE id=? ";
    }

}
