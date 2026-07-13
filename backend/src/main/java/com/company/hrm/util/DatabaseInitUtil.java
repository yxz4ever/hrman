package com.company.hrm.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 数据库初始化工具
 */
public class DatabaseInitUtil {

    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.32.129:3306/hrm_db?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
        String username = "admin";
        String password = "123123123";
        String sqlFilePath = "D:\\project\\HR\\database\\init_admin_user.sql";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath))) {

            // 读取SQL文件
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // 跳过注释和空行
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sqlBuilder.append(line).append("\n");
                
                // 如果遇到分号，执行SQL
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString();
                    try {
                        stmt.execute(sql);
                        System.out.println("执行成功: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                    } catch (Exception e) {
                        System.out.println("执行失败: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                        System.out.println("错误: " + e.getMessage());
                    }
                    sqlBuilder = new StringBuilder();
                }
            }

            System.out.println("数据库初始化完成！");

        } catch (Exception e) {
            System.out.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}