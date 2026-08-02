package com.outdoor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 户外运动社交平台启动类
 */
@SpringBootApplication
@MapperScan("com.outdoor.mapper")
public class OutdoorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutdoorApplication.class, args);
        System.out.println("========================================");
        System.out.println("  户外运动社交平台启动成功！");
        System.out.println("  前台地址: http://localhost:8080");
        System.out.println("  后台地址: http://localhost:8080/login");
        System.out.println("========================================");
    }
}
