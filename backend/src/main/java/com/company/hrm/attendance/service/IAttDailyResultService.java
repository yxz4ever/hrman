package com.company.hrm.attendance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.hrm.attendance.entity.AttDailyResult;

import java.util.List;

/**
 * 考勤日结果服务接口
 */
public interface IAttDailyResultService extends IService<AttDailyResult> {

    /**
     * 计算员工某日的考勤结果
     */
    void calculateDailyResult(Long employeeId, String workDate);

    /**
     * 批量计算考勤结果
     */
    void batchCalculateDailyResult(String workDate);

    /**
     * 获取员工某月的考勤结果
     */
    List<AttDailyResult> getMonthlyResults(Long employeeId, String yearMonth);
}