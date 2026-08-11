package com.knockfish.convert;

import com.knockfish.dto.code_category.CodeCategoryCreateDTO;
import com.knockfish.dto.code_category.CodeCategoryUpdateDTO;
import com.knockfish.entity.CodeCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CodeCategoryConvert {
    CodeCategory createToEntity(CodeCategoryCreateDTO createDTO);
    CodeCategory updateToEntity(CodeCategoryUpdateDTO updateDTO);
}