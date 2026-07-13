package com.company.hrm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.common.Result;
import com.company.hrm.employee.entity.HrEmployeeDetail;
import com.company.hrm.employee.service.IHrEmployeeDetailService;
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
 * 用户/员工管理（整合用户和员工）
 */
@Tag(name = "用户/员工管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IHrEmployeeDetailService employeeDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户/员工
     */
    @Operation(summary = "分页查询用户/员工")
    @GetMapping
    public Result<IPage<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer employeeStatus,
            @RequestParam(required = false) Long deptId) {

        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(SysUser::getRealName, realName);
        }
        if (userType != null) {
            wrapper.eq(SysUser::getUserType, userType);
        }
        if (employeeStatus != null) {
            wrapper.eq(SysUser::getEmployeeStatus, employeeStatus);
        }
        if (deptId != null) {
            wrapper.eq(SysUser::getDeptId, deptId);
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
     * 获取用户详情（包含员工详情）
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            // 获取员工详情
            HrEmployeeDetail detail = employeeDetailService.getById(user.getId());
            return Result.success(Map.of("user", user, "detail", detail));
        }
        return Result.error("用户不存在");
    }

    /**
     * 添加用户/员工
     */
    @Operation(summary = "添加用户/员工")
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        // 检查用户名是否已存在（仅当有用户名时）
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, user.getUsername());
            if (userService.count(wrapper) > 0) {
                return Result.error("用户名已存在");
            }
        }

        // 检查手机号是否已存在
        if (user.getMobile() != null && !user.getMobile().isEmpty()) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getMobile, user.getMobile());
            if (userService.count(wrapper) > 0) {
                return Result.error("手机号已存在");
            }
        }

        // 检查身份证号是否已存在
        if (user.getIdCard() != null && !user.getIdCard().isEmpty()) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getIdCard, user.getIdCard());
            if (userService.count(wrapper) > 0) {
                return Result.error("身份证号已存在");
            }
        }

        // 加密密码（如果有）
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setStatus(user.getStatus() != null ? user.getStatus() : 1);
        user.setUserType(user.getUserType() != null ? user.getUserType() : 2); // 默认为普通员工
        user.setEmployeeStatus(user.getEmployeeStatus() != null ? user.getEmployeeStatus() : 1);

        userService.save(user);
        return Result.success();
    }

    /**
     * 修改用户/员工
     */
    @Operation(summary = "修改用户/员工")
    @PutMapping
    public Result<Void> update(@RequestBody SysUser user) {
        // 检查用户名是否已被其他用户使用
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, user.getUsername());
            wrapper.ne(SysUser::getId, user.getId());
            if (userService.count(wrapper) > 0) {
                return Result.error("用户名已存在");
            }
        }

        // 检查手机号是否已被其他用户使用
        if (user.getMobile() != null && !user.getMobile().isEmpty()) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getMobile, user.getMobile());
            wrapper.ne(SysUser::getId, user.getId());
            if (userService.count(wrapper) > 0) {
                return Result.error("手机号已存在");
            }
        }

        // 检查身份证号是否已被其他用户使用
        if (user.getIdCard() != null && !user.getIdCard().isEmpty()) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getIdCard, user.getIdCard());
            wrapper.ne(SysUser::getId, user.getId());
            if (userService.count(wrapper) > 0) {
                return Result.error("身份证号已存在");
            }
        }

        user.setPassword(null); // 不更新密码
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 添加/更新用户及员工详情
     */
    @Operation(summary = "添加/更新用户及员工详情")
    @PutMapping("/with-detail")
    public Result<Void> saveWithDetail(@RequestBody Map<String, Object> request) {
        SysUser user = (SysUser) request.get("user");
        HrEmployeeDetail detail = (HrEmployeeDetail) request.get("detail");

        if (user.getId() == null) {
            // 新增
            return add(user);
        } else {
            // 更新
            update(user);
        }

        // 保存或更新员工详情
        if (detail != null) {
            detail.setUserId(user.getId());
            HrEmployeeDetail existing = employeeDetailService.getById(user.getId());
            if (existing != null) {
                detail.setId(existing.getId());
                employeeDetailService.updateById(detail);
            } else {
                employeeDetailService.save(detail);
            }
        }

        return Result.success();
    }

    /**
     * 更新员工详情
     */
    @Operation(summary = "更新员工详情")
    @PostMapping("/detail")
    public Result<Void> updateDetail(@RequestBody HrEmployeeDetail detail) {
        if (detail.getUserId() == null) {
            return Result.error("用户 ID 不能为空");
        }

        HrEmployeeDetail existing = employeeDetailService.getById(detail.getUserId());
        if (existing != null) {
            detail.setId(existing.getId());
            employeeDetailService.updateById(detail);
        } else {
            employeeDetailService.save(detail);
        }

        return Result.success();
    }

    /**
     * 获取员工详情
     */
    @Operation(summary = "获取员工详情")
    @GetMapping("/detail/{userId}")
    public Result<HrEmployeeDetail> getDetail(@PathVariable Long userId) {
        HrEmployeeDetail detail = employeeDetailService.getById(userId);
        return Result.success(detail);
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
