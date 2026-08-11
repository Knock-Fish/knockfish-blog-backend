package com.knockfish.vo.file_reference;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class FileReferenceVO {
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