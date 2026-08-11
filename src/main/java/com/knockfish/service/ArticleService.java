package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.article.ArticleCreateDTO;
import com.knockfish.dto.article.ArticleQueryDTO;
import com.knockfish.dto.article.ArticleUpdateDTO;
import com.knockfish.enums.ArticleStatus;
import com.knockfish.vo.article.*;

import java.util.List;


public interface ArticleService {
    PageInfo<ArticleWithTagListVO> getArticleWithTags(ArticleQueryDTO query, Integer pageNum, Integer pageSize);
    PageInfo<ArticleListVO> getArticleList(ArticleQueryDTO query, Integer pageNum, Integer pageSize);
    ArticleDetailVO getArticleWithTagsById(Long id);
    List<ArticleDraftVO> getDraftByUserId(Long userId);
    Integer getDraftCount(Long userId);
    ArticleViewVO getArticleById(Long id);
    Long createArticle(ArticleCreateDTO articleCreateDTO);
    void updateArticle(ArticleUpdateDTO articleUpdateDTO);
    void deleteArticle(Long id);
    List<ArticleArchiveVO> getArticleArchiveList();

    /**
     * 解绑文章中未使用的图片：后端从 content + cover 提取实际使用的图片 file key，
     * 与 file_reference 表中绑定该文章的记录做差集，将未使用的记录 reference_id 置 NULL。
     * 由前端在发布/编辑后离开页面时调用，后续由定时任务清理已解绑的孤儿文件。
     */
    void unbindUnusedFiles(Long articleId);
}
