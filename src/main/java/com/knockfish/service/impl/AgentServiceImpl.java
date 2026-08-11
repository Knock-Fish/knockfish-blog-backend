package com.knockfish.service.impl;

import com.knockfish.convert.LinkConvert;
import com.knockfish.convert.NoteConvert;
import com.knockfish.convert.TagConvert;
import com.knockfish.dto.file_reference.FileReferenceQueryByRefDTO;
import com.knockfish.entity.Note;
import com.knockfish.entity.Tag;
import com.knockfish.repository.*;
import com.knockfish.service.AgentService;
import com.knockfish.service.ArticleService;
import com.knockfish.service.CodeCategoryService;
import com.knockfish.service.FileReferenceService;
import com.knockfish.service.LinkService;
import com.knockfish.service.NoteService;
import com.knockfish.service.UserService;
import com.knockfish.vo.agent.AgentSiteInfoVO;
import com.knockfish.vo.article.ArticleViewVO;
import com.knockfish.vo.article.ArticleWithTagListVO;
import com.knockfish.vo.code_category.CodeCategoryMenuVO;
import com.knockfish.vo.code_snippet.CodeSnippetVO;
import com.knockfish.vo.file_reference.FileReferenceVO;
import com.knockfish.vo.link.LinkVO;
import com.knockfish.vo.note.NoteDetailVO;
import com.knockfish.vo.note.NoteVO;
import com.knockfish.vo.site.SiteWithCategoryVO;
import com.knockfish.vo.tag.TagVO;
import com.knockfish.vo.user.UserFrontVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {
    private final ArticleService articleService;
    private final NoteService noteService;
    private final LinkService linkService;
    private final UserService userService;
    private final CodeCategoryService codeCategoryService;
    private final FileReferenceService fileReferenceService;

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final NoteRepository noteRepository;
    private final CodeSnippetRepository codeSnippetRepository;
    private final SiteRepository siteRepository;
    private final CategoryRepository categoryRepository;
    private final LinkRepository linkRepository;

    private final TagConvert tagConvert;
    private final NoteConvert noteConvert;
    private final LinkConvert linkConvert;

    // ==================== 已有方法 ====================

    @Override
    @Transactional(readOnly = true)
    public ArticleViewVO getArticleForAgent(Long articleId) {
        log.info("Agent 请求获取文章: articleId={}", articleId);
        return articleService.getArticleById(articleId);
    }

    // ==================== 文章相关 ====================

    @Override
    @Transactional(readOnly = true)
    public List<ArticleWithTagListVO> searchArticles(String keyword) {
        log.info("Agent 关键词搜索文章: keyword={}", keyword);
        return articleRepository.selectArticlesByKeyword(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleWithTagListVO> getArticlesByTagName(String tagName) {
        log.info("Agent 根据标签名查询文章: tagName={}", tagName);
        return articleRepository.selectArticlesByTagName(tagName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagVO> getAllTags() {
        log.info("Agent 获取全部标签列表");
        List<Tag> tagList = tagRepository.selectAllNoPage();
        return tagConvert.listToVOList(tagList);
    }

    // ==================== 代码片段相关 ====================

    @Override
    @Transactional(readOnly = true)
    public List<CodeSnippetVO> searchCodeSnippets(String keyword) {
        log.info("Agent 关键词搜索代码片段: keyword={}", keyword);
        return codeSnippetRepository.selectByKeyword(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CodeSnippetVO> getCodeSnippetsByCategory(Long categoryId) {
        log.info("Agent 根据分类ID查询代码片段: categoryId={}", categoryId);
        return codeSnippetRepository.selectListByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CodeCategoryMenuVO> getAllCodeCategories() {
        log.info("Agent 获取所有代码语言分类");
        return codeCategoryService.getMenuList();
    }

    // ==================== 笔记相关 ====================

    @Override
    @Transactional(readOnly = true)
    public List<NoteVO> searchNotes(String keyword) {
        log.info("Agent 关键词搜索笔记: keyword={}", keyword);
        List<Note> noteList = noteRepository.selectByKeyword(keyword);
        return noteConvert.listToVOlist(noteList);
    }

    @Override
    @Transactional(readOnly = true)
    public NoteDetailVO getNoteForAgent(Long id) {
        log.info("Agent 获取笔记详情: noteId={}", id);
        return noteService.getNoteById(id);
    }

    // ==================== 友链 & 站点导航 ====================

    @Override
    @Transactional(readOnly = true)
    public List<LinkVO> getDisplayLinks() {
        log.info("Agent 获取展示状态友链列表");
        return linkService.getLinkList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SiteWithCategoryVO> getSiteListForAgent(Long categoryId) {
        log.info("Agent 获取导航站点列表: categoryId={}", categoryId);
        return siteRepository.selectSiteListForAgent(categoryId);
    }

    // ==================== 资源 & 基础信息 ====================

    @Override
    @Transactional(readOnly = true)
    public List<FileReferenceVO> getFilesByReference(String referenceType, Long referenceId) {
        log.info("Agent 业务附件查询: referenceType={}, referenceId={}", referenceType, referenceId);
        FileReferenceQueryByRefDTO queryDTO = new FileReferenceQueryByRefDTO();
        queryDTO.setReferenceType(referenceType);
        queryDTO.setReferenceId(referenceId);
        return fileReferenceService.selectByReference(queryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AgentSiteInfoVO getSiteInfo() {
        log.info("Agent 获取博客站点基础信息");
        AgentSiteInfoVO info = new AgentSiteInfoVO();
        info.setSiteName("Knockfish Blog");
        info.setSiteDescription("一个分享技术与生活的博客");
        info.setArticleCount(articleRepository.selectArticleCount());
        info.setTagCount(tagRepository.selectTagCount());
        info.setCategoryCount(categoryRepository.selectCategoryCount());
        info.setNoteCount(noteRepository.selectNoteCount());
        info.setCodeSnippetCount(codeSnippetRepository.selectCodeSnippetCount());
        info.setLinkCount(linkRepository.selectLinkCount());
        info.setSiteCount(siteRepository.selectSiteCount());
        return info;
    }

    @Override
    @Transactional(readOnly = true)
    public UserFrontVO getBloggerInfo() {
        log.info("Agent 获取博主公开个人信息");
        // 默认返回用户ID=1的博主信息
        return userService.getUserFrontById(1L);
    }
}
