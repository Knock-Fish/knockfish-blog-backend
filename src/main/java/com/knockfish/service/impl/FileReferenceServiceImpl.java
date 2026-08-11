package com.knockfish.service.impl;

import com.knockfish.config.R2FileConfig;
import com.knockfish.convert.FileReferenceConvert;
import com.knockfish.dto.file_reference.FileReferenceCreateDTO;
import com.knockfish.dto.file_reference.FileReferenceQueryByRefDTO;
import com.knockfish.entity.FileReference;
import com.knockfish.repository.FileReferenceRepository;
import com.knockfish.service.FileReferenceService;
import com.knockfish.vo.file_reference.FileReferenceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileReferenceServiceImpl implements FileReferenceService {

    private final FileReferenceRepository fileReferenceRepository;
    private final FileReferenceConvert fileReferenceConvert;
    private final S3Client s3Client;
    private final R2FileConfig r2FileConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReference(FileReferenceCreateDTO fileReferenceCreateDTO) {
        FileReference fileReferenceEntity = fileReferenceConvert.createFileReference(fileReferenceCreateDTO);
        fileReferenceEntity.setCreateTime(LocalDateTime.now());
        fileReferenceRepository.insert(fileReferenceEntity);
        return fileReferenceEntity.getFileId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateReferenceId(List<Long> fileIds, Long referenceId) {
        // 1. 参数校验
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }

        // 2. 去重
        List<Long> distinctFileIds = fileIds.stream()
                .distinct()
                .collect(Collectors.toList());

        if (distinctFileIds.size() != fileIds.size()) {
            log.warn("fileIds 存在重复，已去重，从 {} 条减少到 {} 条",
                    fileIds.size(), distinctFileIds.size());
        }

        // 3. 执行批量更新
        int rows = fileReferenceRepository.batchUpdateReferenceId(distinctFileIds, referenceId);

        log.info("批量更新完成，更新了 {} 条记录，统一设置为 referenceId: {}", rows, referenceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByReference(FileReferenceQueryByRefDTO fileReferenceQueryByRefDTO) {
        fileReferenceRepository.deleteByReference(fileReferenceQueryByRefDTO);
    }

    public void updateReferenceId(Long fileId, Long referenceId){
        fileReferenceRepository.updateReferenceId(fileId, referenceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindByIds(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }
        int rows = fileReferenceRepository.unbindByIds(fileIds);
        log.info("批量解绑完成，共解绑 {} 条文件引用记录", rows);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long fileId) {
        fileReferenceRepository.deleteById(fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            log.info("批量删除: 列表为空，跳过");
        }
        log.info("批量删除文件引用: count={}", ids.size());
        fileReferenceRepository.batchDeleteByIds(ids);
    }

    @Override
    public List<FileReferenceVO> selectByReference(FileReferenceQueryByRefDTO fileReferenceQueryByRefDTO) {
        List<FileReference> fileReferenceList = fileReferenceRepository.selectByReference(fileReferenceQueryByRefDTO);
        return fileReferenceConvert.fileReferenceListToVOList(fileReferenceList);
    }

    @Override
    public List<String> selectAllReferencedPaths() {
        return fileReferenceRepository.selectAllReferencedPaths();
    }

    @Override
    public FileReferenceVO selectById(Long fileId) {
        FileReference fileReference = fileReferenceRepository.selectById(fileId);
        return fileReference != null ? fileReferenceConvert.fileReferenceToVO(fileReference) : null;
    }

    @Override
    public List<FileReferenceVO> selectAllNoPage() {
        List<FileReference> fileReferenceList = fileReferenceRepository.selectAllNoPage();
        return fileReferenceConvert.fileReferenceListToVOList(fileReferenceList);
    }

    @Override
    public List<FileReference> selectOrphanFiles() {
        return fileReferenceRepository.selectOrphanFiles();
    }

    @Override
    public String cleanupOrphanFiles() {
        try {
            log.info("========== 文件资源清理任务开始 ==========");

            List<FileReference> orphanFiles = fileReferenceRepository.selectOrphanFiles();

            if (orphanFiles.isEmpty()) {
                log.info("未发现需要清理的孤立文件，任务结束");
                log.info("========== 文件资源清理任务结束 ==========");
                return "未发现需要清理的孤立文件";
            }

            log.info("发现 {} 个孤立文件待清理:", orphanFiles.size());

            for (FileReference file : orphanFiles) {
                log.info("  文件ID: {}, 文件路径(key): {}, 文件名: {}, 文件大小: {} bytes, 创建时间: {}, 引用类型: {}, 引用ID: {}",
                        file.getFileId(), file.getFilePath(), file.getFileName(),
                        file.getFileSize(), file.getCreateTime(),
                        file.getReferenceType(), file.getReferenceId());
            }

            List<Long> fileIds = orphanFiles.stream()
                    .map(FileReference::getFileId)
                    .collect(Collectors.toList());

            int deletedCount = 0;
            for (FileReference file : orphanFiles) {
                try {
                    String fileKey = file.getFilePath();

                    DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                            .bucket(r2FileConfig.getBucketName())
                            .key(fileKey)
                            .build();

                    s3Client.deleteObject(deleteRequest);
                    deletedCount++;
                    log.info("成功删除R2文件: {}", fileKey);

                } catch (S3Exception e) {
                    log.error("删除R2文件异常，文件ID: {}, 文件Key: {}", file.getFileId(), file.getFilePath(), e);
                } catch (Exception e) {
                    log.error("删除文件异常，文件ID: {}, 文件Key: {}", file.getFileId(), file.getFilePath(), e);
                }
            }

            try {
                fileReferenceRepository.batchDeleteByIds(fileIds);
                log.info("成功从数据库删除 {} 条文件引用记录", fileIds.size());
            } catch (Exception e) {
                log.error("批量删除数据库记录异常", e);
            }

            String result = String.format("文件资源清理完成，共处理 %d 个文件，成功删除 %d 个R2文件",
                    orphanFiles.size(), deletedCount);
            log.info(result);
            log.info("========== 文件资源清理任务结束 ==========");

            return result;

        } catch (Exception e) {
            log.error("文件资源清理任务执行异常", e);
            return "文件资源清理任务执行异常: " + e.getMessage();
        }
    }

}