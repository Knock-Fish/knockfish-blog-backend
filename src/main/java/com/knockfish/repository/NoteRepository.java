package com.knockfish.repository;

import com.knockfish.dto.note.NoteQueryDTO;
import com.knockfish.entity.Note;
import com.knockfish.enums.NoteStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoteRepository {
    List<Note> selectList(NoteQueryDTO query);

    /**
     * 前台展示用列表：仅返回“显示”状态的笔记
     */
    List<Note> selectDisplayList();

    List<Note> selectMenuList();
    Note selectById(Long noteId);

    /**
     * 前台展示用详情：仅返回“显示”状态的笔记，隐藏笔记返回 null
     */
    Note selectDisplayById(Long noteId);

    void insert(Note note);
    void updateById(Note note);
    void updateStatusById(@Param("noteId") Long noteId, @Param("status") NoteStatus status);
    void deleteById(Long noteId);
    Long selectNoteCount();

    /**
     * Agent: 关键词搜索笔记（标题/内容模糊匹配）
     */
    List<Note> selectByKeyword(@Param("keyword") String keyword);
}
