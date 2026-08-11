package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.note.NoteCreateDTO;
import com.knockfish.dto.note.NoteQueryDTO;
import com.knockfish.dto.note.NoteUpdateDTO;
import com.knockfish.vo.note.NoteDetailVO;
import com.knockfish.vo.note.NoteMenuVO;
import com.knockfish.vo.note.NoteVO;

import java.util.List;

public interface NoteService {
    PageInfo<NoteVO> getNoteList(NoteQueryDTO query, Integer pageNum, Integer pageSize);
    List<NoteMenuVO> getNoteMenuList();
    NoteDetailVO getNoteById(Long noteId);
    Long createNote(NoteCreateDTO createDTO);
    void updateNote(NoteUpdateDTO updateDTO);
    void deleteNote(Long noteId);
    void unbindUnusedFiles(Long noteId);
}