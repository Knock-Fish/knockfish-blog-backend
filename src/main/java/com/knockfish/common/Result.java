package com.knockfish.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * 统一后端返回的数据类型
 */
@Setter
@Getter
@Schema(description = "统一响应结果")
public class Result<T> {
    @Schema(description = "状态码", example = "200", defaultValue = "200")
    private int code;

    @Schema(description = "响应消息", example = "请求成功")
    private String msg;

    @Schema(description = "响应数据")
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(HttpStatus.OK.value());  // 200
        result.setMsg("请求成功");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = success();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());   // 500
        result.setMsg("系统错误");
        return result;
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
