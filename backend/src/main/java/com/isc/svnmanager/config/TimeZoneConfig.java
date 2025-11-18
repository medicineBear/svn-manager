package com.isc.svnmanager.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * 时区配置类
 * 确保应用使用统一的时区（UTC）
 */
@Configuration
public class TimeZoneConfig {
    
    /**
     * 设置 JVM 默认时区为 UTC
     * 确保应用中的所有时间戳都使用统一的时区
     */
    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}

