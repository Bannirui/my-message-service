package com.github.bannirui.mms.common;

import lombok.Getter;

@Getter
public enum ResourceStatus {

    DELETE(0, "删除"),
    // 新建/待审批
    CREATE_NEW(1, "新建"),
    ENABLE(1 << 1, "可用"),
    DISABLE(1 << 2, "禁用"),

    CREATE_APPROVED(1 << 3, "已审批"),
    UPDATE_NEW(1 << 4, "待审批"),
    UPDATE_APPROVED(1 << 5, "已审批"),
    ;

    public static final int ENABLE_MASK = ENABLE.getCode() | CREATE_APPROVED.getCode() | UPDATE_APPROVED.getCode();

    private final Integer code;
    private final String desc;

    ResourceStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

