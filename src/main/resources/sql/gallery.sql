-- ============================================================================
-- 图库（gallery）建表 + 菜单/权限点
-- 执行：mysql -u<user> -p knockfish_blog < 2026-11-02-gallery.sql
-- 说明：图库图片二进制存 R2，本表只存元数据与对象 key
-- ============================================================================

-- 1) 表结构 ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `gallery_photo` (
  `photo_id`    bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_key`    varchar(255) NOT NULL COMMENT '原图 R2 key',
  `file_name`   varchar(255) NOT NULL COMMENT '原始文件名',
  `title`       varchar(100) DEFAULT NULL COMMENT '展示标题，空则用文件名',
  `mime_type`   varchar(100) DEFAULT NULL COMMENT '原图 MIME',
  `file_size`   bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '原图字节数',
  `width`       int UNSIGNED NOT NULL DEFAULT 0 COMMENT '原图宽（水瀑流比例用）',
  `height`      int UNSIGNED NOT NULL DEFAULT 0 COMMENT '原图高',
  `thumb_key`   varchar(255) NOT NULL COMMENT '缩略图 key（宽 640 WebP）',
  `preview_key` varchar(255) DEFAULT NULL COMMENT '预览图 key（长边 1600 WebP），小图为 NULL',
  `album_id`    bigint UNSIGNED DEFAULT NULL COMMENT '相册ID，预留',
  `uploader_id` bigint UNSIGNED NOT NULL COMMENT '上传用户ID',
  `status`      tinyint NOT NULL DEFAULT 0 COMMENT '0 正常 1 隐藏 2 回收站',
  `create_time` datetime NOT NULL COMMENT '上传时间',
  PRIMARY KEY (`photo_id`),
  UNIQUE KEY `uk_file_key` (`file_key`),
  KEY `idx_status_time` (`status`, `create_time`, `photo_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图库照片';

-- 2) 菜单（侧边栏入口）----------------------------------------------------
INSERT INTO `permission`
  (`permission_name`, `permission_code`, `type`, `parent_id`, `route_name`, `path`, `icon`,
   `hidden`, `keep_alive`, `component`, `sort_order`, `create_time`, `status`)
SELECT '图库管理', 'blog:gallery:manage', 'menu', 0, 'Gallery', '/gallery',
       'mdi:image-multiple-outline', 0, 1, '/gallery', 9, NOW(), 'enable'
WHERE NOT EXISTS (SELECT 1 FROM `permission` WHERE `permission_code` = 'blog:gallery:manage' AND `type` = 'menu');

-- 3) 按钮权限 -------------------------------------------------------------
SET @gallery_menu_id = (SELECT `permission_id` FROM `permission`
                        WHERE `permission_code` = 'blog:gallery:manage' AND `type` = 'menu' LIMIT 1);

INSERT INTO `permission`
  (`permission_name`, `permission_code`, `type`, `parent_id`, `route_name`, `path`, `icon`,
   `hidden`, `keep_alive`, `component`, `sort_order`, `create_time`, `status`)
SELECT '上传图片', 'blog:gallery:upload', 'button', @gallery_menu_id, NULL, NULL, NULL,
       NULL, NULL, NULL, 1, NOW(), 'enable'
WHERE NOT EXISTS (SELECT 1 FROM `permission` WHERE `permission_code` = 'blog:gallery:upload');

INSERT INTO `permission`
  (`permission_name`, `permission_code`, `type`, `parent_id`, `route_name`, `path`, `icon`,
   `hidden`, `keep_alive`, `component`, `sort_order`, `create_time`, `status`)
SELECT '删除图片', 'blog:gallery:delete', 'button', @gallery_menu_id, NULL, NULL, NULL,
       NULL, NULL, NULL, 2, NOW(), 'enable'
WHERE NOT EXISTS (SELECT 1 FROM `permission` WHERE `permission_code` = 'blog:gallery:delete');

-- 4) 授权给超级管理员（role_id = 1）-----------------------------------------
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, `permission_id` FROM `permission`
WHERE `permission_code` IN ('blog:gallery:manage', 'blog:gallery:upload', 'blog:gallery:delete');

-- 5) 自检 -----------------------------------------------------------------
SELECT `permission_id`, `permission_name`, `permission_code`, `type`, `parent_id`
FROM `permission` WHERE `permission_code` LIKE 'blog:gallery:%' ORDER BY `permission_id`;
