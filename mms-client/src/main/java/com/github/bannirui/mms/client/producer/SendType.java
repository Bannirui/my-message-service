package com.github.bannirui.mms.client.producer;

/**
 * 消息发送方式
 */
public enum SendType {

    SYNC("sync"), ASYNC("async"), ONEWAY("oneway");

    private String sendType;

    SendType(String sendType) {
        this.sendType = sendType;
    }

    public String getSendType() {
        return sendType;
    }
}

