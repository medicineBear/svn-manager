package com.isc.svnmanager.service.impl;

import com.isc.svnmanager.dto.request.LoginRequest;
import com.isc.svnmanager.dto.request.ResetPasswordRequest;
import com.isc.svnmanager.dto.response.LoginResponse;
import com.isc.svnmanager.entity.User;
import com.isc.svnmanager.exception.AuthenticationException;
import com.isc.svnmanager.service.AuthService;
import com.isc.svnmanager.service.UserService;
import com.isc.svnmanager.util.JwtUtil;
import com.isc.svnmanager.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        // 检查数据库是否为空（首次初始化）
        if (userService.isEmpty()) {
            // 创建第一个管理员用户
            User adminUser = new User();
            adminUser.setUsername(request.getUsername());
            adminUser.setPassword(request.getPassword()); // 临时存储原始密码，createUser会加密
            adminUser.setRole(com.isc.svnmanager.entity.enums.UserRole.ADMIN);
            adminUser.setPasswordResetRequired(false);
            
            adminUser = userService.createUser(adminUser);
            
            // 生成JWT Token
            String token = jwtUtil.generateToken(adminUser.getId(), adminUser.getUsername(), adminUser.getRole());
            
            // 构建响应
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getRole(),
                adminUser.getPasswordResetRequired()
            );
            
            return new LoginResponse(token, userInfo);
        }
        
        // 正常登录流程
        // 查询用户
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            throw new AuthenticationException("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("用户名或密码错误");
        }
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构建响应
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.getPasswordResetRequired()
        );
        
        return new LoginResponse(token, userInfo);
    }
    
    @Override
    public boolean resetPassword(Long userId, ResetPasswordRequest request) {
        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AuthenticationException("新密码和确认密码不一致");
        }
        
        // 查询用户
        User user = userService.findById(userId);
        if (user == null) {
            throw new AuthenticationException("用户不存在");
        }
        
        // 如果不是首次登录，需要验证旧密码
        if (request.getOldPassword() != null && !request.getOldPassword().isEmpty()) {
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new AuthenticationException("旧密码错误");
            }
        }
        
        // 更新密码
        boolean success = userService.updatePassword(userId, request.getNewPassword());
        if (success) {
            // 清除密码重置标志
            userService.updatePasswordResetRequired(userId, false);
        }
        
        return success;
    }
}

