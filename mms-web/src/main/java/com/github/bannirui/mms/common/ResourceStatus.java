package com.github.bannirui.mms.common;

import lombok.Getter;

@Getter
public enum ResourceStatus {

    DELETE(0, "删除"),
    CREATE_NEW(1, "新建"),
    ENABLE(1 << 1, "可用"),
    DISABLE(1 << 2, "禁用"),
    ;

    private final Integer code;
    private final String desc;

    ResourceStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

