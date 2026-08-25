package com.knockfish.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章状态枚举
 */
@Getter
@AllArgsConstructor
public enum ArticleStatus implements BaseEnum {

    PUBLISH("publish", "已发布"),
    DRAFT("draft", "草稿");

    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static ArticleStatus fromCode(String code) {
        for (ArticleStatus s : values()) {
            if (s.code.equalsIgnoreCase(code)) return s;
        }
        throw new IllegalArgumentException("非法文章状态: " + code);
    }
}
