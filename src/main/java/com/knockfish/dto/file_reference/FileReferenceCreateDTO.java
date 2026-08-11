package com.knockfish.dto.file_reference;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileReferenceCreateDTO {
    @NotNull(message = "关联业务ID不能为空")
    private Long referenceId;

    @NotNull(message = "业务类型不能为空")
    private String referenceType;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    private String mimeType;

    private Long fileSize;
}