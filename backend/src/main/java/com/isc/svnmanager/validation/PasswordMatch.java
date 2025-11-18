package com.isc.svnmanager.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 密码匹配验证注解
 * 用于验证两个密码字段是否一致
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
@Documented
public @interface PasswordMatch {
    
    String message() default "密码和确认密码不一致";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * 密码字段名
     */
    String password() default "newPassword";
    
    /**
     * 确认密码字段名
     */
    String confirmPassword() default "confirmPassword";
}

