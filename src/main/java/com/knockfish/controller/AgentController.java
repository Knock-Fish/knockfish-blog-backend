package com.knockfish.controller;

import com.knockfish.annotation.Log;
import com.knockfish.annotation.PublicApi;
import com.knockfish.common.Result;
import com.knockfish.service.AgentService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agent")
@PublicApi
public class AgentController {
    private final AgentService agentService;

    // 文章相关接口

    /**
     * 关键词搜索已发布文章
     * 标题/简介/内容模糊匹配
     */
    @GetMapping("/article/search")
    @Log("agent关键词搜索文章")
    public Result<List<ArticleWithTagListVO>> searchArticles(
            @RequestParam String keyword) {
        return Result.success(agentService.searchArticles(keyword));
    }

    /**
     * 根据文章ID获取完整详情
     */
    @GetMapping("/article/{id}")
    @Log("agent获取文章")
    public Result<ArticleViewVO> getArticleForAgent(@PathVariable Long id) {
        return Result.success(agentService.getArticleForAgent(id));
    }

    /**
     * 根据标签名查询文章
     */
    @GetMapping("/article/byTag")
    @Log("agent根据标签名查询文章")
    public Result<List<ArticleWithTagListVO>> getArticlesByTagName(
            @RequestParam String tagName) {
        return Result.success(agentService.getArticlesByTagName(tagName));
    }

    /**
     * 获取全部标签列表
     */
    @GetMapping("/tag")
    @Log("agent获取全部标签列表")
    public Result<List<TagVO>> getAllTags() {
        return Result.success(agentService.getAllTags());
    }

    // 代码片段接口

    /**
     * 代码片段关键词检索
     * 标题/代码内容模糊匹配
     */
    @GetMapping("/code/search")
    @Log("agent关键词搜索代码片段")
    public Result<List<CodeSnippetVO>> searchCodeSnippets(
            @RequestParam String keyword) {
        return Result.success(agentService.searchCodeSnippets(keyword));
    }

    /**
     * 根据语言分类查询代码
     */
    @GetMapping("/code/byCategory")
    @Log("agent根据分类查询代码片段")
    public Result<List<CodeSnippetVO>> getCodeSnippetsByCategory(
            @RequestParam Long categoryId) {
        return Result.success(agentService.getCodeSnippetsByCategory(categoryId));
    }

    /**
     * 获取所有代码语言分类
     */
    @GetMapping("/code/category")
    @Log("agent获取代码语言分类")
    public Result<List<CodeCategoryMenuVO>> getAllCodeCategories() {
        return Result.success(agentService.getAllCodeCategories());
    }

    // 笔记接口

    /**
     * 笔记关键词搜索
     * 标题/内容模糊匹配
     */
    @GetMapping("/note/search")
    @Log("agent关键词搜索笔记")
    public Result<List<NoteVO>> searchNotes(
            @RequestParam String keyword) {
        return Result.success(agentService.searchNotes(keyword));
    }

    /**
     * 笔记详情查询
     */
    @GetMapping("/note/{id}")
    @Log("agent获取笔记详情")
    public Result<NoteDetailVO> getNoteForAgent(@PathVariable Long id) {
        return Result.success(agentService.getNoteForAgent(id));
    }

    // ==================== 友链 & 站点导航 ====================

    /**
     * 展示状态友链列表（自动过滤 status=DISPLAY）
     */
    @GetMapping("/link/list")
    @Log("agent获取展示状态友链列表")
    public Result<List<LinkVO>> getDisplayLinks() {
        return Result.success(agentService.getDisplayLinks());
    }

    /**
     * 导航站点列表，可选参数 categoryId
     */
    @GetMapping("/site/list")
    @Log("agent获取导航站点列表")
    public Result<List<SiteWithCategoryVO>> getSiteListForAgent(
            @RequestParam(required = false) Long categoryId) {
        return Result.success(agentService.getSiteListForAgent(categoryId));
    }

    // 资源 & 基础信息

    /**
     * 业务附件查询
     */
    @GetMapping("/file/list")
    @Log("agent业务附件查询")
    public Result<List<FileReferenceVO>> getFilesByReference(
            @RequestParam String referenceType,
            @RequestParam Long referenceId) {
        return Result.success(agentService.getFilesByReference(referenceType, referenceId));
    }

    /**
     * 博客站点基础信息
     */
    @GetMapping("/site/info")
    @Log("agent获取博客站点基础信息")
    public Result<AgentSiteInfoVO> getSiteInfo() {
        return Result.success(agentService.getSiteInfo());
    }

    /**
     * 博主公开个人信息（屏蔽密码等敏感字段）
     */
    @GetMapping("/user/info")
    @Log("agent获取博主公开个人信息")
    public Result<UserFrontVO> getBloggerInfo() {
        return Result.success(agentService.getBloggerInfo());
    }
}
