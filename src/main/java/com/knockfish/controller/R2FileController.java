package com.knockfish.controller;

import com.knockfish.common.Result;
import com.knockfish.dto.r2_file.R2FileUploadDTO;
import com.knockfish.service.R2FileService;
import com.knockfish.vo.r2_file.R2FileInfoVO;
import com.knockfish.vo.r2_file.R2FileListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/r2-file")
@Tag(name = "R2文件管理", description = "Cloudflare R2文件存储相关接口")
public class R2FileController {
    private final R2FileService r2FileService;

    @PostMapping
    @Operation(summary = "上传文件", description = "上传文件到R2存储")
    public Result<Map<String, Object>> uploadR2fFile(R2FileUploadDTO uploadDTO){
        if(uploadDTO.getFile().isEmpty()){
            return Result.error(HttpStatus.BAD_REQUEST.value(), "上传的文件不能为空");
        }
        return Result.success(r2FileService.uploadR2File(uploadDTO));
    }

    @GetMapping
    @Operation(summary = "获取所有文件", description = "获取R2存储中的所有文件列表")
    public Result<List<String>> getAllR2Files(){
        List<String> files = r2FileService.getAllR2Files();
        return Result.success(files);
    }

    @GetMapping("/prefix")
    @Operation(summary = "按前缀查询文件", description = "根据前缀查询文件列表")
    public Result<List<R2FileListVO>> getAllR2FilesPrefix(@Parameter(description = "文件前缀") @RequestParam String prefix){
        List<R2FileListVO> files = r2FileService.getAllR2FilesByPrefix(prefix);
        return Result.success(files);
    }

    @GetMapping("/url")
    @Operation(summary = "获取文件URL", description = "获取文件的访问URL")
    public Result<String> getR2FileUrl(@Parameter(description = "文件key") @RequestParam String key){
        try{
            String url = r2FileService.getR2FileUrl(key);
            return Result.success(url);
        }catch (Exception e){
            return Result.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }

    @DeleteMapping
    @Operation(summary = "删除文件", description = "根据key删除文件")
    public Result<Boolean> deleteR2File(@Parameter(description = "文件key") @RequestParam String key) {
        boolean operation = r2FileService.deleteR2File(key);
        return Result.success(operation);
    }

    @DeleteMapping("/batch-delete")
    @Operation(summary = "批量删除文件", description = "批量删除多个文件")
    public Result<Boolean> batchDeleteObject(@Parameter(description = "文件key列表") @RequestBody List<String> keys) {
        boolean operation = r2FileService.batchDeleteR2File(keys);
        return Result.success(operation);
    }

    @GetMapping("/info")
    @Operation(summary = "获取文件信息", description = "获取文件的详细信息")
    public Result<R2FileInfoVO> getObjectInfo(@Parameter(description = "文件key") @RequestParam String key) {
        try {
            R2FileInfoVO r2FileInfoVO = r2FileService.getR2FileInfo(key);
            return Result.success(r2FileInfoVO);
        } catch (RuntimeException e) {
            return Result.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
    }
}
