-- ============================================================================
-- 图库：敏感标记（前台列表缩略图模糊，点开大图正常显示）
-- 执行：mysql -u<user> -p knockfish_blog < 2026-11-03-gallery-nsfw.sql
-- 说明：只加一列，存量数据默认 0（普通），不影响任何现有行为
-- ============================================================================

ALTER TABLE `gallery_photo`
  ADD COLUMN `nsfw` tinyint NOT NULL DEFAULT 0 COMMENT '0 普通 1 敏感（前台列表模糊）' AFTER `status`;

-- 自检
SHOW COLUMNS FROM `gallery_photo` LIKE 'nsfw';

-- 回滚（确认不需要时执行）
-- ALTER TABLE `gallery_photo` DROP COLUMN `nsfw`;
