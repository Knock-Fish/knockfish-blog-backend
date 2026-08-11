package com.knockfish.entity;

import com.knockfish.enums.PermissionStatus;
import com.knockfish.enums.PermissionType;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class Permission {
    private Long permissionId;
    private String permissionName;
    private String permissionCode;
    private PermissionType type;
    private int parentId;
    private String routeName;
    private String path;
    private String icon;
    private int hidden;
    private int keepAlive;
    private String component;
    private int sortOrder;
    private LocalDateTime createTime;
    private PermissionStatus status;
}
