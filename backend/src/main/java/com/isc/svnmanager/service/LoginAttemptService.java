package com.isc.svnmanager.service;

/**
 * 登录尝试服务接口
 * 用于记录和管理登录失败次数，防止暴力破解攻击
 */
public interface LoginAttemptService {
    
    /**
     * 记录登录失败
     *
     * @param username 用户名
     */
    void recordLoginFailure(String username);
    
    /**
     * 记录登录成功
     *
     * @param username 用户名
     */
    void recordLoginSuccess(String username);
    
    /**
     * 检查是否被锁定
     *
     * @param username 用户名
     * @return 是否被锁定
     */
    boolean isBlocked(String username);
    
    /**
     * 获取剩余锁定时间（秒）
     *
     * @param username 用户名
     * @return 剩余锁定时间，如果未锁定则返回0
     */
    long getRemainingBlockTime(String username);
}

