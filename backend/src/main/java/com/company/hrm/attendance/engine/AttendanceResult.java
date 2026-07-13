package com.company.hrm.attendance.engine;

import com.company.hrm.attendance.entity.AttDailyResult;
import com.company.hrm.attendance.entity.AttRule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考勤计算结果
 */
public class AttendanceResult {

    /**
     * 是否通过
     */
    private boolean pass;

    /**
     * 考勤状态: 1-正常 2-迟到 3-早退 4-缺卡 5-旷工 6-请假 7-出差 8-外勤 9-加班
     */
    private Integer attendanceStatus;

    /**
     * 首次上班打卡
     */
    private LocalDateTime firstClockIn;

    /**
     * 最后下班打卡
     */
    private LocalDateTime lastClockOut;

    /**
     * 工作时长(分钟)
     */
    private Integer workMinutes;

    /**
     * 迟到分钟数
     */
    private Integer lateMinutes;

    /**
     * 早退分钟数
     */
    private Integer earlyLeaveMinutes;

    /**
     * 缺勤分钟数
     */
    private Integer absentMinutes;

    /**
     * 加班分钟数
     */
    private Integer overtimeMinutes;

    /**
     * 命中的规则
     */
    private AttRule hitRule;

    /**
     * 命中原因
     */
    private String hitReason;

    public AttendanceResult() {
    }

    public AttendanceResult(boolean pass, Integer attendanceStatus, String hitReason) {
        this.pass = pass;
        this.attendanceStatus = attendanceStatus;
        this.hitReason = hitReason;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public Integer getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(Integer attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public LocalDateTime getFirstClockIn() {
        return firstClockIn;
    }

    public void setFirstClockIn(LocalDateTime firstClockIn) {
        this.firstClockIn = firstClockIn;
    }

    public LocalDateTime getLastClockOut() {
        return lastClockOut;
    }

    public void setLastClockOut(LocalDateTime lastClockOut) {
        this.lastClockOut = lastClockOut;
    }

    public Integer getWorkMinutes() {
        return workMinutes;
    }

    public void setWorkMinutes(Integer workMinutes) {
        this.workMinutes = workMinutes;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public Integer getEarlyLeaveMinutes() {
        return earlyLeaveMinutes;
    }

    public void setEarlyLeaveMinutes(Integer earlyLeaveMinutes) {
        this.earlyLeaveMinutes = earlyLeaveMinutes;
    }

    public Integer getAbsentMinutes() {
        return absentMinutes;
    }

    public void setAbsentMinutes(Integer absentMinutes) {
        this.absentMinutes = absentMinutes;
    }

    public Integer getOvertimeMinutes() {
        return overtimeMinutes;
    }

    public void setOvertimeMinutes(Integer overtimeMinutes) {
        this.overtimeMinutes = overtimeMinutes;
    }

    public AttRule getHitRule() {
        return hitRule;
    }

    public void setHitRule(AttRule hitRule) {
        this.hitRule = hitRule;
    }

    public String getHitReason() {
        return hitReason;
    }

    public void setHitReason(String hitReason) {
        this.hitReason = hitReason;
    }

    /**
     * 获取状态描述
     */
    public String getStatus() {
        if (attendanceStatus == null) {
            return "未知";
        }
        switch (attendanceStatus) {
            case 1: return "正常";
            case 2: return "迟到";
            case 3: return "早退";
            case 4: return "缺卡";
            case 5: return "旷工";
            case 6: return "请假";
            case 7: return "出差";
            case 8: return "外勤";
            case 9: return "加班";
            default: return "未知";
        }
    }

    /**
     * 获取规则说明
     */
    public String getExplanation() {
        return hitReason;
    }

    /**
     * 获取命中规则ID
     */
    public Long getHitRuleId() {
        return hitRule != null ? hitRule.getId() : null;
    }

    /**
     * 创建失败结果
     */
    public static AttendanceResult fail(String status, String explanation) {
        AttendanceResult result = new AttendanceResult();
        result.setPass(false);
        result.setHitReason(explanation);
        // 根据状态字符串设置对应的status码
        switch (status) {
            case "异常":
                result.setAttendanceStatus(5); // 旷工
                break;
            case "迟到":
                result.setAttendanceStatus(2);
                break;
            case "早退":
                result.setAttendanceStatus(3);
                break;
            case "缺卡":
                result.setAttendanceStatus(4);
                break;
            default:
                result.setAttendanceStatus(5); // 默认为旷工
        }
        return result;
    }
}
