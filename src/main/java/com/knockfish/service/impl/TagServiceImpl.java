package com.knockfish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.convert.TagConvert;
import com.knockfish.dto.tag.TagCreateDTO;
import com.knockfish.dto.tag.TagQueryDTO;
import com.knockfish.dto.tag.TagUpdateDTO;
import com.knockfish.entity.Tag;
import com.knockfish.repository.TagRepository;
import com.knockfish.service.TagService;
import com.knockfish.vo.article.ArticleVO;
import com.knockfish.vo.tag.TagVO;
import com.knockfish.vo.tag.TagWithArticleCountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final TagConvert tagConvert;
    @Override
    public PageInfo<TagVO> getTags(TagQueryDTO query, Integer pageNum, Integer pageSize) {
        try (Page<TagVO> page = PageHelper.startPage(pageNum, pageSize)) {
            List<Tag> tagListEntity = tagRepository.selectAll(query);
            List<TagVO> tagListVO = tagConvert.listToVOList(tagListEntity);
            PageInfo<TagVO> pageInfo = PageInfo.of(page);
            pageInfo.setList(tagListVO);
            return pageInfo;
        }
    }
    @Override
    public PageInfo<TagWithArticleCountVO> getTagWithArticleCount(Integer pageNum, Integer pageSize){
        try(Page<TagWithArticleCountVO> page = PageHelper.startPage(pageNum, pageSize)) {
            List<TagWithArticleCountVO> tagListVO = tagRepository.selectTagWithArticleCount();
            return PageInfo.of(tagListVO);
        }
    }

    @Override
    public List<ArticleVO> getArticlesByTagId(Long tagId) {
        return tagRepository.selectArticlesByTagId(tagId);
    }

    @Override
    public Long createTag(TagCreateDTO tagCreateDTO) {
        Tag tagEntity = tagConvert.createToEntity(tagCreateDTO);
        tagEntity.setCreateTime(LocalDateTime.now());
        tagRepository.insert(tagEntity);
        return tagEntity.getTagId();
    }
    @Override
    public void updateTag(TagUpdateDTO tagUpdateDTO) {
        Tag tagEntity = tagConvert.updateToEntity(tagUpdateDTO);
        tagRepository.updateById(tagEntity);
    }

    @Override
    public void deleteTag(Long id){
        tagRepository.deleteById(id);
    }
}
