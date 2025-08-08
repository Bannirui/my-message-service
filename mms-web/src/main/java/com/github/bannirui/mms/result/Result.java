package com.github.bannirui.mms.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean status = false;
    private String statusCode;
    private String message;
    private T result;

    public Result() {
    }

    public Result(String message, T result, String statusCode) {
        this.message = message;
        this.result = result;
        this.statusCode = statusCode;
    }

    public Result(boolean status, String message, T result, String statusCode) {
        this.status = status;
        this.message = message;
        this.result = result;
        this.statusCode = statusCode;
    }

    public static <T> Result<T> error(String statusCode, String message) {
        return new Result<>(message, null, statusCode);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, "操作成功", data, "SYS000");
    }
}


