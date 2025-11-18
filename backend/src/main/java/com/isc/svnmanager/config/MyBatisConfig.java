package com.isc.svnmanager.config;

import org.springframework.context.annotation.Configuration;

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
    // MyBatis 配置通过 application.yml 完成
    // Mapper 扫描已在主启动类中配置
}

