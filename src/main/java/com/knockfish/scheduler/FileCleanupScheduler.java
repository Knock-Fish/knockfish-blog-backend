package com.knockfish.scheduler;

import com.knockfish.service.FileReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileCleanupScheduler {

    private final FileReferenceService fileReferenceService;

    @Scheduled(cron = "${scheduler.file-cleanup.cron:0 0 2 * * ?}")
    public void cleanupOrphanFiles() {
        try {
            log.info("========== 文件资源定时清理任务开始 ==========");
            fileReferenceService.cleanupOrphanFiles();
        } catch (Exception e) {
            log.error("文件资源定时清理任务执行异常", e);
        }
    }
}