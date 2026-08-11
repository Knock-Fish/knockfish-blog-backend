package com.knockfish.dto.file_reference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileReferenceQueryByRefDTO {
    private String referenceType;
    private Long referenceId;
}