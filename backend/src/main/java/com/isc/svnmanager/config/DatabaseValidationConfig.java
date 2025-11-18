package com.isc.svnmanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 * 数据库验证配置类
 * 在应用启动时验证数据库表是否存在
 */
@Component
@Order(1) // 确保在其他组件之前执行
public class DatabaseValidationConfig implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseValidationConfig.class);
    
    private final DataSource dataSource;
    
    // 需要验证的核心表列表
    private static final String[] REQUIRED_TABLES = {
        "users",
        "branch_trunk_groups",
        "tags",
        "svn_operations",
        "file_locks",
        "operation_files"
    };
    
    public DatabaseValidationConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void run(String... args) {
        try {
            validateDatabaseTables();
        } catch (Exception e) {
            logger.error("数据库验证过程中发生错误", e);
        }
    }
    
    /**
     * 验证数据库表是否存在
     */
    private void validateDatabaseTables() {
        logger.info("开始验证数据库表结构...");
        
        Set<String> existingTables = getExistingTables();
        Set<String> missingTables = new HashSet<>();
        
        // 检查每个必需的表是否存在
        for (String tableName : REQUIRED_TABLES) {
            if (!existingTables.contains(tableName.toLowerCase())) {
                missingTables.add(tableName);
            }
        }
        
        if (missingTables.isEmpty()) {
            logger.info("✅ 数据库表验证通过，所有核心表已存在");
        } else {
            logger.error("❌ 数据库表验证失败！以下表不存在：");
            for (String tableName : missingTables) {
                logger.error("   - {}", tableName);
            }
            logger.error("");
            logger.error("═══════════════════════════════════════════════════════════");
            logger.error("请执行以下命令初始化数据库结构：");
            logger.error("");
            logger.error("Windows PowerShell:");
            logger.error("  Get-Content backend/src/main/resources/schema.sql | docker exec -i svnmanager-postgres psql -U svnmanager -d svnmanager");
            logger.error("");
            logger.error("Linux/macOS (bash):");
            logger.error("  docker exec -i svnmanager-postgres psql -U svnmanager -d svnmanager < backend/src/main/resources/schema.sql");
            logger.error("");
            logger.error("或者使用以下方法（适用于所有平台）：");
            logger.error("  docker cp backend/src/main/resources/schema.sql svnmanager-postgres:/tmp/schema.sql");
            logger.error("  docker exec svnmanager-postgres psql -U svnmanager -d svnmanager -f /tmp/schema.sql");
            logger.error("═══════════════════════════════════════════════════════════");
            logger.error("");
            logger.warn("应用将继续启动，但数据库操作可能会失败！");
        }
    }
    
    /**
     * 获取数据库中已存在的表名集合
     */
    private Set<String> getExistingTables() {
        Set<String> tables = new HashSet<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // 获取当前数据库的所有表
            // PostgreSQL 使用 "public" schema
            try (ResultSet rs = metaData.getTables(null, "public", "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if (tableName != null) {
                        tables.add(tableName.toLowerCase());
                    }
                }
            }
            
            logger.debug("发现 {} 个数据库表", tables.size());
        } catch (Exception e) {
            logger.error("获取数据库表列表时发生错误", e);
        }
        
        return tables;
    }
}

