package com.github.bannirui.mms.controller;

import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<Void> handleAll(Exception e) {
        return Result.error("服务器异常: " + e.getMessage());
    }

    @ExceptionHandler(MmsException.class)
    public Result<Void> handleBiz(MmsException e) {
        return Result.error("业务异常: " + e.getMessage());
    }
}
