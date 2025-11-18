package com.isc.svnmanager.service.impl;

import com.isc.svnmanager.service.LoginAttemptService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录尝试服务实现类
 * 使用内存存储（生产环境建议使用Redis）
 */
@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
    
    // 最大失败次数
    private static final int MAX_ATTEMPTS = 5;
    
    // 锁定时间（秒）
    private static final long BLOCK_DURATION = 15 * 60; // 15分钟
    
    // 存储登录失败次数和锁定时间
    // key: username, value: [失败次数, 锁定时间戳]
    private final Map<String, LoginAttemptInfo> attempts = new ConcurrentHashMap<>();
    
    @Override
    public void recordLoginFailure(String username) {
        LoginAttemptInfo info = attempts.getOrDefault(username, new LoginAttemptInfo());
        info.failureCount++;
        info.blockedUntil = System.currentTimeMillis() + BLOCK_DURATION * 1000;
        attempts.put(username, info);
    }
    
    @Override
    public void recordLoginSuccess(String username) {
        // 登录成功，清除失败记录
        attempts.remove(username);
    }
    
    @Override
    public boolean isBlocked(String username) {
        LoginAttemptInfo info = attempts.get(username);
        if (info == null) {
            return false;
        }
        
        // 检查是否超过最大失败次数
        if (info.failureCount < MAX_ATTEMPTS) {
            return false;
        }
        
        // 检查是否还在锁定时间内
        if (System.currentTimeMillis() < info.blockedUntil) {
            return true;
        }
        
        // 锁定时间已过，清除记录
        attempts.remove(username);
        return false;
    }
    
    @Override
    public long getRemainingBlockTime(String username) {
        LoginAttemptInfo info = attempts.get(username);
        if (info == null) {
            return 0;
        }
        
        long remaining = (info.blockedUntil - System.currentTimeMillis()) / 1000;
        return remaining > 0 ? remaining : 0;
    }
    
    /**
     * 登录尝试信息
     */
    private static class LoginAttemptInfo {
        int failureCount = 0;
        long blockedUntil = 0;
    }
}

