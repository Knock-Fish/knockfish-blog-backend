package com.knockfish.repository;

import com.knockfish.dto.article.ArticleQueryDTO;
import com.knockfish.entity.Article;
import com.knockfish.enums.ArticleStatus;
import com.knockfish.vo.article.ArticleArchiveVO;
import com.knockfish.vo.article.ArticleDetailVO;
import com.knockfish.vo.article.ArticleViewVO;
import com.knockfish.vo.article.ArticleWithTagListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleRepository {
    List<ArticleWithTagListVO> selectArticleWithTags(ArticleQueryDTO query);
    List<Article> selectAll(ArticleQueryDTO query);
    ArticleDetailVO selectArticleWithTagsById(Long id);
    Integer selectCount(Long userId);
    List<Article> selectDraftByUserId(Long userId);
    Article selectArticleById(Long id);
    ArticleViewVO selectArticleWithUserById(Long id);
    Long insert(Article article);
    void updateById(Article article);
    void deleteById(Long id);
    void clearUpdatedTime(Long articleId);
    void batchDelete(@Param("ids") List<Long> ids);
    List<ArticleArchiveVO> selectArticleArchiveList();
    Long selectArticleCount();
    List<Map<String, Object>> selectMonthlyArticleCount(Integer year);
    List<Map<String, Object>> selectWeeklyArticleCount(Integer year);
    List<Map<String, Object>> selectYearlyArticleCount();
    List<com.knockfish.vo.dashboard.ArticleLatestVO> selectLatestArticles(Integer limit);

    /**
     * Agent 关键词搜索已发布文章
     * 标题/简介/内容 模糊匹配
     */
    List<ArticleWithTagListVO> selectArticlesByKeyword(@Param("keyword") String keyword);

    /**
     * Agent 根据标签名查询文章
     */
    List<ArticleWithTagListVO> selectArticlesByTagName(@Param("tagName") String tagName);
}
