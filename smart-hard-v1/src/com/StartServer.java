package com;

// import org.apache.log4j.PropertyConfigurator;

import com.biz.AccessServer;
import com.biz.smarthard.SmartHardServer;
import com.biz.smarthard.scheduled.ScheduleServer;
import com.sdk.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowfox.lang.time.DateUtil;
import snowfox.lang.util.NetUtil;

public class StartServer {

    static Logger log = LoggerFactory.getLogger(StartServer.class);

    public static void main(String[] args) throws Exception {

        // 需要使用到HttpServer里面的initConfig方法执行netty的参数载入
        HttpServer server = new HttpServer();

        // 判断端口是否被占用
        if (NetUtil.isLocalPortUsing(server.getConfig().port)) {
            System.out.println("端口被占用："+server.getConfig().port);
            return;
        }

        // 读取缓存数据
        AccessServer.initconf();

        // 定时任务
        ScheduleServer.doSchedule();

        // 初始化请求路径映射
        SmartHardServer.initUrlMap();

        System.out.println("server init start time: " + DateUtil.now());
        server.start();

    }
}
