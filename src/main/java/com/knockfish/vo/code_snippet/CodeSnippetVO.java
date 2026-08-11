package com.knockfish.vo.code_snippet;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CodeSnippetVO {
    private Long codeSnippetId;
    private String title;
    private Long codeCategoryId;
    private String codeContent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
