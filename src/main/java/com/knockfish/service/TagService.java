package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.tag.TagCreateDTO;
import com.knockfish.dto.tag.TagQueryDTO;
import com.knockfish.dto.tag.TagUpdateDTO;
import com.knockfish.vo.article.ArticleVO;
import com.knockfish.vo.tag.TagVO;
import com.knockfish.vo.tag.TagWithArticleCountVO;

import java.util.List;

public interface TagService {
    PageInfo<TagVO> getTags(TagQueryDTO query, Integer pageNum, Integer pageSize);
    PageInfo<TagWithArticleCountVO> getTagWithArticleCount(Integer pageNum, Integer pageSize);
    List<ArticleVO> getArticlesByTagId(Long tagId);
    Long createTag(TagCreateDTO tagCreateDTO);
    void updateTag(TagUpdateDTO tagUpdateDTO);
    void deleteTag(Long id);
}
