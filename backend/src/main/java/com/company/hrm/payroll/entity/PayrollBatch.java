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
 * 工资批次实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pay_salary_batch")
public class PayrollBatch extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 批次名称
     */
    private String batchName;

    /**
     * 批次编码
     */
    private String batchCode;

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
     * 员工数量
     */
    private Integer employeeCount;

    /**
     * 工资总额
     */
    private BigDecimal totalSalary;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实发总额
     */
    private BigDecimal netSalaryTotal;

    /**
     * 状态: 0-草稿 1-已锁定 2-已发放
     */
    private Integer status;

    /**
     * 发放日期
     */
    private LocalDate paymentDate;

    /**
     * 制表人
     */
    private String maker;

    /**
     * 审核人
     */
    private String auditor;

    /**
     * 审核时间
     */
    private LocalDate auditTime;

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

    /**
     * 设置创建时间（兼容字段）
     */
    public void setCreateTime(LocalDateTime createTime) {
        setCreatedAt(createTime);
    }

    /**
     * 设置更新时间（兼容字段）
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        setUpdatedAt(updateTime);
    }
}