package com.isc.svnmanager.service.impl;

import com.isc.svnmanager.dao.UserMapper;
import com.isc.svnmanager.entity.User;
import com.isc.svnmanager.service.UserService;
import com.isc.svnmanager.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    @Override
    public boolean updatePassword(Long id, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        int result = userMapper.updatePassword(id, encodedPassword);
        return result > 0;
    }
    
    @Override
    public boolean updatePasswordResetRequired(Long id, Boolean passwordResetRequired) {
        int result = userMapper.updatePasswordResetRequired(id, passwordResetRequired);
        return result > 0;
    }
    
    @Override
    public boolean isEmpty() {
        return userMapper.count() == 0;
    }
    
    @Override
    public User createUser(User user) {
        // 加密密码
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // 设置默认值
        if (user.getPasswordResetRequired() == null) {
            user.setPasswordResetRequired(false);
        }
        
        userMapper.insert(user);
        return user;
    }
}

