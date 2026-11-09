-- ============================================================================
-- 图库相册（2026-11-05）
-- 相册独立密码：password_hash 非空即私密，前台需解锁后才能取照片
-- ============================================================================

create table if not exists `gallery_album`
(
    `album_id`       bigint unsigned not null auto_increment comment '相册ID',
    `name`           varchar(64)     not null comment '相册名',
    `description`    varchar(255)             default null comment '描述',
    `cover_photo_id` bigint unsigned          default null comment '封面照片，空则取该相册第一张',
    `password_hash`  varchar(100)             default null comment 'BCrypt 哈希；null 表示公开',
    `password_ver`   int             not null default 0 comment '改密码时 +1，让旧令牌失效',
    `sort_order`     int             not null default 0 comment '排序，小的在前',
    `status`         tinyint         not null default 0 comment '0 正常 / 1 隐藏（后台可见）',
    `create_time`    datetime        not null comment '创建时间',
    primary key (`album_id`),
    key `idx_status_sort` (`status`, `sort_order`)
) engine = InnoDB
  default charset = utf8mb4 comment ='图库相册';

-- 照片侧只补索引，album_id 列建表时就已预留
alter table `gallery_photo`
    add index `idx_album_status` (`album_id`, `status`);

-- ----------------------------------------------------------------------------
-- 相册管理权限点（父级 70 = 图库管理菜单）
-- ----------------------------------------------------------------------------
insert into `permission`
(`permission_name`, `permission_code`, `type`, `parent_id`, `route_name`, `path`, `icon`,
 `hidden`, `keep_alive`, `component`, `sort_order`, `create_time`, `status`)
select '相册管理', 'blog:gallery:album', 'button', 70, null, null, null, null, null, null, 2, now(), 'enable'
where not exists(select 1 from `permission` where `permission_code` = 'blog:gallery:album');

insert ignore into `role_permission` (`role_id`, `permission_id`)
select 1, `permission_id` from `permission` where `permission_code` = 'blog:gallery:album';
