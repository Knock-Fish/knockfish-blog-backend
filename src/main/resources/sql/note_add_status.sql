-- ----------------------------
-- 笔记（note）新增展示状态字段
-- 与友链（link.status）保持一致：display-显示 / hide-隐藏
-- 执行前请确认已备份数据库
-- ----------------------------

ALTER TABLE `note`
    ADD COLUMN `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'display' COMMENT '展示状态：display-显示 hide-隐藏' AFTER `sort`;

-- 存量数据兜底（默认值已覆盖，此处仅保证历史脏数据被修正）
UPDATE `note` SET `status` = 'display' WHERE `status` IS NULL OR `status` = '';

-- 前台列表按状态过滤，补充索引
ALTER TABLE `note` ADD INDEX `idx_note_status_sort` (`status`, `sort`, `note_id`);
