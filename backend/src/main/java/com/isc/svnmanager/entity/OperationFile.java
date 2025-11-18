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
 * 操作文件详情实体类
 */
@Data
@ToString
@EqualsAndHashCode
@Alias("OperationFile")
public class OperationFile {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 操作ID（外键关联 svn_operations）
     */
    @NotNull(message = "操作ID不能为空")
    private Long operationId;
    
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
     * 操作前branches版本号
     */
    private Long preVersionBranches;
    
    /**
     * 操作前trunk版本号
     */
    private Long preVersionTrunk;
    
    /**
     * 操作后branches版本号
     */
    private Long postVersionBranches;
    
    /**
     * 操作后trunk版本号
     */
    private Long postVersionTrunk;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

