package com.isc.svnmanager.entity.enums;

/**
 * SVN 操作类型枚举
 */
public enum OperationType {
    /**
     * 出库
     */
    CHECKOUT,
    
    /**
     * 入库
     */
    CHECKIN,
    
    /**
     * 出库取消
     */
    CHECKOUT_CANCEL,
    
    /**
     * 却下（拒绝）
     */
    REJECT
}

