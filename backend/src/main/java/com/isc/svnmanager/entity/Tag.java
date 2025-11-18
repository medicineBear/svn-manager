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
 * 标签实体类
 */
@Data
@ToString
@EqualsAndHashCode
@Alias("Tag")
public class Tag {
    
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
     * 标签名
     */
    @NotBlank(message = "标签名不能为空")
    @Size(min = 1, max = 100, message = "标签名长度必须在1-100之间")
    private String name;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

