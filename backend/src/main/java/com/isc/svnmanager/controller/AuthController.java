package com.isc.svnmanager.controller;

import com.isc.svnmanager.dto.request.LoginRequest;
import com.isc.svnmanager.dto.request.ResetPasswordRequest;
import com.isc.svnmanager.dto.response.ApiResponse;
import com.isc.svnmanager.dto.response.LoginResponse;
import com.isc.svnmanager.dto.response.UserResponse;
import com.isc.svnmanager.entity.User;
import com.isc.svnmanager.service.AuthService;
import com.isc.svnmanager.service.UserService;
import com.isc.svnmanager.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 检查是否需要初始化（数据库是否为空）
     */
    @GetMapping("/check-initialization")
    public ApiResponse<Boolean> checkInitialization() {
        boolean needsInitialization = userService.isEmpty();
        return ApiResponse.success(needsInitialization);
    }
    
    /**
     * 用户登录
     * 如果是首次登录（数据库为空），会自动创建第一个管理员用户
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 在登录前检查是否需要初始化
        boolean wasEmpty = userService.isEmpty();
        LoginResponse response = authService.login(request);
        String message = wasEmpty ? "初始化成功，已创建管理员账户" : "登录成功";
        return ApiResponse.success(message, response);
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return ApiResponse.error(401, "用户未登录");
        }
        
        boolean success = authService.resetPassword(userId, request);
        if (success) {
            return ApiResponse.success("密码重置成功", null);
        } else {
            return ApiResponse.error("密码重置失败");
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return ApiResponse.error(401, "用户未登录");
        }
        
        User user = userService.findById(userId);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        UserResponse response = UserResponse.fromUser(user);
        return ApiResponse.success(response);
    }
    
    /**
     * 用户登出（可选，JWT无状态，前端清除token即可）
     */
    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        // JWT无状态，登出主要是前端清除token
        return ApiResponse.success("登出成功", null);
    }
}

