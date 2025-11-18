package com.isc.svnmanager.util;

import java.util.regex.Pattern;

/**
 * 验证工具类
 */
public class ValidationUtil {
    
    // 用户名规则：3-20个字符，只能包含字母、数字、下划线
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    
    // 密码强度规则：至少8位，包含大小写字母和数字
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    
    /**
     * 验证用户名格式
     *
     * @param username 用户名
     * @return 是否有效
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 验证密码强度
     *
     * @param password 密码
     * @return 是否有效
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        // 至少8位
        if (password.length() < 8) {
            return false;
        }
        // 建议包含大小写字母和数字（可选，不强制）
        // return PASSWORD_PATTERN.matcher(password).matches();
        return true;
    }
    
    /**
     * 验证密码强度（严格模式：必须包含大小写字母和数字）
     *
     * @param password 密码
     * @return 是否有效
     */
    public static boolean isValidPasswordStrict(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 获取用户名验证错误信息
     *
     * @param username 用户名
     * @return 错误信息，如果有效则返回null
     */
    public static String getUsernameValidationError(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        if (username.length() < 3) {
            return "用户名长度至少3个字符";
        }
        if (username.length() > 20) {
            return "用户名长度不能超过20个字符";
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return "用户名只能包含字母、数字和下划线";
        }
        return null;
    }
    
    /**
     * 获取密码验证错误信息
     *
     * @param password 密码
     * @return 错误信息，如果有效则返回null
     */
    public static String getPasswordValidationError(String password) {
        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }
        if (password.length() < 8) {
            return "密码长度至少8位";
        }
        return null;
    }
    
    /**
     * 获取密码验证错误信息（严格模式）
     *
     * @param password 密码
     * @return 错误信息，如果有效则返回null
     */
    public static String getPasswordValidationErrorStrict(String password) {
        String basicError = getPasswordValidationError(password);
        if (basicError != null) {
            return basicError;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return "密码必须包含大小写字母和数字";
        }
        return null;
    }
}

