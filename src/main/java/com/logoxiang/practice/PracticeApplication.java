package com.logoxiang.practice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.logoxiang.practice.dao")
public class PracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticeApplication.class, args);
    }

}
