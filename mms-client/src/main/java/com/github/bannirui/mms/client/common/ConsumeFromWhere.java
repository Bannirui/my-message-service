package com.github.bannirui.mms.client.common;

/**
 * consumer的消费策略
 */
public enum ConsumeFromWhere {

    EARLIEST("earliest"),
    LATEST("latest"),
    NONE("none"),
    ;

    private String name;

    ConsumeFromWhere(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

