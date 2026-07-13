package com.company.hrm.attendance.engine.impl;

import com.company.hrm.attendance.entity.AttClockRecord;
import com.company.hrm.attendance.entity.AttRule;
import com.company.hrm.attendance.engine.AttendanceCalculator;
import com.company.hrm.attendance.engine.AttendanceResult;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 弹性工时规则计算器
 */
@Component
public class FlexibleRuleCalculator implements AttendanceCalculator {

    @Override
    public AttendanceResult calculate(AttRule rule, List<AttClockRecord> clockRecords, String workDate) {
        AttendanceResult result = new AttendanceResult();

        if (clockRecords == null || clockRecords.isEmpty()) {
            result.setPass(false);
            result.setAttendanceStatus(5); // 旷工
            result.setHitReason("无打卡记录");
            return result;
        }

        // 获取首次上班打卡和最后下班打卡
        AttClockRecord firstClockIn = clockRecords.stream()
                .min((r1, r2) -> r1.getClockTime().compareTo(r2.getClockTime()))
                .orElse(null);
        AttClockRecord lastClockOut = clockRecords.stream()
                .max((r1, r2) -> r1.getClockTime().compareTo(r2.getClockTime()))
                .orElse(null);

        if (firstClockIn != null) {
            result.setFirstClockIn(firstClockIn.getClockTime());
        }
        if (lastClockOut != null) {
            result.setLastClockOut(lastClockOut.getClockTime());
        }

        // 计算工作时长
        int workMinutes = 0;
        if (firstClockIn != null && lastClockOut != null) {
            workMinutes = (int) ChronoUnit.MINUTES.between(firstClockIn.getClockTime(), lastClockOut.getClockTime());
        }
        result.setWorkMinutes(workMinutes);

        // 判断是否迟到（最晚上班时间）
        int lateMinutes = 0;
        if (rule.getLatestCheckInTime() != null && firstClockIn != null) {
            LocalTime actualCheckInTime = firstClockIn.getClockTime().toLocalTime();
            if (actualCheckInTime.isAfter(rule.getLatestCheckInTime())) {
                lateMinutes = (int) ChronoUnit.MINUTES.between(rule.getLatestCheckInTime(), actualCheckInTime);
            }
        }
        result.setLateMinutes(lateMinutes);

        // 判断是否早退（最早下班时间）
        int earlyLeaveMinutes = 0;
        if (rule.getEarliestCheckOutTime() != null && lastClockOut != null) {
            LocalTime actualCheckOutTime = lastClockOut.getClockTime().toLocalTime();
            if (actualCheckOutTime.isBefore(rule.getEarliestCheckOutTime())) {
                earlyLeaveMinutes = (int) ChronoUnit.MINUTES.between(actualCheckOutTime, rule.getEarliestCheckOutTime());
            }
        }
        result.setEarlyLeaveMinutes(earlyLeaveMinutes);

        // 判断是否通过
        boolean pass = true;
        Integer attendanceStatus = 1; // 正常
        String hitReason = "弹性工时正常出勤";

        // 检查最晚上班时间
        if (rule.getLatestCheckInTime() != null && lateMinutes > 0) {
            pass = false;
            attendanceStatus = 2; // 迟到
            hitReason = String.format("超过最晚上班时间%d分钟", lateMinutes);
        }

        // 检查最早下班时间
        if (rule.getEarliestCheckOutTime() != null && earlyLeaveMinutes > 0) {
            pass = false;
            attendanceStatus = 3; // 早退
            hitReason = String.format("早于最早下班时间%d分钟", earlyLeaveMinutes);
        }

        // 检查工作时长
        if (rule.getRequiredWorkMinutes() != null && workMinutes < rule.getRequiredWorkMinutes()) {
            pass = false;
            attendanceStatus = 5; // 旷工
            hitReason = String.format("工作时长不足，实际%d分钟，要求%d分钟", workMinutes, rule.getRequiredWorkMinutes());
        }

        result.setPass(pass);
        result.setAttendanceStatus(attendanceStatus);
        result.setHitRule(rule);
        result.setHitReason(hitReason);

        return result;
    }
}
