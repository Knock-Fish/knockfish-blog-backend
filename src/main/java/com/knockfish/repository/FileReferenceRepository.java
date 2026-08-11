package com.knockfish.repository;

import com.knockfish.dto.file_reference.FileReferenceQueryByRefDTO;
import com.knockfish.entity.FileReference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件引用 Mapper
 */
@Mapper
public interface FileReferenceRepository {

    // ==================== 插入 ====================

    /**
     * 单文件插入
     */
    Long insert(FileReference fileReference);


    // ==================== 删除 ====================

    /**
     * 删除指定来源的所有文件
     */
    void deleteByReference(FileReferenceQueryByRefDTO fileReferenceQueryByRefDTO);

    /**
     * 根据文件ID单删
     */
    void deleteById(@Param("fileId") Long fileId);

    /**
     * 批量删除（定时清理用）
     */
    void batchDeleteByIds(@Param("list") List<Long> ids);


    // ==================== 查询 ====================

    /**
     * 查询指定来源的所有文件
     */
    List<FileReference> selectByReference(FileReferenceQueryByRefDTO fileReferenceQueryByRefDTO);

    /**
     * 查询所有被引用的文件路径（去重）
     */
    List<String> selectAllReferencedPaths();

    int batchUpdateReferenceId(@Param("fileIds") List<Long> fileIds,
                               @Param("referenceId") Long referenceId);

    void updateReferenceId(@Param("fileId") Long file,
                           @Param("referenceId") Long referenceId);

    /**
     * 批量解绑：将指定文件的 reference_id 置为 NULL（差集解绑用）
     */
    int unbindByIds(@Param("list") List<Long> fileIds);
    /**
     * 根据文件ID查询
     */
    FileReference selectById(@Param("fileId") Long fileId);

    /**
     * 全量查询所有引用记录（定时任务用）
     */
    List<FileReference> selectAllNoPage();

    /**
     * 查询孤立文件（定时清理用）
     * 1. reference_id为空且创建时间早于72小时（废弃草稿）
     * 2. reference_id有值但对应article/note已删除（残留文件）
     */
    List<FileReference> selectOrphanFiles();

}