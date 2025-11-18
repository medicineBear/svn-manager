package com.isc.svnmanager.entity;

import com.isc.svnmanager.entity.enums.OperationStatus;
import com.isc.svnmanager.entity.enums.OperationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * SVN操作记录实体类
 */
@Data
@ToString
@EqualsAndHashCode
@Alias("SvnOperation")
public class SvnOperation {
    
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
     * 操作类型（CHECKOUT/CHECKIN/CHECKOUT_CANCEL/REJECT）
     */
    @NotNull(message = "操作类型不能为空")
    private OperationType operationType;
    
    /**
     * 操作人ID（外键关联 users）
     */
    @NotNull(message = "操作人ID不能为空")
    private Long operatedBy;
    
    /**
     * 操作时间
     */
    @NotNull(message = "操作时间不能为空")
    private LocalDateTime operatedAt;
    
    /**
     * 操作原因
     */
    @NotBlank(message = "操作原因不能为空")
    @Size(max = 1000, message = "操作原因长度不能超过1000")
    private String reason;
    
    /**
     * 审批人（不能为空）
     */
    @NotBlank(message = "审批人不能为空")
    @Size(max = 50, message = "审批人长度不能超过50")
    private String approver;
    
    /**
     * 操作状态（SUCCESS/FAILED/PENDING）
     */
    @NotNull(message = "操作状态不能为空")
    private OperationStatus status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

