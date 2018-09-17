package com.biz.smarthard.savedb;

import com.biz.smarthard.bean.redis.SHBuffer;
import com.biz.smarthard.entity.pay.TradeDetail;
import com.biz.smarthard.entity.user.RealInfo;
import com.biz.smarthard.entity.user.User;
import com.biz.smarthard.savedb.Dbsql.sHRealInfoUpdate;
import com.sdk.core.buffer.db.DBOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class SaveServer {

    static final Logger log = LoggerFactory.getLogger(SaveServer.class);

    public static List<DBOperation> lstUpdateDBOperation = null;

    static {

        try {

            DBOperation db = null;

            lstUpdateDBOperation = new LinkedList<DBOperation>();

             //sh_user
            db = new DBOperation(SHBuffer.SHUserInsertMap,
                                 null,
                                 new User().getInsertSql(),
                                 new User(),
                                 50);
            db.setBatchInsert(true);
            lstUpdateDBOperation.add(db);

            //sh_realinfo insert
            db = new DBOperation(SHBuffer.SHRealInfoInsertMap,
                    null,
                    new RealInfo().getInsertSql(),
                    new RealInfo(),
                    50);
            db.setBatchInsert(true);
            lstUpdateDBOperation.add(db);

            //sh_realinfo update
            db = new DBOperation(SHBuffer.SHRealInfoUpdateMap,
                    null,
                    Dbsql.sHRealInfoUpdate.UPDATE_SQL,
                    new sHRealInfoUpdate(),
                    50);
            lstUpdateDBOperation.add(db);

            //sh_TradeDetail update
            db = new DBOperation(SHBuffer.SHTradeInsertMap,
                    null,
                    new TradeDetail().getInsertSql(),
                    new TradeDetail(),
                    50);
            db.setBatchInsert(true);
            lstUpdateDBOperation.add(db);

        }
        catch (Exception e) {
            System.out.println("e:" + e);
        }

    }

}
