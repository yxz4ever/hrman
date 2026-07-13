package com.company.hrm.system.controller;

import com.company.hrm.common.Result;
import com.company.hrm.system.entity.SysUser;
import com.company.hrm.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        // 登录验证
        String token = sysUserService.login(username, password);

        // 查询用户信息
        SysUser user = sysUserService.findByUsername(username);
        user.setPassword(null); // 不返回密码

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    /**
     * 用户退出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // 清除Security上下文
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        return Result.success("退出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user/info")
    public Result<SysUser> getUserInfo() {
        // 从Security上下文中获取当前用户
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof com.company.hrm.system.security.LoginUser) {
            com.company.hrm.system.security.LoginUser loginUser =
                    (com.company.hrm.system.security.LoginUser) authentication.getPrincipal();
            SysUser user = loginUser.getUser();
            user.setPassword(null); // 不返回密码
            return Result.success(user);
        }

        return Result.error("未登录");
    }
}
