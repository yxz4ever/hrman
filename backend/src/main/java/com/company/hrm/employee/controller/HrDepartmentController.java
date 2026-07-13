package com.company.hrm.employee.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.employee.entity.HrDepartment;
import com.company.hrm.employee.service.IHrDepartmentService;
import com.company.hrm.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/employee/department")
public class HrDepartmentController {

    @Autowired
    private IHrDepartmentService departmentService;

    /**
     * 分页查询部门
     */
    @Operation(summary = "分页查询部门")
    @GetMapping
    public Result<IPage<HrDepartment>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String deptName) {

        Page<HrDepartment> page = new Page<>(current, size);
        LambdaQueryWrapper<HrDepartment> wrapper = new LambdaQueryWrapper<>();

        if (deptName != null && !deptName.isEmpty()) {
            wrapper.like(HrDepartment::getDeptName, deptName);
        }

        wrapper.orderByAsc(HrDepartment::getSortOrder);
        IPage<HrDepartment> result = departmentService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取部门树
     */
    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public Result<List<HrDepartment>> tree() {
        List<HrDepartment> list = departmentService.list();
        return Result.success(list);
    }

    /**
     * 获取所有部门
     */
    @Operation(summary = "获取所有部门")
    @GetMapping("/list")
    public Result<List<HrDepartment>> list() {
        List<HrDepartment> list = departmentService.list();
        return Result.success(list);
    }

    /**
     * 获取部门详情
     */
    @Operation(summary = "获取部门详情")
    @GetMapping("/{id}")
    public Result<HrDepartment> get(@PathVariable Long id) {
        HrDepartment department = departmentService.getById(id);
        return Result.success(department);
    }

    /**
     * 添加部门
     */
    @Operation(summary = "添加部门")
    @PostMapping
    public Result<Void> add(@RequestBody HrDepartment department) {
        departmentService.save(department);
        return Result.success();
    }

    /**
     * 修改部门
     */
    @Operation(summary = "修改部门")
    @PutMapping
    public Result<Void> update(@RequestBody HrDepartment department) {
        departmentService.updateById(department);
        return Result.success();
    }

    /**
     * 删除部门
     */
    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除部门
     */
    @Operation(summary = "批量删除部门")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        departmentService.removeByIds(ids);
        return Result.success();
    }
}
