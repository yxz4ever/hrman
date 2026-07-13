package com.company.hrm.payroll.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工资记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pay_salary_detail")
public class PayrollRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 批次ID
     */
    private Long batchId;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 员工工号
     */
    private String empNo;

    /**
     * 员工姓名
     */
    private String empName;

    /**
     * 工资月份
     */
    private LocalDate salaryMonth;

    /**
     * 工资期间开始日期
     */
    private LocalDate startDate;

    /**
     * 工资期间结束日期
     */
    private LocalDate endDate;

    /**
     * 基本工资
     */
    private BigDecimal baseSalary;

    /**
     * 岗位工资
     */
    private BigDecimal postSalary;

    /**
     * 绩效工资
     */
    private BigDecimal performanceSalary;

    /**
     * 津贴补贴
     */
    private BigDecimal allowance;

    /**
     * 加班费
     */
    private BigDecimal overtimeAmount;

    /**
     * 其他应发
     */
    private BigDecimal otherAddition;

    /**
     * 应发合计
     */
    private BigDecimal grossSalary;

    /**
     * 迟到扣款
     */
    private BigDecimal lateDeduction;

    /**
     * 早退扣款
     */
    private BigDecimal earlyLeaveDeduction;

    /**
     * 旷工扣款
     */
    private BigDecimal absentDeduction;

    /**
     * 请假扣款
     */
    private BigDecimal leaveDeduction;

    /**
     * 社保个人承担
     */
    private BigDecimal socialPersonalAmount;

    /**
     * 公积金个人承担
     */
    private BigDecimal housingFundPersonalAmount;

    /**
     * 个人所得税
     */
    private BigDecimal taxAmount;

    /**
     * 其他扣款
     */
    private BigDecimal otherDeduction;

    /**
     * 扣款合计
     */
    private BigDecimal totalDeduction;

    /**
     * 实发工资
     */
    private BigDecimal netSalary;

    /**
     * 状态: 0-草稿 1-已确认
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间（兼容字段）
     */
    public LocalDateTime getCreateTime() {
        return getCreatedAt();
    }

    /**
     * 更新时间（兼容字段）
     */
    public LocalDateTime getUpdateTime() {
        return getUpdatedAt();
    }
}