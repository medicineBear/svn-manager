package com.isc.svnmanager.service;

/**
 * 密码重置服务接口
 * 用于记录和管理密码重置频率，防止恶意重置
 */
public interface PasswordResetService {
    
    /**
     * 记录密码重置请求
     *
     * @param userId 用户ID
     * @return 是否允许重置（未超过频率限制）
     */
    boolean recordPasswordResetRequest(Long userId);
    
    /**
     * 记录密码重置成功
     *
     * @param userId 用户ID
     */
    void recordPasswordResetSuccess(Long userId);
}

