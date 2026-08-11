package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileReference {
    private Long fileId;
    private Long referenceId;
    private String referenceType;
    private Long userId;
    private String fileName;
    private String filePath;
    private String mimeType;
    private Long fileSize;
    private LocalDateTime createTime;
}