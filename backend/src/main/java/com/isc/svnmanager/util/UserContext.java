package com.isc.svnmanager.util;

/**
 * 用户上下文工具类
 * 用于在请求处理过程中存储当前登录用户信息
 */
public class UserContext {
    
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();
    
    /**
     * 设置当前用户ID
     */
    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
    }
    
    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        return userIdHolder.get();
    }
    
    /**
     * 设置当前用户名
     */
    public static void setUsername(String username) {
        usernameHolder.set(username);
    }
    
    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        return usernameHolder.get();
    }
    
    /**
     * 清除上下文
     */
    public static void clear() {
        userIdHolder.remove();
        usernameHolder.remove();
    }
}

