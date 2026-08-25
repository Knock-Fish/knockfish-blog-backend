/*
 Navicat MySQL Data Transfer

 Source Server         : manage
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : knockfish_blog

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 25/08/2026 21:58:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `article_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文章id',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '封面',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '简介',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'draft',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
  `user_id` bigint NOT NULL COMMENT '作者',
  PRIMARY KEY (`article_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 64 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for article_tag
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag`  (
  `article_id` bigint UNSIGNED NOT NULL COMMENT '文章id',
  `tag_id` bigint UNSIGNED NOT NULL COMMENT '标签id',
  PRIMARY KEY (`article_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `category_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '类别id',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类别名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for code_category
-- ----------------------------
DROP TABLE IF EXISTS `code_category`;
CREATE TABLE `code_category`  (
  `code_category_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类语言',
  `code_category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`code_category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for code_snippet
-- ----------------------------
DROP TABLE IF EXISTS `code_snippet`;
CREATE TABLE `code_snippet`  (
  `code_snippet_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '代码片段id',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `code_category_id` bigint UNSIGNED NOT NULL COMMENT '关联分类',
  `code_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '代码内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`code_snippet_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for file_reference
-- ----------------------------
DROP TABLE IF EXISTS `file_reference`;
CREATE TABLE `file_reference`  (
  `file_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `reference_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '业务ID（文章ID/笔记ID）',
  `reference_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '上传用户ID',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源相对路径',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `file_size` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '文件大小 单位byte',
  `mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'MIME类型',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 64 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gantt_link
-- ----------------------------
DROP TABLE IF EXISTS `gantt_link`;
CREATE TABLE `gantt_link`  (
  `link_id` bigint NOT NULL AUTO_INCREMENT COMMENT '连线ID',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `source` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '源任务ID',
  `target` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标任务ID',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '连线类型(0=完成-开始 finish-to-start)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`link_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_source`(`source`) USING BTREE,
  INDEX `idx_target`(`target`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for gantt_task
-- ----------------------------
DROP TABLE IF EXISTS `gantt_task`;
CREATE TABLE `gantt_task`  (
  `task_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `text` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `start` datetime NOT NULL COMMENT '开始时间',
  `end` datetime NOT NULL COMMENT '结束时间',
  `progress` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '完成进度(0~1)',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务类型(task/milestone/project)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'todo' COMMENT '任务状态(todo/doing/done/delay/cancel)',
  `owner` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务描述',
  `parent_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父任务ID(顶层为NULL)',
  `open` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否展开(0=收起 1=展开)',
  `sort_order` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '同级排序号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`task_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for link
-- ----------------------------
DROP TABLE IF EXISTS `link`;
CREATE TABLE `link`  (
  `link_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '友链id',
  `link_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '友链名称',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '友链介绍',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '友链链接',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '头像',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`link_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for note
-- ----------------------------
DROP TABLE IF EXISTS `note`;
CREATE TABLE `note`  (
  `note_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '笔记id',
  `note_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '笔记名称',
  `note_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '笔记内容',
  `sort` int NOT NULL COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`note_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `permission_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '权限id',
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限代码，如 blog:article:create',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` bigint UNSIGNED NOT NULL COMMENT '父权限ID，用于菜单/权限树',
  `route_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由name属性',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端路由路径',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `hidden` tinyint(1) NULL DEFAULT NULL COMMENT '是否隐藏',
  `keep_alive` tinyint(1) NULL DEFAULT NULL COMMENT '是否缓存组件',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端组件路径',
  `sort_order` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`permission_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (1, '仪表盘', 'blog:dashboard:view', 'directory', 0, 'Dashboard', '/dashboard', 'mdi:home', 0, NULL, '/index', 1, '2026-05-21 12:05:27', 'enable');
INSERT INTO `permission` VALUES (2, '文章管理', 'blog:article:manage', 'menu', 0, 'Article', '/article', 'mdi:file-document-outline', 0, 1, '/article', 2, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (3, '标签管理', 'blog:tag:manage', 'menu', 0, 'Tag', '/tag', 'mdi:tag-outline', 0, 1, '/tag', 3, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (4, '分类管理', 'blog:category:manage', 'menu', 0, 'Category', '/category', 'mdi:category-plus-outline', 0, 1, '/category', 4, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (5, '站点管理', 'blog:site:manage', 'menu', 0, 'Site', '/site', 'mdi:information-outline', 0, 1, '/site', 5, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (6, 'AI小助手', 'blog:chat:view', 'menu', 0, 'ChatAI', '/chat-ai', 'mdi:chat', 0, 1, '/chat-ai', 6, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (7, '友链管理', 'blog:link:manage', 'menu', 0, 'Link', '/link', 'mdi:package-variant', 0, 1, '/link', 7, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (8, '文件管理', 'blog:file:view', 'directory', 0, 'File', '/file', 'mdi:folder-open-outline', 0, NULL, '/index', 8, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (9, '用户和权限', 'blog:account:manage', 'directory', 0, 'Account', '/account', 'material-symbols:fingerprint', 0, NULL, '/index', 9, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (10, '用户管理', 'blog:user:manage', 'menu', 9, 'User', '/account/user', '', 0, 1, '/account/user', 1, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (11, '权限管理', 'blog:permission:manage', 'menu', 9, 'Permission', '/account/permission', '', 0, 1, '/account/permission', 2, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (12, '系统设置', 'blog:setting:manage', 'menu', 0, 'Setting', '/setting', 'mdi:settings', 0, 1, '/setting', 10, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (13, '编辑页面', 'blog:editor:view', 'menu', 0, 'Editor', '/editor/:id', NULL, 1, NULL, '/editor', 0, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (14, '发布页面', 'blog:publish:view', 'menu', 0, 'Publish', '/publish', NULL, 1, NULL, '/editor', 0, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (15, '文章详情', 'blog:detail:view', 'menu', 0, 'Detail', '/detail/:id', NULL, 1, NULL, '/detail', 0, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (16, '新增文章', 'blog:article:add', 'button', 2, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (17, '编辑文章', 'blog:article:edit', 'button', 2, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (18, '删除文章', 'blog:article:delete', 'button', 2, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (19, '发布文章', 'blog:article:publish', 'button', 2, NULL, NULL, NULL, NULL, NULL, NULL, 4, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (20, '新增标签', 'blog:tag:add', 'button', 3, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (21, '编辑标签', 'blog:tag:edit', 'button', 3, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (22, '删除标签', 'blog:tag:delete', 'button', 3, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (23, '新增分类', 'blog:category:add', 'button', 4, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (24, '编辑分类', 'blog:category:edit', 'button', 4, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (25, '删除分类', 'blog:category:delete', 'button', 4, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (26, '新增站点', 'blog:site:add', 'button', 5, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (27, '编辑站点', 'blog:site:edit', 'button', 5, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (28, '删除站点', 'blog:site:delete', 'button', 5, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (29, '新增友链', 'blog:link:add', 'button', 7, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (30, '编辑友链', 'blog:link:edit', 'button', 7, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (31, '删除友链', 'blog:link:delete', 'button', 7, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (32, '新增用户', 'blog:user:add', 'button', 10, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (33, '编辑用户', 'blog:user:edit', 'button', 10, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (34, '删除用户', 'blog:user:delete', 'button', 10, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (35, '重置密码', 'blog:user:resetPwd', 'button', 10, NULL, NULL, NULL, NULL, NULL, NULL, 4, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (36, '新增权限', 'blog:permission:add', 'button', 11, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (37, '编辑权限', 'blog:permission:edit', 'button', 11, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (38, '删除权限', 'blog:permission:delete', 'button', 11, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (39, '分配权限', 'blog:permission:assign', 'button', 11, NULL, NULL, NULL, NULL, NULL, NULL, 4, '2026-05-21 12:46:17', 'enable');
INSERT INTO `permission` VALUES (40, '角色管理', 'blog:role:manage', 'menu', 9, 'Role', '/account/role', '', 0, 0, '/account/role', 3, '2026-05-22 17:25:11', 'enable');
INSERT INTO `permission` VALUES (41, '系统总览', 'blog:overview:view', 'menu', 1, 'Overview', '/dashboard/overview', '', 0, 1, '/dashboard/overview', 2, '2026-05-27 15:59:38', 'enable');
INSERT INTO `permission` VALUES (42, '工作台', 'blog:workbench:view', 'menu', 1, 'Workbench', '/dashboard/workbench', '', 0, 1, '/dashboard/workbench', 1, '2026-05-27 16:02:26', 'enable');
INSERT INTO `permission` VALUES (43, '新增角色', 'blog:role:add', 'button', 40, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2026-05-28 17:31:45', 'enable');
INSERT INTO `permission` VALUES (44, '编辑角色', 'blog:role:edit', 'button', 40, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2026-05-28 17:33:48', 'enable');
INSERT INTO `permission` VALUES (45, '删除角色', 'blog:role:delete', 'button', 40, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2026-05-28 17:34:42', 'enable');
INSERT INTO `permission` VALUES (46, '文件详情', 'blog:file:view', 'menu', 0, 'FileDetail', '/file-detail/:key', NULL, 1, 0, '/file-detail', 0, '2026-05-30 02:17:05', 'enable');
INSERT INTO `permission` VALUES (47, '权限分配', 'blog:permission:assign', 'button', 40, NULL, NULL, NULL, NULL, NULL, NULL, 0, '2026-06-06 12:00:46', 'enable');
INSERT INTO `permission` VALUES (49, '归档管理', 'blog:archive:view', 'menu', 0, 'Archive', '/archive', 'mdi:archive-outline', 0, 0, '/index', 10, '2026-06-19 18:52:40', 'enable');
INSERT INTO `permission` VALUES (50, '笔记管理', 'blog:note:manage', 'menu', 49, 'Note', '/archive/note', '', 0, 1, '/archive/note', 1, '2026-06-20 00:36:07', 'enable');
INSERT INTO `permission` VALUES (51, '语言分类', 'blog:code-category:manage', 'menu', 49, 'CodeCategory', '/archive/code-category', '', 0, 1, '/archive/code-category', 2, '2026-06-20 00:57:26', 'enable');
INSERT INTO `permission` VALUES (52, '代码片段', 'blog:code-snippet:manage', 'menu', 49, 'CodeSnippet', '/archive/code-snippet', '', 0, 1, '/archive/code-snippet', 3, '2026-06-20 01:10:52', 'enable');
INSERT INTO `permission` VALUES (54, '查看笔记', 'blog:note:view', 'button', 50, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-20 15:03:15', 'enable');
INSERT INTO `permission` VALUES (55, '新增笔记', 'blog:note:add', 'button', 50, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2026-06-20 15:03:15', 'enable');
INSERT INTO `permission` VALUES (56, '编辑笔记', 'blog:note:edit', 'button', 50, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2026-06-20 15:03:15', 'enable');
INSERT INTO `permission` VALUES (57, '删除笔记', 'blog:note:delete', 'button', 50, NULL, NULL, NULL, NULL, NULL, NULL, 4, '2026-06-20 15:03:15', 'enable');
INSERT INTO `permission` VALUES (58, '查看语言分类', 'blog:code-category:view', 'button', 51, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-20 15:03:41', 'enable');
INSERT INTO `permission` VALUES (59, '新增语言分类', 'blog:code-category:add', 'button', 51, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2026-06-20 15:03:41', 'enable');
INSERT INTO `permission` VALUES (60, '编辑语言分类', 'blog:code-category:edit', 'button', 51, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2026-06-20 15:03:41', 'enable');
INSERT INTO `permission` VALUES (61, '删除语言分类', 'blog:code-category:delete', 'button', 51, NULL, NULL, NULL, NULL, NULL, NULL, 4, '2026-06-20 15:03:41', 'enable');
INSERT INTO `permission` VALUES (62, '查看代码片段', 'blog:code-snippet:view', 'button', 52, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2026-06-20 15:03:56', 'enable');
INSERT INTO `permission` VALUES (63, '新增代码片段', 'blog:code-snippet:add', 'button', 52, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2026-06-20 15:03:56', 'enable');
INSERT INTO `permission` VALUES (64, '编辑代码片段', 'blog:code-snippet:edit', 'button', 52, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2026-06-20 15:03:56', 'enable');
INSERT INTO `permission` VALUES (65, '删除代码片段', 'blog:code-snippet:delete', 'button', 52, NULL, NULL, NULL, NULL, NULL, NULL, 4, '2026-06-20 15:03:56', 'enable');
INSERT INTO `permission` VALUES (66, '文件存储', 'blog:storage:manage', 'menu', 8, 'FileStorage', '/file/storage', '', 0, 1, '/file/storage', 1, '2026-07-03 20:53:48', 'enable');
INSERT INTO `permission` VALUES (67, '资源关联', 'blog:reference:manage', 'menu', 8, 'FileReference', '/file/reference', '', 0, 1, '/file/reference', 2, '2026-07-03 21:01:15', 'enable');
INSERT INTO `permission` VALUES (68, '分配角色', 'blog:user:role', 'button', 10, NULL, NULL, NULL, NULL, NULL, NULL, 5, '2026-07-06 00:03:20', 'enable');
INSERT INTO `permission` VALUES (69, '待办事项', 'blog:todo:manage', 'menu', 0, 'Todo', '/todo', NULL, 1, 1, '/todo', 0, '2026-08-25 17:19:11', 'enable');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `role_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '超级管理员', '拥有系统全部权限', '2026-05-21 00:19:20');
INSERT INTO `role` VALUES (2, '管理员', '拥有部分系统权限', '2026-05-21 16:58:51');
INSERT INTO `role` VALUES (5, '演示角色', '拥有浏览权限', '2026-07-16 14:10:48');
INSERT INTO `role` VALUES (6, '123', '213', '2026-07-16 14:41:22');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `role_id` bigint UNSIGNED NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (1, 1);
INSERT INTO `role_permission` VALUES (1, 2);
INSERT INTO `role_permission` VALUES (1, 3);
INSERT INTO `role_permission` VALUES (1, 4);
INSERT INTO `role_permission` VALUES (1, 5);
INSERT INTO `role_permission` VALUES (1, 6);
INSERT INTO `role_permission` VALUES (1, 7);
INSERT INTO `role_permission` VALUES (1, 8);
INSERT INTO `role_permission` VALUES (1, 9);
INSERT INTO `role_permission` VALUES (1, 10);
INSERT INTO `role_permission` VALUES (1, 11);
INSERT INTO `role_permission` VALUES (1, 12);
INSERT INTO `role_permission` VALUES (1, 13);
INSERT INTO `role_permission` VALUES (1, 14);
INSERT INTO `role_permission` VALUES (1, 15);
INSERT INTO `role_permission` VALUES (1, 16);
INSERT INTO `role_permission` VALUES (1, 17);
INSERT INTO `role_permission` VALUES (1, 18);
INSERT INTO `role_permission` VALUES (1, 19);
INSERT INTO `role_permission` VALUES (1, 20);
INSERT INTO `role_permission` VALUES (1, 21);
INSERT INTO `role_permission` VALUES (1, 22);
INSERT INTO `role_permission` VALUES (1, 23);
INSERT INTO `role_permission` VALUES (1, 24);
INSERT INTO `role_permission` VALUES (1, 25);
INSERT INTO `role_permission` VALUES (1, 26);
INSERT INTO `role_permission` VALUES (1, 27);
INSERT INTO `role_permission` VALUES (1, 28);
INSERT INTO `role_permission` VALUES (1, 29);
INSERT INTO `role_permission` VALUES (1, 30);
INSERT INTO `role_permission` VALUES (1, 31);
INSERT INTO `role_permission` VALUES (1, 32);
INSERT INTO `role_permission` VALUES (1, 33);
INSERT INTO `role_permission` VALUES (1, 34);
INSERT INTO `role_permission` VALUES (1, 35);
INSERT INTO `role_permission` VALUES (1, 36);
INSERT INTO `role_permission` VALUES (1, 37);
INSERT INTO `role_permission` VALUES (1, 38);
INSERT INTO `role_permission` VALUES (1, 39);
INSERT INTO `role_permission` VALUES (1, 40);
INSERT INTO `role_permission` VALUES (1, 41);
INSERT INTO `role_permission` VALUES (1, 42);
INSERT INTO `role_permission` VALUES (1, 43);
INSERT INTO `role_permission` VALUES (1, 44);
INSERT INTO `role_permission` VALUES (1, 45);
INSERT INTO `role_permission` VALUES (1, 46);
INSERT INTO `role_permission` VALUES (1, 47);
INSERT INTO `role_permission` VALUES (1, 49);
INSERT INTO `role_permission` VALUES (1, 50);
INSERT INTO `role_permission` VALUES (1, 51);
INSERT INTO `role_permission` VALUES (1, 52);
INSERT INTO `role_permission` VALUES (1, 53);
INSERT INTO `role_permission` VALUES (1, 54);
INSERT INTO `role_permission` VALUES (1, 55);
INSERT INTO `role_permission` VALUES (1, 56);
INSERT INTO `role_permission` VALUES (1, 57);
INSERT INTO `role_permission` VALUES (1, 58);
INSERT INTO `role_permission` VALUES (1, 59);
INSERT INTO `role_permission` VALUES (1, 60);
INSERT INTO `role_permission` VALUES (1, 61);
INSERT INTO `role_permission` VALUES (1, 62);
INSERT INTO `role_permission` VALUES (1, 63);
INSERT INTO `role_permission` VALUES (1, 64);
INSERT INTO `role_permission` VALUES (1, 65);
INSERT INTO `role_permission` VALUES (1, 66);
INSERT INTO `role_permission` VALUES (1, 67);
INSERT INTO `role_permission` VALUES (1, 68);
INSERT INTO `role_permission` VALUES (1, 69);


-- ----------------------------
-- Table structure for site
-- ----------------------------
DROP TABLE IF EXISTS `site`;
CREATE TABLE `site`  (
  `site_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '网站id',
  `site_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网站名称',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网站描述',
  `ico` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网站图标',
  `site_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '网址',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `category_id` bigint UNSIGNED NOT NULL COMMENT '所属类别',
  PRIMARY KEY (`site_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `tag_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `tag_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签颜色',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
  `github_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人github地址',
  `bilibili_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人b站账号',
  `background` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人后台背景图',
  `create_time` datetime NOT NULL COMMENT '账号创建时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'user_fish', '$2a$10$pBScgnI.fdHEKvvW6RXLlurn/pZAliRyGl8eNSauuHJjy.fqLf1LK', 'user_fish@qq.com', 'user_fish', 'https://fishbarn.cn/blog/article-image/2026-06/168954af-15b9-4268-9743-080e1b495538.webp', '演示账号', NULL, NULL, NULL, '2026-05-28 11:17:59');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1);

SET FOREIGN_KEY_CHECKS = 1;
