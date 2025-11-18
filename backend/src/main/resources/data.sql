-- 初始数据脚本
-- 注意：密码需要使用BCrypt加密，可以使用PasswordHashGenerator工具类生成
-- 运行：java -cp target/classes com.isc.svnmanager.util.PasswordHashGenerator

-- 测试用户（密码：admin123）
-- 注意：以下哈希值仅为示例，实际使用时需要运行PasswordHashGenerator生成新的哈希值
-- INSERT INTO users (username, password, role, password_reset_required, created_at, updated_at) 
-- VALUES ('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyY5Y5Y5Y5Y5', 'ADMIN', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 测试用户（密码：user123）
-- INSERT INTO users (username, password, role, password_reset_required, created_at, updated_at) 
-- VALUES ('user', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyY5Y5Y5Y5Y5', 'USER', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
