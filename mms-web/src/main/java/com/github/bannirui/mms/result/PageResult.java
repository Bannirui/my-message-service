package com.github.bannirui.mms.result;

import java.util.List;
import lombok.Data;

@Data
public class PageResult<T> extends Result<List<T>> {

    private long total;

    public PageResult(long total) {
        this.total = total;
    }

    public PageResult(String message, List<T> data, Integer statusCode, long total) {
        super(message, data, statusCode);
        this.total = total;
    }

    public PageResult(boolean status, String message, List<T> data, Integer statusCode, long total) {
        super(status, message, data, statusCode);
        this.total = total;
    }

    public static <T> PageResult<T> success(long total, List<T> data) {
        return new PageResult<>(true, "操作成功", data, 20_000, total);
    }
}

