package com.knockfish.repository;

import com.knockfish.dto.note.NoteQueryDTO;
import com.knockfish.entity.Note;
import com.knockfish.vo.note.NoteMenuVO;
import com.knockfish.vo.note.NoteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoteRepository {
    List<Note> selectList(NoteQueryDTO query);
    List<Note> selectMenuList();
    Note selectById(Long noteId);
    void insert(Note note);
    void updateById(Note note);
    void deleteById(Long noteId);
    Long selectNoteCount();

    /**
     * Agent: 关键词搜索笔记（标题/内容模糊匹配）
     */
    List<Note> selectByKeyword(@Param("keyword") String keyword);
}
