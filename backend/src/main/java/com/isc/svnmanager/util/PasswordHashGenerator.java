package com.isc.svnmanager.util;

/**
 * 密码哈希生成工具类
 * 用于生成BCrypt哈希值，可以用于创建测试用户
 * 
 * 使用方法：
 * 1. 运行main方法
 * 2. 输入要加密的密码
 * 3. 复制输出的哈希值到数据库
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        PasswordEncoder encoder = new PasswordEncoder();
        
        // 生成测试用户密码哈希
        // 默认密码：admin123
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("密码: " + password);
        System.out.println("BCrypt哈希: " + hash);
        System.out.println();
        System.out.println("SQL插入语句示例：");
        System.out.println("INSERT INTO users (username, password, role, password_reset_required) VALUES ('admin', '" + hash + "', 'ADMIN', false);");
        System.out.println("INSERT INTO users (username, password, role, password_reset_required) VALUES ('user', '" + encoder.encode("user123") + "', 'USER', false);");
    }
}

