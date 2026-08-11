package com.knockfish.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.convert.ArticleConvert;
import com.knockfish.dto.article.ArticleCreateDTO;
import com.knockfish.dto.article.ArticleQueryDTO;
import com.knockfish.dto.article.ArticleUpdateDTO;
import com.knockfish.dto.file_reference.FileReferenceQueryByRefDTO;
import com.knockfish.entity.Article;
import com.knockfish.enums.ArticleStatus;
import com.knockfish.exception.CustomException;
import com.knockfish.repository.ArticleRepository;
import com.knockfish.repository.ArticleTagRepository;
import com.knockfish.service.ArticleService;
import com.knockfish.service.FileReferenceService;
import com.knockfish.service.R2FileService;
import com.knockfish.vo.article.*;
import com.knockfish.vo.file_reference.FileReferenceVO;
import com.knockfish.utils.MarkdownImageExtractor;
import com.knockfish.utils.UrlUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleTagRepository articleTagRepository;
    private final ArticleConvert articleConvert;
    private final R2FileService r2FileService;
    private final FileReferenceService fileReferenceService;

    @Override
    public PageInfo<ArticleWithTagListVO> getArticleWithTags(ArticleQueryDTO query, Integer pageNum, Integer pageSize){
        try(Page<Object> page = PageHelper.startPage(pageNum, pageSize)){
            return PageInfo.of(articleRepository.selectArticleWithTags(query));
        }
    }
    @Override
    public PageInfo<ArticleListVO> getArticleList(ArticleQueryDTO query, Integer pageNum, Integer pageSize ){
        try(Page<ArticleListVO> page = PageHelper.startPage(pageNum, pageSize)){
            List<Article> articleListEntity = articleRepository.selectAll(query);
            List<ArticleListVO> articleListVO = articleConvert.listToVOList(articleListEntity);
            // 组装分页参数
            PageInfo<ArticleListVO> pageInfo = PageInfo.of(page);
            pageInfo.setList(articleListVO);
            return pageInfo;
        }
    }

    public List<ArticleDraftVO> getDraftByUserId(Long userId){
        List<Article> articleList = articleRepository.selectDraftByUserId(userId);
        return articleConvert.listToDraftVOList(articleList);
    }

    @Override
    public Integer getDraftCount(Long userId){
        return articleRepository.selectCount(userId);
    }

    @Override
    public ArticleDetailVO getArticleWithTagsById(Long id){
        return articleRepository.selectArticleWithTagsById(id);
    }

    public ArticleViewVO getArticleById(Long id){
        return articleRepository.selectArticleWithUserById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createArticle(ArticleCreateDTO articleCreateDTO){
        Article articleEntity = articleConvert.createToEntity(articleCreateDTO);
        // 默认设为草稿（打开即创建场景，status 可能为 null）
        if (articleEntity.getStatus() == null) {
            articleEntity.setStatus(ArticleStatus.DRAFT);
        }
        // 发布时校验必填字段
        if (articleEntity.getStatus() == ArticleStatus.PUBLISH) {
            validatePublishFields(articleEntity.getTitle(), articleEntity.getDescription(), articleEntity.getContent());
            articleEntity.setPublishTime(LocalDateTime.now());
        } else {
            // 草稿需要更新时间
            articleEntity.setUpdatedTime(LocalDateTime.now());
        }
        articleRepository.insert(articleEntity);
        Long articleId = articleEntity.getArticleId();
        if(!(articleCreateDTO.getTags() == null || articleCreateDTO.getTags().isEmpty())){
            articleTagRepository.batchInsert(
                    articleEntity.getArticleId(),
                    articleCreateDTO.getTags()
            );
        }
        log.info("文章创建成功: articleId={}, title={}", articleId, articleEntity.getTitle());
        return articleId;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(ArticleUpdateDTO articleUpdateDTO){
        Article articleEntity = articleConvert.updateToEntity(articleUpdateDTO);
        // 获取旧文章，判断状态变化
        Article oldArticle = articleRepository.selectArticleById(articleEntity.getArticleId());
        // 发布时校验必填字段
        if (articleEntity.getStatus() == ArticleStatus.PUBLISH) {
            validatePublishFields(articleEntity.getTitle(), articleEntity.getDescription(), articleEntity.getContent());
        }
        // 草稿 → 第一次发布，不更新 update_time
        boolean isDraftToPublish =
                oldArticle.getStatus() == ArticleStatus.DRAFT &&
                        articleEntity.getStatus() == ArticleStatus.PUBLISH;

        // 只有 不是 草稿→首次发布 时，才更新 update_time
        if (!isDraftToPublish) {
            articleEntity.setUpdatedTime(LocalDateTime.now());
        }
        // 草稿 → 首次发布：只设置发布时间
        if (isDraftToPublish) {
            articleEntity.setPublishTime(LocalDateTime.now());
        }
        articleRepository.updateById(articleEntity);
        if (isDraftToPublish) {
            articleRepository.clearUpdatedTime(articleEntity.getArticleId());
        }
        if(!(articleUpdateDTO.getTags() == null || articleUpdateDTO.getTags().isEmpty())){
            articleTagRepository.deleteByArticleId(articleUpdateDTO.getArticleId());
            articleTagRepository.batchInsert(
                    articleUpdateDTO.getArticleId(),
                    articleUpdateDTO.getTags()
            );
        }
        log.info("文章更新成功: articleId={}", articleUpdateDTO.getArticleId());
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id){
        if(id == null){
            throw new CustomException(404, "文章不存在");
        }
        // 先查出要删除的文章（获取封面、内容图片）
        Article article = articleRepository.selectArticleById(id);
        if (article == null) {
            log.warn("删除文章失败: 文章不存在, id={}", id);
            return;
        }

        List<String> keyList = new ArrayList<>();

        // 提取封面 + 内容图片（content 为 Markdown 文本，用 MarkdownImageExtractor 提取）
        keyList.addAll(extractUsedFileKeys(article));

        // 调用文件服务批量删除图片
        if (!keyList.isEmpty()) {
            log.debug("删除文章关联文件: articleId={}, fileCount={}", id, keyList.size());
            r2FileService.batchDeleteR2File(keyList);
        }

        // 删除file_reference记录
        FileReferenceQueryByRefDTO queryDTO = new FileReferenceQueryByRefDTO();
        queryDTO.setReferenceType("article");
        queryDTO.setReferenceId(id);
        fileReferenceService.deleteByReference(queryDTO);

        articleRepository.deleteById(id);
        articleTagRepository.deleteByArticleId(id);
        log.info("文章删除成功: articleId={}, title={}", id, article.getTitle());
    }

    @Override
    public List<ArticleArchiveVO> getArticleArchiveList() {
        return articleRepository.selectArticleArchiveList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindUnusedFiles(Long articleId) {
        if (articleId == null) {
            return;
        }
        Article article = articleRepository.selectArticleById(articleId);
        if (article == null) {
            log.warn("解绑未使用图片失败: 文章不存在, articleId={}", articleId);
            return;
        }

        // 1. 后端从 content + cover 提取实际使用的 file key 集合
        Set<String> usedKeys = extractUsedFileKeys(article);

        // 2. 查询 file_reference 表中绑定该文章的所有记录
        FileReferenceQueryByRefDTO queryDTO = new FileReferenceQueryByRefDTO();
        queryDTO.setReferenceType("article");
        queryDTO.setReferenceId(articleId);
        List<FileReferenceVO> boundFiles = fileReferenceService.selectByReference(queryDTO);

        // 3. 差集：绑定记录中 file_path 不在 usedKeys 中的 → 解绑
        List<Long> unbindIds = boundFiles.stream()
                .filter(f -> StrUtil.isNotBlank(f.getFilePath()) && !usedKeys.contains(f.getFilePath()))
                .map(FileReferenceVO::getFileId)
                .collect(Collectors.toList());

        if (unbindIds.isEmpty()) {
            log.info("文章无未使用图片需解绑: articleId={}", articleId);
            return;
        }

        fileReferenceService.unbindByIds(unbindIds);
        log.info("文章未使用图片解绑完成: articleId={}, 解绑数量={}", articleId, unbindIds.size());
    }

    /**
     * 提取文章实际使用的所有图片 file key（封面 + content 中的图片）
     * content 为 Markdown 文本，使用 MarkdownImageExtractor 提取
     */
    private Set<String> extractUsedFileKeys(Article article) {
        Set<String> usedKeys = new HashSet<>();

        // 封面
        if (StrUtil.isNotBlank(article.getCover())) {
            String coverKey = UrlUtil.extractFileKeyFromUrl(article.getCover());
            if (StrUtil.isNotBlank(coverKey)) {
                usedKeys.add(coverKey);
            }
        }

        // 内容图片（Markdown 文本）
        if (StrUtil.isNotBlank(article.getContent())) {
            usedKeys.addAll(MarkdownImageExtractor.extractFileKeys(article.getContent()));
        }
        return usedKeys;
    }

    /**
     * 发布时校验必填字段
     */
    private void validatePublishFields(String title, String description, String content) {
        if (StrUtil.isBlank(title)) {
            throw new CustomException(400, "发布文章时标题不能为空");
        }
        if (StrUtil.isBlank(description)) {
            throw new CustomException(400, "发布文章时简介不能为空");
        }
        if (StrUtil.isBlank(content)) {
            throw new CustomException(400, "发布文章时内容不能为空");
        }
    }
}
