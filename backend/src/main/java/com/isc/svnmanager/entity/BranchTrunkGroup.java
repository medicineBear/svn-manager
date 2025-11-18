package com.isc.svnmanager.entity;

import com.isc.svnmanager.entity.enums.GroupStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 分支-主干组实体类
 */
@Data
@ToString
@EqualsAndHashCode
@Alias("BranchTrunkGroup")
public class BranchTrunkGroup {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 组名（唯一）
     */
    @NotBlank(message = "组名不能为空")
    @Size(min = 1, max = 100, message = "组名长度必须在1-100之间")
    private String name;
    
    /**
     * branches路径
     */
    @NotBlank(message = "branches路径不能为空")
    @Size(max = 500, message = "branches路径长度不能超过500")
    private String branchesPath;
    
    /**
     * trunk路径
     */
    @NotBlank(message = "trunk路径不能为空")
    @Size(max = 500, message = "trunk路径长度不能超过500")
    private String trunkPath;
    
    /**
     * 状态（OPEN/CLOSED）
     */
    @NotNull(message = "状态不能为空")
    private GroupStatus status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

