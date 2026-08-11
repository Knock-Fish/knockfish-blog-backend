package com.knockfish.repository;

import com.knockfish.dto.tag.TagQueryDTO;
import com.knockfish.entity.Tag;
import com.knockfish.vo.article.ArticleVO;
import com.knockfish.vo.tag.TagWithArticleCountVO;
import com.knockfish.vo.tag.TagWithArticleListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagRepository {
    List<Tag> selectAll(TagQueryDTO query);

    List<TagWithArticleListVO> selectTagWithArticles();

    List<TagWithArticleCountVO> selectTagWithArticleCount();

    List<ArticleVO> selectArticlesByTagId(@Param("tagId") Long tagId);

    Long insert(Tag tag);

    void updateById(Tag tag);

    void deleteById(Long id);

    Long selectTagCount();

    /**
     * Agent: 获取全部标签列表（不分页）
     */
    List<Tag> selectAllNoPage();
}
