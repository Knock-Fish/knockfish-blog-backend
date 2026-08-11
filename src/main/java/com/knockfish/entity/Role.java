package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Role {
    private Long roleId;
    private String roleName;
    private String description;
    private LocalDateTime createTime;
}
