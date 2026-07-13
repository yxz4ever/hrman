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
 * 固定班制规则计算器
 */
@Component
public class FixedTimeRuleCalculator implements AttendanceCalculator {

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

        // 判断是否迟到
        int lateMinutes = 0;
        if (rule.getCheckInTime() != null && firstClockIn != null) {
            LocalTime actualCheckInTime = firstClockIn.getClockTime().toLocalTime();
            if (actualCheckInTime.isAfter(rule.getCheckInTime())) {
                lateMinutes = (int) ChronoUnit.MINUTES.between(rule.getCheckInTime(), actualCheckInTime);
            }
        }
        result.setLateMinutes(lateMinutes);

        // 判断是否早退
        int earlyLeaveMinutes = 0;
        if (rule.getCheckOutTime() != null && lastClockOut != null) {
            LocalTime actualCheckOutTime = lastClockOut.getClockTime().toLocalTime();
            if (actualCheckOutTime.isBefore(rule.getCheckOutTime())) {
                earlyLeaveMinutes = (int) ChronoUnit.MINUTES.between(actualCheckOutTime, rule.getCheckOutTime());
            }
        }
        result.setEarlyLeaveMinutes(earlyLeaveMinutes);

        // 判断是否通过
        boolean pass = true;
        Integer attendanceStatus = 1; // 正常
        String hitReason = "正常出勤";

        // 检查迟到
        if (rule.getAllowLateMinutes() != null && lateMinutes > rule.getAllowLateMinutes()) {
            pass = false;
            attendanceStatus = 2; // 迟到
            hitReason = String.format("迟到%d分钟", lateMinutes);
        }

        // 检查早退
        if (rule.getAllowEarlyLeaveMinutes() != null && earlyLeaveMinutes > rule.getAllowEarlyLeaveMinutes()) {
            pass = false;
            attendanceStatus = 3; // 早退
            hitReason = String.format("早退%d分钟", earlyLeaveMinutes);
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
