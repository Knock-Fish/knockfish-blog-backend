package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CodeSnippet {
    private Long codeSnippetId;
    private Long codeCategoryId;
    private String title;
    private String codeContent;
    private LocalDateTime createTime;
}
