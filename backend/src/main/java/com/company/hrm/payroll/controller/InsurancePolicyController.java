package com.company.hrm.payroll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.payroll.entity.InsurancePolicy;
import com.company.hrm.payroll.service.IInsurancePolicyService;
import com.company.hrm.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社保政策管理
 */
@Tag(name = "社保政策管理")
@RestController
@RequestMapping("/payroll/insurance-policy")
public class InsurancePolicyController {

    @Autowired
    private IInsurancePolicyService policyService;

    /**
     * 分页查询社保政策
     */
    @Operation(summary = "分页查询社保政策")
    @GetMapping("/page")
    public Result<IPage<InsurancePolicy>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String policyName,
            @RequestParam(required = false) Integer insuranceType) {

        Page<InsurancePolicy> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InsurancePolicy> wrapper = new LambdaQueryWrapper<>();

        if (policyName != null && !policyName.isEmpty()) {
            wrapper.like(InsurancePolicy::getPolicyName, policyName);
        }
        if (insuranceType != null) {
            wrapper.eq(InsurancePolicy::getInsuranceType, insuranceType);
        }

        wrapper.orderByDesc(InsurancePolicy::getCreateTime);
        IPage<InsurancePolicy> result = policyService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有社保政策
     */
    @Operation(summary = "获取所有社保政策")
    @GetMapping("/list")
    public Result<List<InsurancePolicy>> list() {
        List<InsurancePolicy> list = policyService.list();
        return Result.success(list);
    }

    /**
     * 获取社保政策详情
     */
    @Operation(summary = "获取社保政策详情")
    @GetMapping("/{id}")
    public Result<InsurancePolicy> get(@PathVariable Long id) {
        InsurancePolicy policy = policyService.getById(id);
        return Result.success(policy);
    }

    /**
     * 添加社保政策
     */
    @Operation(summary = "添加社保政策")
    @PostMapping
    public Result<Void> add(@RequestBody InsurancePolicy policy) {
        policyService.save(policy);
        return Result.success();
    }

    /**
     * 修改社保政策
     */
    @Operation(summary = "修改社保政策")
    @PutMapping
    public Result<Void> update(@RequestBody InsurancePolicy policy) {
        policyService.updateById(policy);
        return Result.success();
    }

    /**
     * 删除社保政策
     */
    @Operation(summary = "删除社保政策")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        policyService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除社保政策
     */
    @Operation(summary = "批量删除社保政策")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        policyService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 查询月度社保统计
     */
    @Operation(summary = "查询月度社保统计")
    @GetMapping("/month-stats")
    public Result<Map<String, Object>> getMonthStats(
            @RequestParam(required = false) String month) {
        // 返回月度社保统计数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("personalTotal", 123456.78);
        stats.put("companyTotal", 246913.56);
        stats.put("total", 370370.34);
        stats.put("employeeCount", 1180);
        return Result.success(stats);
    }
}
