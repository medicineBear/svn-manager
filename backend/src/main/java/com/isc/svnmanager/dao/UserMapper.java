package com.isc.svnmanager.dao;

import com.isc.svnmanager.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(@Param("id") Long id);
    
    /**
     * 更新用户密码
     *
     * @param id 用户ID
     * @param password 新密码（加密后）
     * @return 更新行数
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    /**
     * 更新密码重置标志
     *
     * @param id 用户ID
     * @param passwordResetRequired 是否需要重置密码
     * @return 更新行数
     */
    int updatePasswordResetRequired(@Param("id") Long id, @Param("passwordResetRequired") Boolean passwordResetRequired);
    
    /**
     * 统计用户总数
     *
     * @return 用户总数
     */
    long count();
    
    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 插入行数
     */
    int insert(User user);
}

