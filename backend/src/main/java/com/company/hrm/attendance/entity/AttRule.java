package com.company.hrm.attendance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 考勤规则实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_rule")
public class AttRule extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型: 1-固定班制 2-弹性工时 3-按工时制 4-排班制 5-特殊岗位制
     */
    private Integer ruleType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 上班时间
     */
    private LocalTime checkInTime;

    /**
     * 下班时间
     */
    private LocalTime checkOutTime;

    /**
     * 最晚上班时间
     */
    private LocalTime latestCheckInTime;

    /**
     * 最早下班时间
     */
    private LocalTime earliestCheckOutTime;

    /**
     * 要求工作时长(分钟)
     */
    private Integer requiredWorkMinutes;

    /**
     * 允许迟到分钟数
     */
    private Integer allowLateMinutes;

    /**
     * 允许早退分钟数
     */
    private Integer allowEarlyLeaveMinutes;

    /**
     * 休息开始时间
     */
    private LocalTime restStartTime;

    /**
     * 休息结束时间
     */
    private LocalTime restEndTime;

    /**
     * 是否跨天: 0-否 1-是
     */
    private Integer crossDayFlag;

    /**
     * 是否计加班: 0-否 1-是
     */
    private Integer overtimeFlag;

    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;
}
