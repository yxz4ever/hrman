package com.company.hrm.attendance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.attendance.entity.AttClockRecord;
import com.company.hrm.attendance.service.IAttClockRecordService;
import com.company.hrm.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 打卡记录管理
 */
@Tag(name = "打卡记录管理")
@RestController
@RequestMapping("/attendance/clock-record")
public class AttClockRecordController {

    @Autowired
    private IAttClockRecordService clockRecordService;

    /**
     * 分页查询打卡记录
     */
    @Operation(summary = "分页查询打卡记录")
    @GetMapping("/page")
    public Result<IPage<AttClockRecord>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Page<AttClockRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AttClockRecord> wrapper = new LambdaQueryWrapper<>();

        if (employeeId != null) {
            wrapper.eq(AttClockRecord::getEmployeeId, employeeId);
        }
        if (startDate != null) {
            wrapper.ge(AttClockRecord::getClockTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(AttClockRecord::getClockTime, endDate.atTime(23, 59, 59));
        }

        wrapper.orderByDesc(AttClockRecord::getClockTime);
        IPage<AttClockRecord> result = clockRecordService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 查询员工打卡记录
     */
    @Operation(summary = "查询员工打卡记录")
    @GetMapping("/employee/{employeeId}")
    public Result<List<AttClockRecord>> getEmployeeRecords(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        LambdaQueryWrapper<AttClockRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttClockRecord::getEmployeeId, employeeId);
        wrapper.ge(AttClockRecord::getClockTime, date.atStartOfDay());
        wrapper.le(AttClockRecord::getClockTime, date.atTime(23, 59, 59));
        wrapper.orderByAsc(AttClockRecord::getClockTime);

        List<AttClockRecord> records = clockRecordService.list(wrapper);
        return Result.success(records);
    }

    /**
     * 手动添加打卡记录
     */
    @Operation(summary = "手动添加打卡记录")
    @PostMapping
    public Result<Void> add(@RequestBody AttClockRecord record) {
        record.setCreateTime(LocalDateTime.now());
        clockRecordService.save(record);
        return Result.success();
    }

    /**
     * 修改打卡记录
     */
    @Operation(summary = "修改打卡记录")
    @PutMapping
    public Result<Void> update(@RequestBody AttClockRecord record) {
        clockRecordService.updateById(record);
        return Result.success();
    }

    /**
     * 删除打卡记录
     */
    @Operation(summary = "删除打卡记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        clockRecordService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除打卡记录
     */
    @Operation(summary = "批量删除打卡记录")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        clockRecordService.removeByIds(ids);
        return Result.success();
    }
}
