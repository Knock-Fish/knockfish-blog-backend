package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.note.NoteCreateDTO;
import com.knockfish.dto.note.NoteQueryDTO;
import com.knockfish.dto.note.NoteStatusUpdateDTO;
import com.knockfish.dto.note.NoteUpdateDTO;
import com.knockfish.vo.note.NoteDetailVO;
import com.knockfish.vo.note.NoteMenuVO;
import com.knockfish.vo.note.NoteVO;

import java.util.List;

public interface NoteService {
    PageInfo<NoteVO> getNoteList(NoteQueryDTO query, Integer pageNum, Integer pageSize);

    /**
     * 前台展示列表：仅“显示”状态的笔记
     */
    PageInfo<NoteVO> getDisplayNoteList(Integer pageNum, Integer pageSize);

    List<NoteMenuVO> getNoteMenuList();
    NoteDetailVO getNoteById(Long noteId);

    /**
     * 前台展示详情：隐藏笔记视为不存在
     */
    NoteDetailVO getDisplayNoteById(Long noteId);

    Long createNote(NoteCreateDTO createDTO);
    void updateNote(NoteUpdateDTO updateDTO);
    void updateNoteStatus(NoteStatusUpdateDTO statusUpdateDTO);
    void deleteNote(Long noteId);
    void unbindUnusedFiles(Long noteId);
}