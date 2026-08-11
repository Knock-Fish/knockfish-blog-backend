package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.code_snippet.CodeSnippetCreateDTO;
import com.knockfish.dto.code_snippet.CodeSnippetUpdateDTO;
import com.knockfish.vo.code_snippet.CodeSnippetDetailVO;
import com.knockfish.vo.code_snippet.CodeSnippetVO;

import java.util.List;

public interface CodeSnippetService {
    PageInfo<CodeSnippetVO> getCodeSnippetListByCategoryId(Long codeCategoryId, Integer pageNum, Integer pageSize);
    CodeSnippetDetailVO getCodeSnippetById(Long codeSnippetId);
    void createCodeSnippet(CodeSnippetCreateDTO createDTO);
    void updateCodeSnippet(CodeSnippetUpdateDTO updateDTO);
    void deleteCodeSnippet(Long codeSnippetId);
}