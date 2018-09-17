package com.biz.smarthard.entity;

import java.util.Map;

public class CommandApi implements SHEntity {

    private String channel;
    private String device;
    private String command;
    private Map<String,Object> data;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public Object Map2Bean(Map map) {
        return null;
    }

    @Override
    public void reloadTable() {

    }
}
