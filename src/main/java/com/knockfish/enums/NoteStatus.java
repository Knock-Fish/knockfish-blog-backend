package com.knockfish.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 笔记展示状态枚举
 */
@Getter
@AllArgsConstructor
public enum NoteStatus implements BaseEnum {

    HIDE("hide", "隐藏"),
    DISPLAY("display", "显示");

    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static NoteStatus fromCode(String code) {
        if (code == null) return null;
        for (NoteStatus s : values()) {
            if (s.code.equalsIgnoreCase(code)) return s;
        }
        throw new IllegalArgumentException("非法笔记状态: " + code);
    }
}
