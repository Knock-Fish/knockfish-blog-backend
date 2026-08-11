package com.knockfish.exception;

import com.knockfish.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局抛出接口异常状态
 */
@Slf4j
@ControllerAdvice("com.knockfish.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Void> error(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error();
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result<Void> error(CustomException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMsg());
        return Result.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // HTTP状态码400
    public Result<Void> error(NumberFormatException e) {
        log.warn("参数格式错误: {}", e.getMessage());
        return Result.error();
    }
}
