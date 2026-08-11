package com.knockfish.dto.code_snippet;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeSnippetCreateDTO {
    private Long codeCategoryId;
    private String title;
    private String codeContent;
}
