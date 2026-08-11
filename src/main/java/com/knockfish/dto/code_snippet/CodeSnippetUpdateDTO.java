package com.knockfish.dto.code_snippet;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeSnippetUpdateDTO {
    private Long codeSnippetId;
    private String title;
    private Long codeCategoryId;
    private String codeContent;
}
