package com.isc.svnmanager.service;

import com.isc.svnmanager.dto.request.LoginRequest;
import com.isc.svnmanager.dto.request.ResetPasswordRequest;
import com.isc.svnmanager.dto.response.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含token和用户信息）
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param request 密码重置请求
     * @return 是否成功
     */
    boolean resetPassword(Long userId, ResetPasswordRequest request);
}

