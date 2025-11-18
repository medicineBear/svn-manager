package com.isc.svnmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    /**
     * JWT密钥
     */
    private String secret;
    
    /**
     * Token过期时间（毫秒）
     */
    private Long expiration;
    
    /**
     * Token请求头名称
     */
    private String header;
    
    /**
     * Token前缀
     */
    private String prefix;
}

