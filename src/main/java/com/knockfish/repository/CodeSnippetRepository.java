package com.knockfish.repository;

import com.knockfish.entity.CodeSnippet;
import com.knockfish.vo.code_snippet.CodeSnippetVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CodeSnippetRepository {
    List<CodeSnippetVO> selectListByCategoryId(Long codeCategoryId);
    CodeSnippet selectById(Long codeSnippetId);
    void insert(CodeSnippet codeSnippet);
    void updateById(CodeSnippet codeSnippet);
    void deleteById(Long codeSnippetId);
    void deleteByCategoryId(Long codeCategoryId);
    Long selectCodeSnippetCount();

    /**
     * Agent: 关键词搜索代码片段（标题/代码内容模糊匹配）
     */
    List<CodeSnippetVO> selectByKeyword(@Param("keyword") String keyword);
}
