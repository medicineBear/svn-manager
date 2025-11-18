package com.isc.svnmanager.service.impl;

import com.isc.svnmanager.dto.request.LoginRequest;
import com.isc.svnmanager.dto.request.ResetPasswordRequest;
import com.isc.svnmanager.dto.response.LoginResponse;
import com.isc.svnmanager.entity.User;
import com.isc.svnmanager.exception.AuthenticationException;
import com.isc.svnmanager.service.AuthService;
import com.isc.svnmanager.service.LoginAttemptService;
import com.isc.svnmanager.service.PasswordResetService;
import com.isc.svnmanager.service.UserService;
import com.isc.svnmanager.util.JwtUtil;
import com.isc.svnmanager.util.PasswordEncoder;
import com.isc.svnmanager.util.ValidationUtil;
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
    
    @Autowired
    private LoginAttemptService loginAttemptService;
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        
        // 检查登录尝试限制
        if (loginAttemptService.isBlocked(username)) {
            long remainingTime = loginAttemptService.getRemainingBlockTime(username);
            throw new AuthenticationException(
                String.format("账户已被锁定，请在 %d 分钟后重试", remainingTime / 60 + 1)
            );
        }
        
        // 检查数据库是否为空（首次初始化）
        if (userService.isEmpty()) {
            // 验证首次初始化的用户名和密码
            String usernameError = ValidationUtil.getUsernameValidationError(username);
            if (usernameError != null) {
                throw new AuthenticationException("首次初始化失败：" + usernameError);
            }
            
            String passwordError = ValidationUtil.getPasswordValidationErrorStrict(password);
            if (passwordError != null) {
                throw new AuthenticationException("首次初始化失败：" + passwordError);
            }
            
            // 检查是否已有用户（防止重复初始化）
            if (!userService.isEmpty()) {
                throw new AuthenticationException("系统已初始化，请使用已有账户登录");
            }
            
            // 创建第一个管理员用户
            User adminUser = new User();
            adminUser.setUsername(username);
            adminUser.setPassword(password); // 临时存储原始密码，createUser会加密
            adminUser.setRole(com.isc.svnmanager.entity.enums.UserRole.ADMIN);
            adminUser.setPasswordResetRequired(false);
            
            adminUser = userService.createUser(adminUser);
            
            // 记录登录成功
            loginAttemptService.recordLoginSuccess(username);
            
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
        // 查询用户（防止时序攻击：无论用户是否存在都执行密码验证）
        User user = userService.findByUsername(username);
        String dummyPassword = "$2a$10$dummyHashToPreventTimingAttack"; // 虚拟哈希值
        
        // 统一响应时间：无论用户是否存在都执行密码验证
        boolean passwordMatches = false;
        if (user != null) {
            passwordMatches = passwordEncoder.matches(password, user.getPassword());
        } else {
            // 用户不存在时，仍然执行密码验证以统一响应时间
            passwordEncoder.matches(password, dummyPassword);
        }
        
        // 验证结果
        if (user == null || !passwordMatches) {
            // 记录登录失败
            loginAttemptService.recordLoginFailure(username);
            throw new AuthenticationException("用户名或密码错误");
        }
        
        // 记录登录成功
        loginAttemptService.recordLoginSuccess(username);
        
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
        // 检查密码重置频率限制
        if (!passwordResetService.recordPasswordResetRequest(userId)) {
            throw new AuthenticationException("密码重置过于频繁，请稍后再试（每小时最多3次）");
        }
        
        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AuthenticationException("新密码和确认密码不一致");
        }
        
        // 验证新密码强度
        String passwordError = ValidationUtil.getPasswordValidationErrorStrict(request.getNewPassword());
        if (passwordError != null) {
            throw new AuthenticationException("新密码不符合要求：" + passwordError);
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
            // 记录密码重置成功
            passwordResetService.recordPasswordResetSuccess(userId);
        }
        
        return success;
    }
}

