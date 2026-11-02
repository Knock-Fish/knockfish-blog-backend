package com.knockfish.controller.admin;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import org.springframework.security.access.prepost.PreAuthorize;
import com.knockfish.common.Result;
import com.knockfish.dto.article.ArticleCreateDTO;
import com.knockfish.dto.article.ArticleQueryDTO;
import com.knockfish.dto.article.ArticleUpdateDTO;
import com.knockfish.exception.CustomException;
import com.knockfish.security.CustomUserDetails;
import com.knockfish.service.ArticleService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.article.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/article")
@Tag(name = "文章管理", description = "文章相关接口")
public class ArticleAdminController {
    private final ArticleService articleService;

    @GetMapping("/list")
    @Operation(summary = "分页查询文章列表", description = "根据条件分页查询文章")
    @PreAuthorize("hasAuthority('PERMISSION_blog:article:manage')")
    @Log("查询文章列表")
    public Result<PageResultVO<ArticleListVO>> getArticleList(
            @Parameter(description = "查询条件") ArticleQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize){
        // 从SecurityContext获取当前用户ID
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new CustomException(401, "用户未登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        query.setUserId(userDetails.getUserId());
        PageInfo<ArticleListVO> listVO = articleService.getArticleList(query,pageNum,pageSize);
        return Result.success(PageConvertUtil.convert(listVO));
    }

    @GetMapping("/with-tag/{id}")
    @Operation(summary = "获取文章详情（含标签）", description = "根据ID获取文章详情，包含标签信息")
    @PreAuthorize("hasAuthority('PERMISSION_blog:article:manage')")
    @Log("获取文章详情(含标签)")
    public Result<ArticleDetailVO> getArticleWithTagsById(@Parameter(description = "文章ID") @PathVariable Long id){
        ArticleDetailVO articleDetailVO = articleService.getArticleWithTagsById(id);
        return Result.success(articleDetailVO);
    }

    @GetMapping("/draft")
    @Operation(summary = "获取草稿", description = "根据用户id获取草稿")
    @PreAuthorize("hasAuthority('PERMISSION_blog:article:manage')")
    @Log("获取10条草稿")
    public Result<List<ArticleDraftVO>> getDraftByUserId(){
        // 从SecurityContext获取当前用户ID
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new CustomException(401, "用户未登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        return Result.success(articleService.getDraftByUserId(userId));
    }

    @GetMapping("/draft-count")
    @Operation(summary = "获取草稿数量", description = "获取草稿文章的数量")
    @PreAuthorize("hasAuthority('PERMISSION_blog:article:manage')")
    @Log("获取草稿数量")
    public Result<Integer> getDraftCount(){
        // 从SecurityContext获取当前用户ID
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new CustomException(401, "用户未登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        return Result.success(articleService.getDraftCount(userId));
    }

    @PostMapping
    @Operation(summary = "新增文章", description = "创建新的文章")
    @PreAuthorize("hasAuthority('PERMISSION_blog:article:add')")
    @Log(value = "新增文章", recordResult = true)
    public Result<Long> createArticle(
            @Parameter(description = "文章创建信息") @RequestBody ArticleCreateDTO articleCreateDTO){
        // 从SecurityContext获取当前用户ID
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        articleCreateDTO.setUserId(userDetails.getUserId());
        return Result.success(articleService.createArticle(articleCreateDTO));
    }

    @PutMapping
    @Operation(summary = "更新文章", description = "更新文章信息")
    @PreAuthorize("hasAuthority('PERMISSION_blog:article:edit')")
    @Log("更新文章")
    public Result<Void> updateArticle(@Parameter(description = "文章更新信息") @RequestBody ArticleUpdateDTO articleUpdateDTO){
        articleService.updateArticle(articleUpdateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章", description = "根据ID删除文章")
    @PreAuthorize("hasAuthority('PERMISSION_blog:article:delete')")
    @Log("删除文章")
    public Result<Void> deleteArticle(@Parameter(description = "文章ID") @PathVariable Long id){
        articleService.deleteArticle(id);
        return Result.success();
    }

    @PostMapping("/unbindUnused/{id}")
    @Operation(summary = "解绑文章未使用的图片", description = "后端从 content + cover 提取实际使用的图片，与 file_reference 表绑定记录做差集，解绑未使用的图片（reference_id 置 NULL），由定时任务后续清理")
    @PreAuthorize("hasAuthority('PERMISSION_blog:article:edit')")
    @Log("解绑文章未使用图片")
    public Result<Void> unbindUnusedFiles(@Parameter(description = "文章ID") @PathVariable Long id){
        articleService.unbindUnusedFiles(id);
        return Result.success();
    }

}
