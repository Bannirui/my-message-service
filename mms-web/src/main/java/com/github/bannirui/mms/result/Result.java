package com.github.bannirui.mms.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean status = false;
    private Integer code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(String message, T data, Integer statusCode) {
        this.message = message;
        this.data = data;
        this.code = statusCode;
    }

    public Result(boolean status, String message, T data, Integer statusCode) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.code = statusCode;
    }

    public static <T> Result<T> error(Integer statusCode, String message) {
        return new Result<>(message, null, statusCode);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, "操作成功", data, 20_000);
    }
}


