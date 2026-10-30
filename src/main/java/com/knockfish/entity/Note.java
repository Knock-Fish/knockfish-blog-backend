package com.knockfish.entity;


import com.knockfish.enums.NoteStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Note {
    private Long noteId;
    private String noteTitle;
    private String noteContent;
    private int sort;
    private NoteStatus status;
    private LocalDateTime createTime;
}
