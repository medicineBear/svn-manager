package com.isc.svnmanager.util;

import com.isc.svnmanager.config.JwtConfig;
import com.isc.svnmanager.entity.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {
    
    @Autowired
    private JwtConfig jwtConfig;
    
    /**
     * 生成JWT Token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @return JWT Token
     */
    public String generateToken(Long userId, String username, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role.name());
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getExpiration());
        
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 从Token中获取Claims
     *
     * @param token JWT Token
     * @return Claims
     */
    public Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    
    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }
    
    /**
     * 从Token中获取用户角色
     *
     * @param token JWT Token
     * @return 用户角色
     */
    public UserRole getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String roleStr = claims.get("role", String.class);
        return UserRole.valueOf(roleStr);
    }
    
    /**
     * 验证Token是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token已过期
            return false;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            // 签名验证失败
            return false;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            // Token格式错误
            return false;
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            // 不支持的JWT格式
            return false;
        } catch (IllegalArgumentException e) {
            // Token为空或null
            return false;
        } catch (Exception e) {
            // 其他异常
            return false;
        }
    }
    
    /**
     * 检查Token是否过期
     *
     * @param token JWT Token
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token已过期
            return true;
        } catch (Exception e) {
            // 其他异常，视为过期
            return true;
        }
    }
    
    /**
     * 验证Token并返回详细错误信息
     *
     * @param token JWT Token
     * @return 验证结果，如果有效返回null，否则返回错误信息
     */
    public String validateTokenWithError(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                return "Token已过期";
            }
            return null; // 有效
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return "Token已过期";
        } catch (io.jsonwebtoken.security.SignatureException e) {
            return "Token签名验证失败";
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            return "Token格式错误";
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            return "不支持的JWT格式";
        } catch (IllegalArgumentException e) {
            return "Token为空";
        } catch (Exception e) {
            return "Token验证失败：" + e.getMessage();
        }
    }
}

