package com.isc.svnmanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SVN 出入库 Web 管理应用主启动类
 * 
 * @author Dev Team
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.isc.svnmanager.dao")
public class SvnManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SvnManagerApplication.class, args);
    }
}

