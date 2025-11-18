package com.isc.svnmanager.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 文件锁实体类
 */
@Data
@ToString
@EqualsAndHashCode
@Alias("FileLock")
public class FileLock {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 所属组ID（外键关联 branch_trunk_groups）
     */
    @NotNull(message = "组ID不能为空")
    private Long groupId;
    
    /**
     * 标签ID（外键关联 tags）
     */
    @NotNull(message = "标签ID不能为空")
    private Long tagId;
    
    /**
     * 文件路径（相对路径）
     */
    @NotBlank(message = "文件路径不能为空")
    @Size(max = 1000, message = "文件路径长度不能超过1000")
    private String filePath;
    
    /**
     * 锁定人ID（外键关联 users）
     */
    @NotNull(message = "锁定人ID不能为空")
    private Long lockedBy;
    
    /**
     * 锁定时间
     */
    @NotNull(message = "锁定时间不能为空")
    private LocalDateTime lockedAt;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

