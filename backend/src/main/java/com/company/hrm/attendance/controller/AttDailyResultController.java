package com.company.hrm.attendance.controller;

import com.company.hrm.attendance.entity.AttDailyResult;
import com.company.hrm.attendance.engine.AttendanceEngine;
import com.company.hrm.attendance.mapper.AttDailyResultMapper;
import com.company.hrm.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/attendance/daily-results")
public class AttDailyResultController {
    
    @Autowired
    private AttendanceEngine attendanceEngine;
    
    @Autowired
    private AttDailyResultMapper dailyResultMapper;
    
    /**
     * 计算员工某日的考勤结果
     */
    @PostMapping("/calculate")
    public Result<AttDailyResult> calculateDailyAttendance(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate workDate) {
        
        AttDailyResult result = attendanceEngine.calculateDailyAttendance(employeeId, workDate);
        
        // 保存或更新考勤结果
        AttDailyResult existResult = dailyResultMapper.selectByEmployeeAndDate(employeeId, workDate);
        if (existResult != null) {
            result.setId(existResult.getId());
            dailyResultMapper.updateById(result);
        } else {
            dailyResultMapper.insert(result);
        }
        
        return Result.success(result);
    }
    
    /**
     * 查询员工某日的考勤结果
     */
    @GetMapping
    public Result<AttDailyResult> getDailyResult(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate workDate) {
        
        AttDailyResult result = dailyResultMapper.selectByEmployeeAndDate(employeeId, workDate);
        return Result.success(result);
    }
}