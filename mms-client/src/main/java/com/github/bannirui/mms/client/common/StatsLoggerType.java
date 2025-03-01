package com.github.bannirui.mms.client.common;

/**
 * 统计发送、消费日志方式
 */
public enum StatsLoggerType {

    //日志输出、异步消息
    DISK("disk"), MESSAGE("message");

    StatsLoggerType(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

