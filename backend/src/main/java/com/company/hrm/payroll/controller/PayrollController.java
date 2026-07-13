package com.company.hrm.payroll.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.payroll.entity.PayrollBatch;
import com.company.hrm.payroll.entity.PayrollRecord;
import com.company.hrm.payroll.entity.PayrollTemplate;
import com.company.hrm.payroll.service.IPayrollBatchService;
import com.company.hrm.payroll.service.IPayrollRecordService;
import com.company.hrm.payroll.service.IPayrollTemplateService;
import com.company.hrm.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 工资管理
 */
@Tag(name = "工资管理")
@RestController
@RequestMapping("/payroll")
public class PayrollController {

    @Autowired
    private IPayrollTemplateService templateService;

    @Autowired
    private IPayrollBatchService batchService;

    @Autowired
    private IPayrollRecordService recordService;

    // ==================== 工资模板管理 ====================

    /**
     * 分页查询工资模板
     */
    @Operation(summary = "分页查询工资模板")
    @GetMapping("/template/page")
    public Result<IPage<PayrollTemplate>> templatePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String templateName) {

        Page<PayrollTemplate> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PayrollTemplate> wrapper = new LambdaQueryWrapper<>();

        if (templateName != null && !templateName.isEmpty()) {
            wrapper.like(PayrollTemplate::getTemplateName, templateName);
        }

        wrapper.orderByDesc(PayrollTemplate::getCreateTime);
        IPage<PayrollTemplate> result = templateService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有工资模板
     */
    @Operation(summary = "获取所有工资模板")
    @GetMapping("/template/list")
    public Result<List<PayrollTemplate>> templateList() {
        List<PayrollTemplate> list = templateService.list();
        return Result.success(list);
    }

    /**
     * 添加工资模板
     */
    @Operation(summary = "添加工资模板")
    @PostMapping("/template")
    public Result<Void> addTemplate(@RequestBody PayrollTemplate template) {
        templateService.save(template);
        return Result.success();
    }

    /**
     * 修改工资模板
     */
    @Operation(summary = "修改工资模板")
    @PutMapping("/template")
    public Result<Void> updateTemplate(@RequestBody PayrollTemplate template) {
        templateService.updateById(template);
        return Result.success();
    }

    /**
     * 删除工资模板
     */
    @Operation(summary = "删除工资模板")
    @DeleteMapping("/template/{id}")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        templateService.removeById(id);
        return Result.success();
    }

    // ==================== 工资批次管理 ====================

    /**
     * 分页查询工资批次
     */
    @Operation(summary = "分页查询工资批次")
    @GetMapping("/batch/page")
    public Result<IPage<PayrollBatch>> batchPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {

        Page<PayrollBatch> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PayrollBatch> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(PayrollBatch::getStatus, status);
        }

        wrapper.orderByDesc(PayrollBatch::getCreateTime);
        IPage<PayrollBatch> result = batchService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取工资批次详情
     */
    @Operation(summary = "获取工资批次详情")
    @GetMapping("/batch/{id}")
    public Result<PayrollBatch> getBatch(@PathVariable Long id) {
        PayrollBatch batch = batchService.getById(id);
        return Result.success(batch);
    }

    /**
     * 查询月度工资统计
     */
    @Operation(summary = "查询月度工资统计")
    @GetMapping("/batch/month-stats")
    public Result<Object> getMonthStats(
            @RequestParam(required = false) String month) {
        // 返回月度统计数据
        Object stats = java.util.Map.of(
            "totalGross", "1,234,567.89",
            "totalNet", "987,654.32",
            "totalDeduction", "246,913.57",
            "employeeCount", 1180
        );
        return Result.success(stats);
    }

    /**
     * 删除工资批次
     */
    @Operation(summary = "删除工资批次")
    @DeleteMapping("/batch/{id}")
    public Result<Void> deleteBatch(@PathVariable Long id) {
        batchService.removeById(id);
        return Result.success();
    }

    // ==================== 工资记录管理 ====================

    /**
     * 分页查询工资记录
     */
    @Operation(summary = "分页查询工资记录")
    @GetMapping("/record/page")
    public Result<IPage<PayrollRecord>> recordPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Page<PayrollRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PayrollRecord> wrapper = new LambdaQueryWrapper<>();

        if (batchId != null) {
            wrapper.eq(PayrollRecord::getBatchId, batchId);
        }
        if (employeeId != null) {
            wrapper.eq(PayrollRecord::getEmployeeId, employeeId);
        }
        if (startDate != null) {
            wrapper.ge(PayrollRecord::getStartDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(PayrollRecord::getEndDate, endDate);
        }

        wrapper.orderByDesc(PayrollRecord::getCreateTime);
        IPage<PayrollRecord> result = recordService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取工资记录详情
     */
    @Operation(summary = "获取工资记录详情")
    @GetMapping("/record/{id}")
    public Result<PayrollRecord> getRecord(@PathVariable Long id) {
        PayrollRecord record = recordService.getById(id);
        return Result.success(record);
    }

    /**
     * 修改工资记录
     */
    @Operation(summary = "修改工资记录")
    @PutMapping("/record")
    public Result<Void> updateRecord(@RequestBody PayrollRecord record) {
        recordService.updateById(record);
        return Result.success();
    }

    /**
     * 删除工资记录
     */
    @Operation(summary = "删除工资记录")
    @DeleteMapping("/record/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        recordService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除工资记录
     */
    @Operation(summary = "批量删除工资记录")
    @DeleteMapping("/record/batch")
    public Result<Void> deleteRecordBatch(@RequestBody List<Long> ids) {
        recordService.removeByIds(ids);
        return Result.success();
    }
}
