package com.knockfish.service;

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

import java.util.List;

public interface AgentService {
    /**
     * 获取文章详情
     * 仅返回已发布的文章，不返回草稿
     *
     * @param articleId 文章ID
     * @return 文章详情 VO，如果文章不存在或未发布则返回 null
     */
    ArticleViewVO getArticleForAgent(Long articleId);

    // ============ 文章相关 ============

    /**
     * 关键词搜索已发布文章
     * 标题/简介/内容 模糊匹配
     *
     * @param keyword 关键词
     * @return 文章列表（含标签）
     */
    List<ArticleWithTagListVO> searchArticles(String keyword);

    /**
     * 根据标签名查询文章
     *
     * @param tagName 标签名称
     * @return 文章列表（含标签）
     */
    List<ArticleWithTagListVO> getArticlesByTagName(String tagName);

    /**
     * 获取全部标签列表
     *
     * @return 标签列表
     */
    List<TagVO> getAllTags();

    // ============ 代码片段相关 ============

    /**
     * 代码片段关键词检索
     * 标题/代码内容 模糊匹配
     *
     * @param keyword 关键词
     * @return 代码片段列表
     */
    List<CodeSnippetVO> searchCodeSnippets(String keyword);

    /**
     * 根据语言分类查询代码
     *
     * @param categoryId 分类ID
     * @return 代码片段列表
     */
    List<CodeSnippetVO> getCodeSnippetsByCategory(Long categoryId);

    /**
     * 获取所有代码语言分类
     *
     * @return 代码分类菜单列表
     */
    List<CodeCategoryMenuVO> getAllCodeCategories();

    // ============ 笔记相关 ============

    /**
     * 笔记关键词搜索
     * 标题/内容 模糊匹配
     *
     * @param keyword 关键词
     * @return 笔记列表
     */
    List<NoteVO> searchNotes(String keyword);

    /**
     * 笔记详情查询
     *
     * @param id 笔记ID
     * @return 笔记详情
     */
    NoteDetailVO getNoteForAgent(Long id);

    // ============ 友链 & 站点导航 ============

    /**
     * 展示状态友链列表（自动过滤 status=DISPLAY）
     *
     * @return 友链列表
     */
    List<LinkVO> getDisplayLinks();

    /**
     * 导航站点列表，可选参数 categoryId
     *
     * @param categoryId 分类ID（可选）
     * @return 站点列表（含分类）
     */
    List<SiteWithCategoryVO> getSiteListForAgent(Long categoryId);

    // ============ 资源 & 基础信息 ============

    /**
     * 业务附件查询
     *
     * @param referenceType 关联类型（article/note等）
     * @param referenceId   关联ID
     * @return 附件列表
     */
    List<FileReferenceVO> getFilesByReference(String referenceType, Long referenceId);

    /**
     * 博客站点基础信息
     *
     * @return 站点基础信息
     */
    AgentSiteInfoVO getSiteInfo();

    /**
     * 博主公开个人信息（屏蔽密码等敏感字段）
     * 默认返回用户ID=1的博主信息
     *
     * @return 博主公开信息
     */
    UserFrontVO getBloggerInfo();
}
