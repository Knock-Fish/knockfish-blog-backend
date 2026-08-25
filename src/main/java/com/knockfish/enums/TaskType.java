package com.knockfish.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskType implements BaseEnum {

    TASK("task",                 "普通任务"),
    MILESTONE("milestone",       "里程碑"),
    PROJECT("project",           "汇总任务");

    @JsonValue
    private final String code;
    private final String label;

    @JsonCreator
    public static TaskType fromCode(String code) {
        for (TaskType t : values()) {
            if (t.code.equalsIgnoreCase(code)) return t;
        }
        throw new IllegalArgumentException("非法任务类型: " + code);
    }
}
