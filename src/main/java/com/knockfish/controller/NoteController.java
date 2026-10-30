package com.knockfish.controller;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.annotation.PublicApi;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.note.NoteCreateDTO;
import com.knockfish.dto.note.NoteQueryDTO;
import com.knockfish.dto.note.NoteStatusUpdateDTO;
import com.knockfish.dto.note.NoteUpdateDTO;
import com.knockfish.service.NoteService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.note.NoteDetailVO;
import com.knockfish.vo.note.NoteMenuVO;
import com.knockfish.vo.note.NoteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
@Tag(name = "笔记管理", description = "笔记相关接口")
public class NoteController {
    private final NoteService noteService;

    @PublicApi
    @GetMapping("/menu")
    @Operation(summary = "获取笔记菜单", description = "获取笔记菜单列表")
    @Log("获取笔记菜单")
    public Result<List<NoteMenuVO>> getNoteMenuList() {
        return Result.success(noteService.getNoteMenuList());
    }

    @GetMapping
    @Operation(summary = "获取笔记列表（管理端）", description = "获取所有笔记列表，含隐藏笔记，需登录")
    @Log("获取笔记列表")
    public Result<PageResultVO<NoteVO>> getNoteList(@Parameter(description = "查询条件") NoteQueryDTO query,
                                                      @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
                                                      @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<NoteVO> noteList = noteService.getNoteList(query, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(noteList));
    }

    @PublicApi
    @GetMapping("/display")
    @Operation(summary = "获取笔记列表（前台）", description = "仅返回显示状态的笔记")
    @Log("获取展示笔记列表")
    public Result<PageResultVO<NoteVO>> getDisplayNoteList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<NoteVO> noteList = noteService.getDisplayNoteList(pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(noteList));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取笔记详情（管理端）", description = "根据ID获取笔记详情，含隐藏笔记，需登录")
    @Log("获取笔记详情")
    public Result<NoteDetailVO> getNoteById(@Parameter(description = "笔记ID") @PathVariable Long id) {
        return Result.success(noteService.getNoteById(id));
    }

    @PublicApi
    @GetMapping("/display/{id}")
    @Operation(summary = "获取笔记详情（前台）", description = "根据ID获取显示状态的笔记详情，隐藏笔记返回不存在")
    @Log("获取展示笔记详情")
    public Result<NoteDetailVO> getDisplayNoteById(@Parameter(description = "笔记ID") @PathVariable Long id) {
        return Result.success(noteService.getDisplayNoteById(id));
    }

    @PutMapping("/status")
    @Operation(summary = "更新笔记展示状态", description = "切换笔记显示/隐藏，不影响笔记正文")
    @RequiresPermission("blog:note:edit")
    @Log("更新笔记展示状态")
    public Result<Void> updateNoteStatus(@Parameter(description = "笔记状态信息") @Valid @RequestBody NoteStatusUpdateDTO statusUpdateDTO) {
        noteService.updateNoteStatus(statusUpdateDTO);
        return Result.success();
    }

    @PostMapping
    @Operation(summary = "新增笔记", description = "创建新的笔记")
    @RequiresPermission("blog:note:add")
    @Log("新增笔记")
    public Result<Long> createNote(@Parameter(description = "笔记创建信息") @Valid @RequestBody NoteCreateDTO createDTO) {
        return Result.success(noteService.createNote(createDTO));
    }

    @PutMapping
    @Operation(summary = "更新笔记", description = "更新笔记信息")
    @RequiresPermission("blog:note:edit")
    @Log("更新笔记")
    public Result<Void> updateNote(@Parameter(description = "笔记更新信息") @Valid @RequestBody NoteUpdateDTO updateDTO) {
        noteService.updateNote(updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除笔记", description = "根据ID删除笔记")
    @RequiresPermission("blog:note:delete")
    @Log("删除笔记")
    public Result<Void> deleteNote(@Parameter(description = "笔记ID") @PathVariable Long id) {
        noteService.deleteNote(id);
        return Result.success();
    }

    @PostMapping("/unbindUnused/{id}")
    @Operation(summary = "解绑未使用图片", description = "解绑笔记中未使用的图片引用")
    @RequiresPermission("blog:note:edit")
    @Log("解绑笔记未使用图片")
    public Result<Void> unbindUnusedFiles(@Parameter(description = "笔记ID") @PathVariable Long id) {
        noteService.unbindUnusedFiles(id);
        return Result.success();
    }
}