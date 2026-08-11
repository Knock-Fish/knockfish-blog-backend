package com.knockfish.vo.note;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoteMenuVO {
    private Long noteId;
    private String noteTitle;
    private int sort;
}
