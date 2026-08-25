package com.knockfish.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 友链展示状态枚举
 */
@Getter
@AllArgsConstructor
public enum LinkStatus implements BaseEnum {

    HIDE("hide", "隐藏"),
    DISPLAY("display", "展示");

    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static LinkStatus fromCode(String code) {
        for (LinkStatus s : values()) {
            if (s.code.equalsIgnoreCase(code)) return s;
        }
        throw new IllegalArgumentException("非法友链状态: " + code);
    }
}
