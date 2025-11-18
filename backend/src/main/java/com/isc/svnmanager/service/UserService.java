package com.isc.svnmanager.service;

import com.isc.svnmanager.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(Long id);
    
    /**
     * 更新用户密码
     *
     * @param id 用户ID
     * @param newPassword 新密码（原始密码，未加密）
     * @return 是否成功
     */
    boolean updatePassword(Long id, String newPassword);
    
    /**
     * 更新密码重置标志
     *
     * @param id 用户ID
     * @param passwordResetRequired 是否需要重置密码
     * @return 是否成功
     */
    boolean updatePasswordResetRequired(Long id, Boolean passwordResetRequired);
    
    /**
     * 检查数据库是否为空（没有用户）
     *
     * @return 是否为空
     */
    boolean isEmpty();
    
    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建的用户
     */
    User createUser(User user);
}

