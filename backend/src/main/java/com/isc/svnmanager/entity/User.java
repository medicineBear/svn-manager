package com.isc.svnmanager.entity;

import com.isc.svnmanager.entity.enums.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@ToString
@EqualsAndHashCode
@Alias("User")
public class User {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户名（唯一）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 50, message = "用户名长度必须在1-50之间")
    private String username;
    
    /**
     * 密码（BCrypt加密后存储）
     * 注意：在创建用户时，可以传入原始密码，createUser方法会自动加密
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 角色（ADMIN/USER）
     */
    @NotNull(message = "角色不能为空")
    private UserRole role;
    
    /**
     * 是否需要重置密码（首次登录或管理员重置后为true）
     */
    @NotNull(message = "密码重置标志不能为空")
    private Boolean passwordResetRequired;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

