package com.isc.svnmanager.interceptor;

import com.isc.svnmanager.annotation.RequireRole;
import com.isc.svnmanager.dao.UserMapper;
import com.isc.svnmanager.entity.User;
import com.isc.svnmanager.entity.enums.UserRole;
import com.isc.svnmanager.exception.AuthorizationException;
import com.isc.svnmanager.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 角色权限拦截器
 */
@Component
public class RoleAuthorizationInterceptor implements HandlerInterceptor {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理方法级别的注解
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        
        // 如果没有@RequireRole注解，则允许访问
        if (requireRole == null) {
            return true;
        }
        
        // 获取当前用户ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new AuthorizationException("用户未登录");
        }
        
        // 查询用户信息
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new AuthorizationException("用户不存在");
        }
        
        // 检查用户角色
        UserRole[] requiredRoles = requireRole.value();
        UserRole userRole = user.getRole();
        
        boolean hasRole = Arrays.asList(requiredRoles).contains(userRole);
        if (!hasRole) {
            throw new AuthorizationException("权限不足，需要角色: " + Arrays.toString(requiredRoles));
        }
        
        return true;
    }
}

