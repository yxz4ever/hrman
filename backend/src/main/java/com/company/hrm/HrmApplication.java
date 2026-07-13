package com.company.hrm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.company.hrm.*.mapper")
public class HrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrmApplication.class, args);
        System.out.println("人力资源管理系统启动成功！");
    }
}