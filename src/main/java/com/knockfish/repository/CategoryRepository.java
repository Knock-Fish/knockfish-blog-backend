package com.knockfish.repository;

import com.knockfish.dto.category.CategoryQueryDTO;
import com.knockfish.entity.Category;
import com.knockfish.vo.category.CategoryWithSiteCountVO;
import com.knockfish.vo.category.CategoryWithSiteListVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryRepository {
    List<CategoryWithSiteListVO> selectCategoryWithSites();
    List<CategoryWithSiteCountVO> selectCategoryWithSiteCount(CategoryQueryDTO query);
    List<Category> selectCategoryOption();
    void insert(Category category);
    void updateById(Category category);
    void deleteById(Long id);

    /**
     * Agent: 统计分类总数
     */
    Long selectCategoryCount();
}
