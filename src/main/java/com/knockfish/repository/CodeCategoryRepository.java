package com.knockfish.repository;

import com.knockfish.dto.code_category.CodeCategoryQueryDTO;
import com.knockfish.entity.CodeCategory;
import com.knockfish.vo.code_category.CodeCategoryMenuVO;
import com.knockfish.vo.code_category.CodeCategoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CodeCategoryRepository {
    List<CodeCategoryVO> selectList(CodeCategoryQueryDTO query);
    List<CodeCategoryMenuVO> selectMenuList();
    CodeCategory selectById(Long codeCategoryId);
    void insert(CodeCategory codeCategory);
    void updateById(CodeCategory codeCategory);
    void deleteById(Long codeCategoryId);
}