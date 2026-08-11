package com.knockfish.convert;

import com.knockfish.dto.file_reference.FileReferenceCreateDTO;
import com.knockfish.entity.FileReference;
import com.knockfish.vo.file_reference.FileReferenceVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileReferenceConvert {
    FileReference createFileReference(FileReferenceCreateDTO fileReferenceCreateDTO);

    FileReferenceVO fileReferenceToVO(FileReference fileReference);

    List<FileReferenceVO> fileReferenceListToVOList(List<FileReference> fileReferenceList);
}