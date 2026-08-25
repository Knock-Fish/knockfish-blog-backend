package com.knockfish.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限状态枚举
 */
@Getter
@AllArgsConstructor
public enum PermissionStatus implements BaseEnum {

    ENABLE("enable", "启用"),
    DISABLE("disable", "禁用");

    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static PermissionStatus fromCode(String code) {
        for (PermissionStatus s : values()) {
            if (s.code.equalsIgnoreCase(code)) return s;
        }
        throw new IllegalArgumentException("非法权限状态: " + code);
    }
}
