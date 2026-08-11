package com.knockfish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.convert.CategoryConvert;
import com.knockfish.dto.category.CategoryCreateDTO;
import com.knockfish.dto.category.CategoryQueryDTO;
import com.knockfish.dto.category.CategoryUpdateDTO;
import com.knockfish.entity.Category;
import com.knockfish.repository.CategoryRepository;
import com.knockfish.service.CategoryService;
import com.knockfish.vo.category.CategoryOptionVO;
import com.knockfish.vo.category.CategoryWithSiteCountVO;
import com.knockfish.vo.category.CategoryWithSiteListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryConvert categoryConvert;

    @Override
    public List<CategoryWithSiteListVO> getCategoryWithSiteList(){
        return categoryRepository.selectCategoryWithSites();
    }

    @Override
    public PageInfo<CategoryWithSiteCountVO> getCategoryWithSiteCountList(CategoryQueryDTO query, Integer pageNum, Integer pageSize){
        try(Page<CategoryWithSiteCountVO> page = PageHelper.startPage(pageNum, pageSize)){
            List<CategoryWithSiteCountVO> categoryWithSiteCount = categoryRepository.selectCategoryWithSiteCount(query);
            return PageInfo.of(categoryWithSiteCount);
        }
    }

    @Override
    public List<CategoryOptionVO> getCategoryOption(){
        List<Category> categoryEntity = categoryRepository.selectCategoryOption();
        return categoryConvert.optionToVOList(categoryEntity);
    }

    @Override
    public void createCategory(CategoryCreateDTO categoryCreateDTO){
        Category categoryEntity =  categoryConvert.createToEntity(categoryCreateDTO);
        categoryEntity.setCreateTime(LocalDateTime.now());
        categoryRepository.insert(categoryEntity);
    }
    @Override
    public void updateCategory(CategoryUpdateDTO categoryUpdateDTO){
        Category categoryEntity = categoryConvert.updateToEntity(categoryUpdateDTO);
        categoryRepository.updateById(categoryEntity);
    }
    @Override
    public void deleteCategory(Long id){
        categoryRepository.deleteById(id);
    }
}
