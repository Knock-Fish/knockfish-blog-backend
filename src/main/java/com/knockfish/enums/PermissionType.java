package com.knockfish.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限类型枚举
 */
@Getter
@AllArgsConstructor
public enum PermissionType implements BaseEnum {

    MENU("menu", "菜单"),
    BUTTON("button", "按钮"),
    DIRECTORY("directory", "目录"),
    API("api", "接口");

    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static PermissionType fromCode(String code) {
        for (PermissionType t : values()) {
            if (t.code.equalsIgnoreCase(code)) return t;
        }
        throw new IllegalArgumentException("非法权限类型: " + code);
    }
}
