package com.github.bannirui.mms.result;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private boolean status = false;
    private Integer code;
    private String message;
    private long total;
    private List<T> data;

    public PageResult() {
    }

    public PageResult(boolean status, Integer statusCode, String message, long total, List<T> data) {
        this.status = status;
        this.code = statusCode;
        this.message = message;
        this.total = total;
        this.data = data;
    }

    public static <T> PageResult<T> error(Integer statusCode, String message) {
        return new PageResult<>(false, statusCode, message, 0L, null);
    }

    public static <T> PageResult<T> success(long total, List<T> data) {
        return new PageResult<>(true, 20_000, "操作成功", total, data);
    }
}

