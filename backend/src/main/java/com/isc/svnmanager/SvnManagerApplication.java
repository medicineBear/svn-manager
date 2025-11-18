package com.isc.svnmanager;

import com.isc.svnmanager.config.JwtConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * SVN 出入库 Web 管理应用主启动类
 * 
 * @author Dev Team
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.isc.svnmanager.dao")
public class SvnManagerApplication implements CommandLineRunner {

    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(SvnManagerApplication.class, args);
    }
    
    @Override
    public void run(String... args) {
        // 检查JWT密钥是否使用默认值
        String defaultSecret = "svn-manager-jwt-secret-key-change-in-production-environment";
        String currentSecret = jwtConfig.getSecret();
        String activeProfile = environment.getActiveProfiles().length > 0 
            ? environment.getActiveProfiles()[0] 
            : "default";
        
        // 生产环境必须使用环境变量
        if ("prod".equals(activeProfile) || "production".equals(activeProfile)) {
            if (currentSecret == null || currentSecret.equals(defaultSecret) || 
                currentSecret.isEmpty() || System.getenv("JWT_SECRET") == null) {
                System.err.println("==========================================");
                System.err.println("严重安全警告：生产环境必须设置JWT_SECRET环境变量！");
                System.err.println("当前使用的是默认密钥或不安全的配置。");
                System.err.println("请设置环境变量 JWT_SECRET 为强随机密钥（至少32字符）。");
                System.err.println("==========================================");
                System.exit(1);
            }
        } else {
            // 开发环境给出警告
            if (currentSecret != null && currentSecret.equals(defaultSecret)) {
                System.out.println("==========================================");
                System.out.println("安全提示：当前使用默认JWT密钥，仅适用于开发环境。");
                System.out.println("生产环境部署前，请务必设置JWT_SECRET环境变量。");
                System.out.println("==========================================");
            }
        }
    }
}

