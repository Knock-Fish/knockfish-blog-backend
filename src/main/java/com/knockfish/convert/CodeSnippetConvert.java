package com.knockfish.convert;

import com.knockfish.dto.code_snippet.CodeSnippetCreateDTO;
import com.knockfish.dto.code_snippet.CodeSnippetUpdateDTO;
import com.knockfish.entity.CodeSnippet;
import com.knockfish.vo.code_snippet.CodeSnippetDetailVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CodeSnippetConvert {
    CodeSnippet createToEntity(CodeSnippetCreateDTO createDTO);
    CodeSnippet updateToEntity(CodeSnippetUpdateDTO updateDTO);
    CodeSnippetDetailVO toDetailVO(CodeSnippet codeSnippet);
}