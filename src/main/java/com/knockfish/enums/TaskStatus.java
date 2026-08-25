package com.knockfish.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus implements BaseEnum {

    TODO("todo",       "未开始"),
    DOING("doing",     "进行中"),
    DONE("done",       "已完成"),
    DELAY("delay",     "已延期"),
    CANCEL("cancel",   "已取消");

    @JsonValue
    private final String code;
    private final String label;

    @JsonCreator
    public static TaskStatus fromCode(String code) {
        for (TaskStatus s : values()) {
            if (s.code.equalsIgnoreCase(code)) return s;
        }
        throw new IllegalArgumentException("非法任务状态: " + code);
    }
}
