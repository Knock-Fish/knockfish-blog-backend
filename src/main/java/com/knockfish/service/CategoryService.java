package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.category.CategoryCreateDTO;
import com.knockfish.dto.category.CategoryQueryDTO;
import com.knockfish.dto.category.CategoryUpdateDTO;
import com.knockfish.vo.category.CategoryOptionVO;
import com.knockfish.vo.category.CategoryWithSiteCountVO;
import com.knockfish.vo.category.CategoryWithSiteListVO;

import java.util.List;

public interface CategoryService {
    List<CategoryWithSiteListVO> getCategoryWithSiteList();
    PageInfo<CategoryWithSiteCountVO> getCategoryWithSiteCountList(CategoryQueryDTO query, Integer pageNum, Integer pageSize);
    List<CategoryOptionVO> getCategoryOption();
    void createCategory(CategoryCreateDTO categoryCreateDTO);
    void updateCategory(CategoryUpdateDTO categoryUpdateDTO);
    void deleteCategory(Long id);
}
