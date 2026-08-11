package com.knockfish.convert;

import com.knockfish.dto.article.ArticleCreateDTO;
import com.knockfish.dto.article.ArticleUpdateDTO;
import com.knockfish.entity.Article;
import com.knockfish.vo.article.ArticleDraftVO;
import com.knockfish.vo.article.ArticleListVO;
import com.knockfish.vo.article.ArticleVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticleConvert {
    // ==================== DTO -> Entity ====================
    /**
     * 新增文章DTO 转 文章Entity
     */
    Article createToEntity(ArticleCreateDTO createDTO);
    /**
     * 更新文章DTO 转 文章Entity
     */
    Article updateToEntity(ArticleUpdateDTO updateDTO);

    // ==================== Entity -> VO ====================
    /**
     * 文章Entity 转 文章vo
     */
    ArticleVO entityToVO(Article article);
    /**
     * 文章Entity列表 转 文章VO列表
     */
    List<ArticleListVO> listToVOList(List<Article> articleList);
    /**
     * 草稿文章Entity列表 转文章VO
     */
    List<ArticleDraftVO> listToDraftVOList(List<Article> articleList);
}
