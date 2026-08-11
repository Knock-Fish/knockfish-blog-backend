package com.knockfish.dto.code_category;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CodeCategoryUpdateDTO {
    private Long codeCategoryId;
    private String codeCategoryName;
    private int sort;
}
