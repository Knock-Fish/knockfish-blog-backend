package com.knockfish.convert;

import com.knockfish.dto.note.NoteCreateDTO;
import com.knockfish.dto.note.NoteUpdateDTO;
import com.knockfish.entity.Note;
import com.knockfish.vo.note.NoteDetailVO;
import com.knockfish.vo.note.NoteMenuVO;
import com.knockfish.vo.note.NoteVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoteConvert {
    Note createToEntity(NoteCreateDTO createDTO);
    Note updateToEntity(NoteUpdateDTO updateDTO);
    NoteDetailVO toDetailVO(Note note);
    List<NoteVO> listToVOlist(List<Note> noteList);
    List<NoteMenuVO> listToVOMenulist(List<Note> noteList);
}