package com.example.casclient1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CAS客户端1应用
 * 作为CAS单点登录系统中的一个客户端应用
 * 通过CAS协议与CAS服务器交互，实现单点登录功能
 */
@SpringBootApplication
public class CasClient1Application {
    public static void main(String[] args) {
        SpringApplication.run(CasClient1Application.class, args);
    }
} 