package com.isc.svnmanager.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * MyBatis 配置类
 * 
 * @author Dev Team
 * 
 * 注意：@MapperScan 已在主启动类 SvnManagerApplication 中配置，
 * 此处不再重复配置，避免冗余。
 */
@Configuration
public class MyBatisConfig {
    
    private final SqlSessionFactory sqlSessionFactory;
    
    public MyBatisConfig(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
    /**
     * 注册枚举类型处理器
     */
    @PostConstruct
    public void registerEnumTypeHandlers() {
        TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
        
        // 注册UserRole专用类型处理器
        typeHandlerRegistry.register(
            com.isc.svnmanager.entity.enums.UserRole.class,
            com.isc.svnmanager.config.handler.UserRoleTypeHandler.class
        );
        
        // 注册其他枚举类型处理器
        typeHandlerRegistry.register(
            com.isc.svnmanager.entity.enums.OperationType.class,
            com.isc.svnmanager.config.handler.EnumTypeHandler.class
        );
        typeHandlerRegistry.register(
            com.isc.svnmanager.entity.enums.OperationStatus.class,
            com.isc.svnmanager.config.handler.EnumTypeHandler.class
        );
        typeHandlerRegistry.register(
            com.isc.svnmanager.entity.enums.GroupStatus.class,
            com.isc.svnmanager.config.handler.EnumTypeHandler.class
        );
    }
}

