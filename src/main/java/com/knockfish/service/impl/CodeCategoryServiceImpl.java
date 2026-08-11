package com.knockfish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.convert.CodeCategoryConvert;
import com.knockfish.dto.code_category.CodeCategoryCreateDTO;
import com.knockfish.dto.code_category.CodeCategoryQueryDTO;
import com.knockfish.dto.code_category.CodeCategoryUpdateDTO;
import com.knockfish.entity.CodeCategory;
import com.knockfish.repository.CodeCategoryRepository;
import com.knockfish.service.CodeCategoryService;
import com.knockfish.vo.code_category.CodeCategoryMenuVO;
import com.knockfish.vo.code_category.CodeCategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeCategoryServiceImpl implements CodeCategoryService {
    private final CodeCategoryRepository codeCategoryRepository;
    private final CodeCategoryConvert codeCategoryConvert;

    @Override
    public PageInfo<CodeCategoryVO> getCodeCategoryList(CodeCategoryQueryDTO query, Integer pageNum, Integer pageSize) {
        try (Page<CodeCategoryVO> page = PageHelper.startPage(pageNum, pageSize)) {
            List<CodeCategoryVO> list = codeCategoryRepository.selectList(query);
            return PageInfo.of(list);
        }
    }

    @Override
    public List<CodeCategoryMenuVO> getMenuList() {
        return codeCategoryRepository.selectMenuList();
    }

    @Override
    public void createCodeCategory(CodeCategoryCreateDTO createDTO) {
        CodeCategory entity = codeCategoryConvert.createToEntity(createDTO);
        entity.setCreateTime(LocalDateTime.now());
        codeCategoryRepository.insert(entity);
    }

    @Override
    public void updateCodeCategory(CodeCategoryUpdateDTO updateDTO) {
        CodeCategory entity = codeCategoryConvert.updateToEntity(updateDTO);
        codeCategoryRepository.updateById(entity);
    }

    @Override
    public void deleteCodeCategory(Long codeCategoryId) {
        codeCategoryRepository.deleteById(codeCategoryId);
    }
}