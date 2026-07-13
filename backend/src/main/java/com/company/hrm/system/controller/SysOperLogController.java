package com.company.hrm.system.controller;

import com.company.hrm.common.Result;
import com.company.hrm.system.entity.SysOperLog;
import com.company.hrm.system.service.ISysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 操作日志 Controller
 */
@Tag(name = "操作日志", description = "系统操作日志管理")
@RestController
@RequestMapping("/system/log")
public class SysOperLogController {
    
    @Autowired
    private ISysOperLogService operLogService;
    
    /**
     * 获取操作日志列表（带分页和条件）
     */
    @Operation(summary = "获取操作日志列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getLogList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String operName,
            @RequestParam(required = false) Integer businessType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        if (title != null) params.put("title", title);
        if (operName != null) params.put("operName", operName);
        if (businessType != null) params.put("businessType", businessType);
        if (status != null) params.put("status", status);
        if (startTime != null) params.put("startTime", startTime);
        if (endTime != null) params.put("endTime", endTime);
        
        Map<String, Object> result = operLogService.getOperLogList(params);
        return Result.success(result);
    }
    
    /**
     * 获取操作日志详情
     */
    @Operation(summary = "获取操作日志详情")
    @GetMapping("/{id}")
    public Result<SysOperLog> getLog(@PathVariable Long id) {
        SysOperLog operLog = operLogService.getById(id);
        return Result.success(operLog);
    }
    
    /**
     * 删除操作日志
     */
    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteLog(@PathVariable Long id) {
        boolean success = operLogService.removeById(id);
        return Result.success(success);
    }
    
    /**
     * 批量删除操作日志
     */
    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteLog(@RequestBody Long[] ids) {
        boolean success = operLogService.removeByIds(java.util.Arrays.asList(ids));
        return Result.success(success);
    }
    
    /**
     * 清空操作日志
     */
    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clear")
    public Result<Boolean> clearLog() {
        boolean success = operLogService.remove(null);
        return Result.success(success);
    }
}
