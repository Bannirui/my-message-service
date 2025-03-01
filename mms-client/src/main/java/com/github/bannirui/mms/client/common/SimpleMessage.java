package com.github.bannirui.mms.client.common;

public class SimpleMessage {
    // 排序和分片key
    private String key;

    private String tags;

    private int delayLevel = 0;
    // 消息体
    private byte[] payload;

    public SimpleMessage() {
    }

    public SimpleMessage(byte[] payload) {
        this.payload = payload;
    }

    public SimpleMessage(String key, String tags, int delayLevel, byte[] payload) {
        this.key = key;
        this.tags = tags;
        this.delayLevel = delayLevel;
        this.payload = payload;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public int getDelayLevel() {
        return delayLevel;
    }

    public void setDelayLevel(int delayLevel) {
        this.delayLevel = delayLevel;
    }
}

