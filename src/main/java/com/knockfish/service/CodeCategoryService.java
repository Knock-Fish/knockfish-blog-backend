package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.code_category.CodeCategoryCreateDTO;
import com.knockfish.dto.code_category.CodeCategoryQueryDTO;
import com.knockfish.dto.code_category.CodeCategoryUpdateDTO;
import com.knockfish.vo.code_category.CodeCategoryMenuVO;
import com.knockfish.vo.code_category.CodeCategoryVO;

import java.util.List;

public interface CodeCategoryService {
    PageInfo<CodeCategoryVO> getCodeCategoryList(CodeCategoryQueryDTO query, Integer pageNum, Integer pageSize);
    List<CodeCategoryMenuVO> getMenuList();
    void createCodeCategory(CodeCategoryCreateDTO createDTO);
    void updateCodeCategory(CodeCategoryUpdateDTO updateDTO);
    void deleteCodeCategory(Long codeCategoryId);
}