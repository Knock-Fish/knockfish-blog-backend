package com.knockfish.vo.tag;

import com.knockfish.vo.article.ArticleVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "标签（含文章列表）VO")
public class TagWithArticleListVO {
    @Schema(description = "标签信息")
    private TagVO tag;
    @Schema(description = "关联文章列表")
    private List<ArticleVO> articles;
}
