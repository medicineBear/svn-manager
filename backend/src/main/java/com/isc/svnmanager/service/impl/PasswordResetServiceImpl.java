package com.isc.svnmanager.service.impl;

import com.isc.svnmanager.service.PasswordResetService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 密码重置服务实现类
 * 使用内存存储（生产环境建议使用Redis）
 */
@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    
    // 每小时最大重置次数
    private static final int MAX_RESETS_PER_HOUR = 3;
    
    // 时间窗口（毫秒）
    private static final long TIME_WINDOW = 60 * 60 * 1000; // 1小时
    
    // 存储重置记录
    // key: userId, value: [重置次数, 窗口开始时间]
    private final Map<Long, ResetAttemptInfo> resetAttempts = new ConcurrentHashMap<>();
    
    @Override
    public boolean recordPasswordResetRequest(Long userId) {
        long currentTime = System.currentTimeMillis();
        ResetAttemptInfo info = resetAttempts.get(userId);
        
        if (info == null) {
            // 第一次请求，创建新记录
            info = new ResetAttemptInfo();
            info.count = 1;
            info.windowStart = currentTime;
            resetAttempts.put(userId, info);
            return true;
        }
        
        // 检查时间窗口是否已过期
        if (currentTime - info.windowStart > TIME_WINDOW) {
            // 窗口已过期，重置计数
            info.count = 1;
            info.windowStart = currentTime;
            return true;
        }
        
        // 检查是否超过限制
        if (info.count >= MAX_RESETS_PER_HOUR) {
            return false;
        }
        
        // 增加计数
        info.count++;
        return true;
    }
    
    @Override
    public void recordPasswordResetSuccess(Long userId) {
        // 重置成功，清除记录（允许下次重置）
        resetAttempts.remove(userId);
    }
    
    /**
     * 密码重置尝试信息
     */
    private static class ResetAttemptInfo {
        int count = 0;
        long windowStart = 0;
    }
}

