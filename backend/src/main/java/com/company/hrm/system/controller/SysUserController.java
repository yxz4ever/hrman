package com.company.hrm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.common.Result;
import com.company.hrm.system.entity.SysUser;
import com.company.hrm.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户
     */
    @Operation(summary = "分页查询用户")
    @PostMapping("/page")
    public Result<IPage<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {

        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(SysUser::getRealName, realName);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }

        wrapper.orderByDesc(SysUser::getUpdatedAt);
        IPage<SysUser> result = userService.page(page, wrapper);
        
        // 移除密码字段
        result.getRecords().forEach(user -> user.setPassword(null));
        
        return Result.success(result);
    }

    /**
     * 获取所有用户（用于下拉选择）
     */
    @Operation(summary = "获取所有用户")
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        List<SysUser> list = userService.list();
        list.forEach(user -> user.setPassword(null));
        return Result.success(list);
    }

    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<SysUser> get(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 添加用户
     */
    @Operation(summary = "添加用户")
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        if (userService.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(user.getStatus() != null ? user.getStatus() : 1);
        
        userService.save(user);
        return Result.success();
    }

    /**
     * 修改用户
     */
    @Operation(summary = "修改用户")
    @PutMapping
    public Result<Void> update(@RequestBody SysUser user) {
        // 检查用户名是否已被其他用户使用
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, user.getUsername());
        wrapper.ne(SysUser::getId, user.getId());
        if (userService.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        user.setPassword(null); // 不更新密码
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 不能删除自己
        // TODO: 获取当前用户 ID 进行比较
        
        userService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除用户
     */
    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        userService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码")
    @PutMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String newPassword = request.get("newPassword").toString();

        SysUser user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 加密新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(user);
        
        return Result.success();
    }

    /**
     * 修改自身密码
     */
    @Operation(summary = "修改自身密码")
    @PutMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> request) {
        Long userId = Long.valueOf(request.get("userId"));
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        SysUser user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }

        // 更新新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(user);
        
        return Result.success();
    }

    /**
     * 启用/禁用用户
     */
    @Operation(summary = "启用/禁用用户")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Integer status = Integer.valueOf(request.get("status").toString());

        SysUser user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        user.setStatus(status);
        userService.updateById(user);
        
        return Result.success();
    }
}
