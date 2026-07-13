package com.company.hrm.employee.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.employee.entity.HrPosition;
import com.company.hrm.employee.service.IHrPositionService;
import com.company.hrm.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理
 */
@Tag(name = "岗位管理")
@RestController
@RequestMapping("/employee/position")
public class HrPositionController {

    @Autowired
    private IHrPositionService positionService;

    /**
     * 分页查询岗位
     */
    @Operation(summary = "分页查询岗位")
    @GetMapping("/page")
    public Result<IPage<HrPosition>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String positionName,
            @RequestParam(required = false) Long deptId) {

        Page<HrPosition> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrPosition> wrapper = new LambdaQueryWrapper<>();

        if (positionName != null && !positionName.isEmpty()) {
            wrapper.like(HrPosition::getPositionName, positionName);
        }
        if (deptId != null) {
            wrapper.eq(HrPosition::getDeptId, deptId);
        }

        wrapper.orderByAsc(HrPosition::getSortOrder);
        IPage<HrPosition> result = positionService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有岗位
     */
    @Operation(summary = "获取所有岗位")
    @GetMapping("/list")
    public Result<List<HrPosition>> list() {
        List<HrPosition> list = positionService.list();
        return Result.success(list);
    }

    /**
     * 根据部门ID获取岗位
     */
    @Operation(summary = "根据部门ID获取岗位")
    @GetMapping("/dept/{deptId}")
    public Result<List<HrPosition>> getByDeptId(@PathVariable Long deptId) {
        LambdaQueryWrapper<HrPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrPosition::getDeptId, deptId);
        wrapper.orderByAsc(HrPosition::getSortOrder);
        List<HrPosition> list = positionService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 获取岗位详情
     */
    @Operation(summary = "获取岗位详情")
    @GetMapping("/{id}")
    public Result<HrPosition> get(@PathVariable Long id) {
        HrPosition position = positionService.getById(id);
        return Result.success(position);
    }

    /**
     * 添加岗位
     */
    @Operation(summary = "添加岗位")
    @PostMapping
    public Result<Void> add(@RequestBody HrPosition position) {
        positionService.save(position);
        return Result.success();
    }

    /**
     * 修改岗位
     */
    @Operation(summary = "修改岗位")
    @PutMapping
    public Result<Void> update(@RequestBody HrPosition position) {
        positionService.updateById(position);
        return Result.success();
    }

    /**
     * 删除岗位
     */
    @Operation(summary = "删除岗位")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        positionService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除岗位
     */
    @Operation(summary = "批量删除岗位")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        positionService.removeByIds(ids);
        return Result.success();
    }
}
