package com.knockfish.dto.note;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoteCreateDTO {
    private String noteTitle;
    private String noteContent;
    private int sort;
}
