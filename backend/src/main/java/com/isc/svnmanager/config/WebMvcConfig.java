package com.isc.svnmanager.config;

import com.isc.svnmanager.interceptor.AuthenticationInterceptor;
import com.isc.svnmanager.interceptor.RoleAuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;
    
    @Autowired
    private RoleAuthorizationInterceptor roleAuthorizationInterceptor;
    
    @Autowired
    private Environment environment;
    
    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProd = activeProfiles.length > 0 && 
                        (activeProfiles[0].equals("prod") || activeProfiles[0].equals("production"));
        
        CorsRegistration corsRegistration = registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        
        if (isProd) {
            // 生产环境：只允许配置的域名
            if (allowedOrigins != null && !allowedOrigins.equals("*")) {
                String[] origins = allowedOrigins.split(",");
                corsRegistration.allowedOrigins(origins);
            } else {
                // 生产环境未配置允许的域名，使用默认值并警告
                System.err.println("警告：生产环境CORS配置未指定允许的域名，使用默认配置");
                corsRegistration.allowedOriginPatterns("*");
            }
        } else {
            // 开发环境：允许所有来源
            corsRegistration.allowedOriginPatterns("*");
        }
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 认证拦截器：拦截需要认证的接口（排除登录、健康检查等公开接口）
        // 注意：OPTIONS请求已在拦截器内部处理，这里不需要额外排除
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/login",
                    "/auth/check-initialization",
                    "/health",
                    "/error"
                );
        
        // 角色权限拦截器：在所有接口上检查角色权限
        registry.addInterceptor(roleAuthorizationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/login",
                    "/auth/check-initialization",
                    "/health",
                    "/error"
                );
    }
}

