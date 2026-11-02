package com.knockfish.controller.open;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.common.Result;
import com.knockfish.service.NoteService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.note.NoteDetailVO;
import com.knockfish.vo.note.NoteMenuVO;
import com.knockfish.vo.note.NoteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/note")
@Tag(name = "前台-笔记", description = "博客前台笔记接口（匿名可访问，仅返回展示状态数据）")
public class NoteFrontController {
    private final NoteService noteService;

    @GetMapping("/menu")
    @Operation(summary = "获取笔记菜单", description = "获取笔记菜单列表")
    @Log("前台获取笔记菜单")
    public Result<List<NoteMenuVO>> getNoteMenuList() {
        return Result.success(noteService.getNoteMenuList());
    }

    @GetMapping("/display")
    @Operation(summary = "获取笔记列表（前台）", description = "仅返回显示状态的笔记")
    @Log("前台获取展示笔记列表")
    public Result<PageResultVO<NoteVO>> getDisplayNoteList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<NoteVO> noteList = noteService.getDisplayNoteList(pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(noteList));
    }

    @GetMapping("/display/{id}")
    @Operation(summary = "获取笔记详情（前台）", description = "根据ID获取显示状态的笔记详情，隐藏笔记返回不存在")
    @Log("前台获取展示笔记详情")
    public Result<NoteDetailVO> getDisplayNoteById(@Parameter(description = "笔记ID") @PathVariable Long id) {
        return Result.success(noteService.getDisplayNoteById(id));
    }
}
