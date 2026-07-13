package com.company.hrm.attendance.engine;

import com.company.hrm.attendance.entity.AttClockRecord;
import com.company.hrm.attendance.entity.AttRule;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤计算器接口
 */
public interface AttendanceCalculator {

    /**
     * 计算考勤结果
     *
     * @param rule 考勤规则
     * @param clockRecords 打卡记录
     * @param workDate 工作日期
     * @return 考勤结果
     */
    AttendanceResult calculate(AttRule rule, List<AttClockRecord> clockRecords, String workDate);
}
