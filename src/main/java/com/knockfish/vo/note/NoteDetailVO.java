package com.knockfish.vo.note;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoteDetailVO {
    private Long noteId;    
    private String noteTitle;
    private String noteContent;
}
