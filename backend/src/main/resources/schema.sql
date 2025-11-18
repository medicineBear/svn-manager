-- 数据库初始化脚本
-- PostgreSQL 14+
-- 用于创建所有核心数据表、索引和外键约束

-- ============================================
-- 1. 用户表 (users)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    password_reset_required BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户名唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username ON users(username);

COMMENT ON TABLE users IS '用户表';
COMMENT ON COLUMN users.id IS '主键ID';
COMMENT ON COLUMN users.username IS '用户名（唯一）';
COMMENT ON COLUMN users.password IS '密码（BCrypt加密后存储）';
COMMENT ON COLUMN users.role IS '角色（ADMIN/USER）';
COMMENT ON COLUMN users.password_reset_required IS '是否需要重置密码';
COMMENT ON COLUMN users.created_at IS '创建时间';
COMMENT ON COLUMN users.updated_at IS '更新时间';

-- ============================================
-- 2. 分支-主干组表 (branch_trunk_groups)
-- ============================================
CREATE TABLE IF NOT EXISTS branch_trunk_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    branches_path VARCHAR(500) NOT NULL,
    trunk_path VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'CLOSED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建组名唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_branch_trunk_groups_name ON branch_trunk_groups(name);

COMMENT ON TABLE branch_trunk_groups IS '分支-主干组表';
COMMENT ON COLUMN branch_trunk_groups.id IS '主键ID';
COMMENT ON COLUMN branch_trunk_groups.name IS '组名（唯一）';
COMMENT ON COLUMN branch_trunk_groups.branches_path IS 'branches路径';
COMMENT ON COLUMN branch_trunk_groups.trunk_path IS 'trunk路径';
COMMENT ON COLUMN branch_trunk_groups.status IS '状态（OPEN/CLOSED）';
COMMENT ON COLUMN branch_trunk_groups.created_at IS '创建时间';
COMMENT ON COLUMN branch_trunk_groups.updated_at IS '更新时间';

-- ============================================
-- 3. 标签表 (tags)
-- ============================================
CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tags_group_id FOREIGN KEY (group_id) REFERENCES branch_trunk_groups(id) ON DELETE CASCADE,
    CONSTRAINT uk_tags_name_group_id UNIQUE (name, group_id)
);

-- 创建(name, group_id)复合唯一索引（已通过唯一约束创建）
-- 创建 group_id 外键索引
CREATE INDEX IF NOT EXISTS idx_tags_group_id ON tags(group_id);

COMMENT ON TABLE tags IS '标签表';
COMMENT ON COLUMN tags.id IS '主键ID';
COMMENT ON COLUMN tags.group_id IS '所属组ID（外键关联 branch_trunk_groups）';
COMMENT ON COLUMN tags.name IS '标签名';
COMMENT ON COLUMN tags.created_at IS '创建时间';
COMMENT ON COLUMN tags.updated_at IS '更新时间';

-- ============================================
-- 4. SVN操作记录表 (svn_operations)
-- ============================================
CREATE TABLE IF NOT EXISTS svn_operations (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    operation_type VARCHAR(30) NOT NULL CHECK (operation_type IN ('CHECKOUT', 'CHECKIN', 'CHECKOUT_CANCEL', 'REJECT')),
    operated_by BIGINT NOT NULL,
    operated_at TIMESTAMP NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    approver VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SUCCESS', 'FAILED', 'PENDING')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_svn_operations_group_id FOREIGN KEY (group_id) REFERENCES branch_trunk_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_svn_operations_operated_by FOREIGN KEY (operated_by) REFERENCES users(id) ON DELETE RESTRICT
);

-- 创建(group_id, operated_at)复合索引
CREATE INDEX IF NOT EXISTS idx_svn_operations_group_id_operated_at ON svn_operations(group_id, operated_at);
-- 创建(operated_by, operated_at)复合索引
CREATE INDEX IF NOT EXISTS idx_svn_operations_operated_by_operated_at ON svn_operations(operated_by, operated_at);

