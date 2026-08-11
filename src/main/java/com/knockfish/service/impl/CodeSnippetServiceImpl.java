package com.knockfish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.convert.CodeSnippetConvert;
import com.knockfish.dto.code_snippet.CodeSnippetCreateDTO;
import com.knockfish.dto.code_snippet.CodeSnippetUpdateDTO;
import com.knockfish.entity.CodeSnippet;
import com.knockfish.repository.CodeSnippetRepository;
import com.knockfish.service.CodeSnippetService;
import com.knockfish.vo.code_snippet.CodeSnippetDetailVO;
import com.knockfish.vo.code_snippet.CodeSnippetVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeSnippetServiceImpl implements CodeSnippetService {
    private final CodeSnippetRepository codeSnippetRepository;
    private final CodeSnippetConvert codeSnippetConvert;

    @Override
    public PageInfo<CodeSnippetVO> getCodeSnippetListByCategoryId(Long codeCategoryId, Integer pageNum, Integer pageSize) {
        try (Page<CodeSnippetVO> page = PageHelper.startPage(pageNum, pageSize)) {
            List<CodeSnippetVO> list = codeSnippetRepository.selectListByCategoryId(codeCategoryId);
            PageInfo<CodeSnippetVO> pageInfo = PageInfo.of(page);
            pageInfo.setList(list);
            return pageInfo;
        }
    }

    @Override
    public CodeSnippetDetailVO getCodeSnippetById(Long codeSnippetId) {
        CodeSnippet codeSnippet = codeSnippetRepository.selectById(codeSnippetId);
        return codeSnippetConvert.toDetailVO(codeSnippet);
    }

    @Override
    public void createCodeSnippet(CodeSnippetCreateDTO createDTO) {
        CodeSnippet entity = codeSnippetConvert.createToEntity(createDTO);
        entity.setCreateTime(LocalDateTime.now());
        codeSnippetRepository.insert(entity);
    }

    @Override
    public void updateCodeSnippet(CodeSnippetUpdateDTO updateDTO) {
        CodeSnippet entity = codeSnippetConvert.updateToEntity(updateDTO);
        codeSnippetRepository.updateById(entity);
    }

    @Override
    public void deleteCodeSnippet(Long codeSnippetId) {
        codeSnippetRepository.deleteById(codeSnippetId);
    }
}