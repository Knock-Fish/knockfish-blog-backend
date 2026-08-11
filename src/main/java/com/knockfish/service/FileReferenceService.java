package com.knockfish.service;

import com.knockfish.dto.file_reference.FileReferenceCreateDTO;
import com.knockfish.dto.file_reference.FileReferenceQueryByRefDTO;
import com.knockfish.vo.file_reference.FileReferenceVO;

import java.util.List;

public interface FileReferenceService {
    Long createReference(FileReferenceCreateDTO fileReferenceCreateDTO);

    void deleteByReference(FileReferenceQueryByRefDTO fileReferenceQueryByRefDTO);

    void deleteById(Long fileId);

    void batchDeleteByIds(List<Long> ids);

    void updateReferenceId(Long fileId, Long referenceId);

    void batchUpdateReferenceId(List<Long> fileIds, Long referenceId);

    /**
     * 批量解绑：将指定文件的 reference_id 置为 NULL（差集解绑用）
     */
    void unbindByIds(List<Long> fileIds);

    List<FileReferenceVO> selectByReference(FileReferenceQueryByRefDTO fileReferenceQueryByRefDTO);

    List<String> selectAllReferencedPaths();

    FileReferenceVO selectById(Long fileId);

    List<FileReferenceVO> selectAllNoPage();

    List<com.knockfish.entity.FileReference> selectOrphanFiles();

    String cleanupOrphanFiles();
}