package com.knockfish.convert;


import com.knockfish.dto.category.CategoryCreateDTO;
import com.knockfish.dto.category.CategoryUpdateDTO;
import com.knockfish.entity.Category;
import com.knockfish.vo.category.CategoryOptionVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryConvert {
    // ==================== DTO -> Entity ====================
    /**
     * 新增类别DTO 转 类别Entity
     */
    Category createToEntity(CategoryCreateDTO createDTO);
    /**
     * 更新类别DTO 转 类别Entity
     */
    Category updateToEntity(CategoryUpdateDTO updateDTO);

    // ==================== Entity -> VO ====================

    List<CategoryOptionVO> optionToVOList(List<Category> categoryList);
}
