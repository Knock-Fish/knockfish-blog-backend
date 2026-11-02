package com.knockfish.controller.admin;

import com.knockfish.annotation.Log;
import org.springframework.security.access.prepost.PreAuthorize;
import com.knockfish.dto.file_reference.FileReferenceCreateDTO;
import com.knockfish.dto.file_reference.FileReferenceQueryByRefDTO;
import com.knockfish.service.FileReferenceService;
import com.knockfish.common.Result;
import com.knockfish.vo.file_reference.FileReferenceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/file-reference")
@Tag(name = "文件引用管理", description = "文件引用相关接口")
public class FileReferenceAdminController {

    private final FileReferenceService fileReferenceService;

    @PostMapping
    @Operation(summary = "新增文件引用", description = "创建文件引用记录")
    @Log("新增文件引用")
    public Result<Long> createReference(@Parameter(description = "文件引用创建信息") @Valid @RequestBody FileReferenceCreateDTO createDTO) {
        return Result.success(fileReferenceService.createReference(createDTO));
    }

    @DeleteMapping("/delete-by-reference")
    @Operation(summary = "删除指定来源的所有文件引用", description = "根据业务类型和业务ID删除文件引用")
    @PreAuthorize("hasAuthority('PERMISSION_blog:file-reference:delete')")
    @Log("删除指定来源的文件引用")
    public Result<Void> deleteByReference(@Parameter(description = "业务类型：0=文章 1=笔记") @RequestParam String referenceType,
                                             @Parameter(description = "业务ID（文章ID/笔记ID）") @RequestParam Long referenceId) {
        log.info("删除文件引用: referenceType={}, referenceId={}", referenceType, referenceId);
        FileReferenceQueryByRefDTO queryDTO = new FileReferenceQueryByRefDTO();
        queryDTO.setReferenceType(referenceType);
        queryDTO.setReferenceId(referenceId);
        fileReferenceService.deleteByReference(queryDTO);
        return Result.success();
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "删除文件引用", description = "根据文件ID删除文件引用")
    @PreAuthorize("hasAuthority('PERMISSION_blog:file-reference:delete')")
    @Log("删除文件引用")
    public Result<Void> deleteById(@Parameter(description = "文件ID") @PathVariable Long fileId) {
        log.info("删除文件引用: fileId={}", fileId);
        fileReferenceService.deleteById(fileId);
        return Result.success();
    }

    @DeleteMapping("/batch-delete")
    @Operation(summary = "批量删除文件引用", description = "批量删除文件引用（定时清理用）")
    @PreAuthorize("hasAuthority('PERMISSION_blog:file-reference:delete')")
    @Log("批量删除文件引用")
    public Result<Void> batchDeleteByIds(@Parameter(description = "文件ID列表") @RequestBody List<Long> ids) {
        log.info("批量删除文件引用: count={}", ids != null ? ids.size() : 0);
        fileReferenceService.batchDeleteByIds(ids);
        return Result.success();
    }

    @GetMapping("/list-by-reference")
    @Operation(summary = "查询指定来源的文件引用", description = "根据业务类型和业务ID查询文件引用列表")
    @Log("查询指定来源的文件引用")
    public Result<List<FileReferenceVO>> selectByReference(@Parameter(description = "业务类型：0=文章 1=笔记") @RequestParam String referenceType,
                                                           @Parameter(description = "业务ID（文章ID/笔记ID）") @RequestParam Long referenceId) {
        log.info("查询文件引用: referenceType={}, referenceId={}", referenceType, referenceId);
        FileReferenceQueryByRefDTO queryDTO = new FileReferenceQueryByRefDTO();
        queryDTO.setReferenceType(referenceType);
        queryDTO.setReferenceId(referenceId);
        return Result.success(fileReferenceService.selectByReference(queryDTO));
    }

    @GetMapping("/referenced-paths")
    @Operation(summary = "查询所有被引用的文件路径", description = "查询所有被引用的文件路径（去重）")
    @PreAuthorize("hasAuthority('PERMISSION_blog:file-reference:list')")
    @Log("查询所有被引用的文件路径")
    public Result<List<String>> selectAllReferencedPaths() {
        log.info("查询所有被引用的文件路径");
        return Result.success(fileReferenceService.selectAllReferencedPaths());
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "查询文件引用详情", description = "根据文件ID查询文件引用详情")
    @Log("查询文件引用详情")
    public Result<FileReferenceVO> selectById(@Parameter(description = "文件ID") @PathVariable Long fileId) {
        log.info("查询文件引用: fileId={}", fileId);
        return Result.success(fileReferenceService.selectById(fileId));
    }

    @PreAuthorize("hasAuthority('PERMISSION_blog:file-reference:list')")
    @GetMapping("/all")
    @Operation(summary = "查询所有文件引用", description = "全量查询所有文件引用记录（定时任务用）")
    @Log("查询所有文件引用")
    public Result<List<FileReferenceVO>> selectAllNoPage() {
        log.info("查询所有文件引用");
        return Result.success(fileReferenceService.selectAllNoPage());
    }

    @PreAuthorize("hasAuthority('PERMISSION_blog:file-reference:delete')")
    @PostMapping("/cleanup")
    @Operation(summary = "手动触发文件清理", description = "手动执行文件资源清理任务，清理孤立文件")
    @Log("手动触发文件清理")
    public Result<String> cleanupOrphanFiles() {
        log.info("手动触发文件清理任务");
        String result = fileReferenceService.cleanupOrphanFiles();
        return Result.success(result);
    }
}