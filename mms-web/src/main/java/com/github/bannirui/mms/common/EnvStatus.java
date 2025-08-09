package com.github.bannirui.mms.common;

import lombok.Getter;

@Getter
public enum EnvStatus {

    CREATE_NEW(1, "新建"),
    ENABLE(1 << 1, "可用"),
    DISABLE(1 << 2, "禁用"),
    ;

    private final Integer code;
    private final String desc;

    EnvStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

