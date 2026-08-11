package com.knockfish.vo.note;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class NoteVO {
    private Long noteId;
    private String noteTitle;
    private int sort;
    private LocalDateTime createTime;
}
