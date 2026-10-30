package com.knockfish.dto.note;

import com.knockfish.enums.NoteStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoteStatusUpdateDTO {
    @NotNull(message = "笔记ID不能为空")
    private Long noteId;
    @NotNull(message = "状态不能为空")
    private NoteStatus status;
}
