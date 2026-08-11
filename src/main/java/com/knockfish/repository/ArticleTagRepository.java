package com.knockfish.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleTagRepository {
    void batchInsert(@Param("articleId") Long articleId, @Param("tagIds") List<Long> tagIds);
    void deleteByArticleId(@Param("articleId") Long articleId);
}
