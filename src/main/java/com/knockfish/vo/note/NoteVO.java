package com.knockfish.vo.note;

import com.knockfish.enums.NoteStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class NoteVO {
    private Long noteId;
    private String noteTitle;
    private int sort;
    private NoteStatus status;
    private LocalDateTime createTime;
}
