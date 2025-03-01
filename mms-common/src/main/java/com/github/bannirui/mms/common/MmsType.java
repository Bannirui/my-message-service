package com.github.bannirui.mms.common;

public enum MmsType {

    TOPIC("topic"),
    CONSUMER_GROUP("consumergroup");

    private String name;

    MmsType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
