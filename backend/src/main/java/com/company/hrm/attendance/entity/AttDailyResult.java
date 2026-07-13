package com.company.hrm.attendance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日考勤结果实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_daily_result")
public class AttDailyResult extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 工作日期
     */
    private LocalDate workDate;

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
     * 命中规则ID
     */
    private Long hitRuleId;

    /**
     * 命中规则名称
     */
    private String hitRuleName;

    /**
     * 是否人工调整: 0-否 1-是
     */
    private Integer isManualAdjusted;

    /**
     * 调整原因
     */
    private String adjustReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 计算时间
     */
    private LocalDateTime calculatedAt;
}
