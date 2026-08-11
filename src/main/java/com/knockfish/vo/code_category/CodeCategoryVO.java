package com.knockfish.vo.code_category;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CodeCategoryVO {
    private Long codeCategoryId;
    private String codeCategoryName;
    private int sort;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