COMMENT ON TABLE svn_operations IS 'SVN操作记录表';
COMMENT ON COLUMN svn_operations.id IS '主键ID';
COMMENT ON COLUMN svn_operations.group_id IS '所属组ID（外键关联 branch_trunk_groups）';
COMMENT ON COLUMN svn_operations.operation_type IS '操作类型（CHECKOUT/CHECKIN/CHECKOUT_CANCEL/REJECT）';
COMMENT ON COLUMN svn_operations.operated_by IS '操作人ID（外键关联 users）';
COMMENT ON COLUMN svn_operations.operated_at IS '操作时间';
COMMENT ON COLUMN svn_operations.reason IS '操作原因';
COMMENT ON COLUMN svn_operations.approver IS '审批人';
COMMENT ON COLUMN svn_operations.status IS '操作状态（SUCCESS/FAILED/PENDING）';
COMMENT ON COLUMN svn_operations.created_at IS '创建时间';
COMMENT ON COLUMN svn_operations.updated_at IS '更新时间';

-- ============================================
-- 5. 文件锁表 (file_locks)
-- ============================================
CREATE TABLE IF NOT EXISTS file_locks (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    locked_by BIGINT NOT NULL,
    locked_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_file_locks_group_id FOREIGN KEY (group_id) REFERENCES branch_trunk_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_file_locks_tag_id FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    CONSTRAINT fk_file_locks_locked_by FOREIGN KEY (locked_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT uk_file_locks_group_tag_path UNIQUE (group_id, tag_id, file_path)
);

-- 创建(group_id, tag_id, file_path)复合唯一索引（已通过唯一约束创建）

COMMENT ON TABLE file_locks IS '文件锁表';
COMMENT ON COLUMN file_locks.id IS '主键ID';
COMMENT ON COLUMN file_locks.group_id IS '所属组ID（外键关联 branch_trunk_groups）';
COMMENT ON COLUMN file_locks.tag_id IS '标签ID（外键关联 tags）';
COMMENT ON COLUMN file_locks.file_path IS '文件路径（相对路径）';
COMMENT ON COLUMN file_locks.locked_by IS '锁定人ID（外键关联 users）';
COMMENT ON COLUMN file_locks.locked_at IS '锁定时间';
COMMENT ON COLUMN file_locks.created_at IS '创建时间';
COMMENT ON COLUMN file_locks.updated_at IS '更新时间';

-- ============================================
-- 6. 操作文件详情表 (operation_files)
-- ============================================
CREATE TABLE IF NOT EXISTS operation_files (
    id BIGSERIAL PRIMARY KEY,
    operation_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    pre_version_branches BIGINT,
    pre_version_trunk BIGINT,
    post_version_branches BIGINT,
    post_version_trunk BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_operation_files_operation_id FOREIGN KEY (operation_id) REFERENCES svn_operations(id) ON DELETE CASCADE,
    CONSTRAINT fk_operation_files_tag_id FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- 创建 operation_id 外键索引
CREATE INDEX IF NOT EXISTS idx_operation_files_operation_id ON operation_files(operation_id);

COMMENT ON TABLE operation_files IS '操作文件详情表';
COMMENT ON COLUMN operation_files.id IS '主键ID';
COMMENT ON COLUMN operation_files.operation_id IS '操作ID（外键关联 svn_operations）';
COMMENT ON COLUMN operation_files.tag_id IS '标签ID（外键关联 tags）';
COMMENT ON COLUMN operation_files.file_path IS '文件路径（相对路径）';
COMMENT ON COLUMN operation_files.pre_version_branches IS '操作前branches版本号';
COMMENT ON COLUMN operation_files.pre_version_trunk IS '操作前trunk版本号';
COMMENT ON COLUMN operation_files.post_version_branches IS '操作后branches版本号';
COMMENT ON COLUMN operation_files.post_version_trunk IS '操作后trunk版本号';
COMMENT ON COLUMN operation_files.created_at IS '创建时间';

-- ============================================
-- 7. 自动更新 updated_at 字段的触发器函数
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为所有包含 updated_at 字段的表创建触发器
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_branch_trunk_groups_updated_at 
    BEFORE UPDATE ON branch_trunk_groups
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tags_updated_at 
    BEFORE UPDATE ON tags
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_svn_operations_updated_at 
    BEFORE UPDATE ON svn_operations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_file_locks_updated_at 
    BEFORE UPDATE ON file_locks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
