package com.github.bannirui.mms.client.common;

import com.github.bannirui.mms.client.producer.SendType;

import java.util.Map;

public class MmsMessage extends SimpleMessage {

    //同步 异步 oneway
    private SendType sendType;

    // 消息属性列表 目前只支持RocketMQ
    private Map<String, String> properties;

    public MmsMessage() {
        this.sendType = SendType.SYNC;
    }

    public MmsMessage(SimpleMessage simpleMessage) {
        super(simpleMessage.getKey(), simpleMessage.getTags(), simpleMessage.getDelayLevel(), simpleMessage.getPayload());
    }

    public MmsMessage(String key, String tags, int delayLevel, byte[] payload, SendType sendType) {
        super(key, tags, delayLevel, payload);
        this.sendType = sendType;
    }

    public MmsMessage(String key, String tags, int delayLevel, byte[] payload, SendType sendType, Map<String, String> properties) {
        super(key, tags, delayLevel, payload);
        this.sendType = sendType;
        this.properties = properties;
    }

    public void setSendType(SendType sendType) {
        this.sendType = sendType;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public SendType getSendType() {
        return sendType;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "MmsMessage{" +
                "sendType=" + sendType +
                ", properties=" + properties +
                '}' + super.toString();
    }
}