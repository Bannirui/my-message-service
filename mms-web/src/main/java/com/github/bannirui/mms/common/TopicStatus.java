package com.github.bannirui.mms.common;

import lombok.Getter;

@Getter
public enum TopicStatus {

    DELETED(0, "已删除"),
    CREATE_NEW(1, "待审批"),
    CREATE_APPROVED(1 << 1, "已审批"),
    UPDATE_NEW(1 << 2, "待审批"),
    UPDATE_APPROVED(1 << 3, "已审批"),
    ;

    private final Integer code;
    private final String desc;

    TopicStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

