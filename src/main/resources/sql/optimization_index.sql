-- 用户表索引优化
CREATE INDEX idx_user_username ON `user`(username);
CREATE INDEX idx_user_email ON `user`(email);
CREATE INDEX idx_user_create_time ON `user`(create_time);

-- 文章表索引优化
CREATE INDEX idx_article_user_id ON article(user_id);
CREATE INDEX idx_article_status ON article(status);
CREATE INDEX idx_article_publish_time ON article(publish_time);
CREATE INDEX idx_article_status_user_id ON article(status, user_id);
CREATE INDEX idx_article_status_publish_time ON article(status, publish_time);

-- 标签表索引优化
CREATE INDEX idx_tag_name ON `tag`(tag_name);

-- 分类表索引优化
CREATE INDEX idx_category_name ON category(category_name);
CREATE INDEX idx_category_site_id ON category(site_id);

-- 站点表索引优化
CREATE INDEX idx_site_name ON site(site_name);

-- 用户角色关联表索引优化
CREATE INDEX idx_user_role_user_id ON user_role(user_id);
CREATE INDEX idx_user_role_role_id ON user_role(role_id);
CREATE UNIQUE INDEX uk_user_role_user_role ON user_role(user_id, role_id);

-- 文章标签关联表索引优化
CREATE INDEX idx_article_tag_article_id ON article_tag(article_id);
CREATE INDEX idx_article_tag_tag_id ON article_tag(tag_id);
CREATE UNIQUE INDEX uk_article_tag_article_tag ON article_tag(article_id, tag_id);

-- 角色权限关联表索引优化
CREATE INDEX idx_role_permission_role_id ON role_permission(role_id);
CREATE INDEX idx_role_permission_permission_id ON role_permission(permission_id);

-- 文件引用表索引优化
CREATE INDEX idx_file_reference_ref_type ON file_reference(ref_type);
CREATE INDEX idx_file_reference_ref_id ON file_reference(ref_id);
CREATE INDEX idx_file_reference_create_time ON file_reference(create_time);

-- 友链表索引优化
CREATE INDEX idx_link_status ON link(status);
CREATE INDEX idx_link_create_time ON link(create_time);

-- 权限表索引优化
CREATE INDEX idx_permission_parent_id ON permission(parent_id);
CREATE INDEX idx_permission_permission_type ON permission(permission_type);

-- 代码分类表索引优化
CREATE INDEX idx_code_category_parent_id ON code_category(parent_id);

-- 笔记表索引优化
CREATE INDEX idx_note_user_id ON note(user_id);
CREATE INDEX idx_note_create_time ON note(create_time);