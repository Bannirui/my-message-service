package com.github.bannirui.mms.common;

public enum ResourceStatus {

    SOFT_DELETED(0, "已删除"),
    CREATE_NEW(1, "待审批"),
    CREATE_APPROVED(1 << 1, "已审批"),
    UPDATE_NEW(1 << 2, "待审批"),
    UPDATE_APPROVED(1 << 3, "已审批");

    private Integer status;
    private String showValue;

    ResourceStatus(Integer status, String showValue) {
        this.status = status;
        this.showValue = showValue;
    }

    public String getShowValue() {
        return showValue;
    }

    public void setShowValue(String showValue) {
        this.showValue = showValue;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

