package com.isc.svnmanager.interceptor;

import com.isc.svnmanager.config.JwtConfig;
import com.isc.svnmanager.exception.AuthenticationException;
import com.isc.svnmanager.util.JwtUtil;
import com.isc.svnmanager.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 允许OPTIONS预检请求通过（CORS）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 获取Token
        String token = getTokenFromRequest(request);
        
        if (token == null || token.isEmpty()) {
            throw new AuthenticationException("未提供认证Token");
        }
        
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            throw new AuthenticationException("Token无效或已过期");
        }
        
        // 将用户信息存储到上下文
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        UserContext.setUserId(userId);
        UserContext.setUsername(username);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除上下文
        UserContext.clear();
    }
    
    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(jwtConfig.getHeader());
        if (header != null && header.startsWith(jwtConfig.getPrefix() + " ")) {
            return header.substring(jwtConfig.getPrefix().length() + 1);
        }
        return null;
    }
}

