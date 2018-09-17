package com.biz.smarthard.handler;

import com.biz.smarthard.utils.GeoIP2;
import com.sdk.server.RequestHandlerV2;
import io.netty.handler.codec.http.FullHttpResponse;
import snowfox.lang.util.Convert;
import snowfox.lang.util.Objects;

import java.util.HashMap;
import java.util.Map;

public class SmartHardRequestHandler extends RequestHandlerV2 {
    @Override
    public void post(Map<String, Object> reqMap) {

        String clientIp = Convert.toString(this.getClientIp());
        String clientUa = Convert.toString(this.getClientUA());
        reqMap.put("client_ip", clientIp);
        reqMap.put("client_ua", clientUa);
        reqMap.put("country_code", GeoIP2.getIsoCodeByIp(clientIp));
        SHPost(reqMap);
        write(sendMap);
    }

    @Override
    public void get() {
        Map<String,Object> reqMap = new HashMap<>();
        String android_id = this.getParameter("android_id", "");
        String serial_number = this.getParameter("serial_number", "");
        reqMap.put("android_id",android_id);
        reqMap.put("serial_number",serial_number);
        String clientIp = this.getClientIp();
        String clientUa = this.getClientUA();
        reqMap.put("client_ip", Objects.toString(clientIp));
        reqMap.put("client_ua", Objects.toString(clientUa));
        reqMap.put("country_code", GeoIP2.getIsoCodeByIp(clientIp));

        SHGet(reqMap);
    }

    public void SHGet(Map<String, Object> recMap) {

    }

    public void SHPost(Map<String, Object> recMap) {

    }
    @Override
    protected void addServerHeader(final FullHttpResponse response) {
        response.headers().set("Access-Control-Allow-Origin", "*");
        response.headers().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.headers().set("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
    }

}
