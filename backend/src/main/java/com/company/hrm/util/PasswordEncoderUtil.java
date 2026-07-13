package com.company.hrm.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 */
public class PasswordEncoderUtil {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成admin123的加密密码
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("密码验证: " + matches);
        
        // 生成其他常用密码的加密版本
        String[] passwords = {"admin123", "123456", "password", "admin"};
        for (String pwd : passwords) {
            String encoded = encoder.encode(pwd);
            System.out.println(pwd + " -> " + encoded);
        }
    }
}