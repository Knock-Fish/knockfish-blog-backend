package com.knockfish.dto.r2_file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class R2FileUploadDTO {
    private MultipartFile file;
    private String type;
    private Long userId;
    /**
     * 关联ID（如文章ID、笔记ID），上传时直接建立关联，避免后续补关联
     */
    private Long referenceId;
}
