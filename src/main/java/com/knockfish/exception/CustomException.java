package com.knockfish.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义抛出异常类
 */
@Getter
@Setter
public class CustomException extends RuntimeException {
    private int code;
    private String msg;
    public CustomException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
