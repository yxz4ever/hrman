package com.company.hrm.payroll.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 工资模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pay_salary_template")
public class PayrollTemplate extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编码
     */
    private String templateCode;

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
     * 餐补
     */
    private BigDecimal mealAllowance;

    /**
     * 交通补
     */
    private BigDecimal trafficAllowance;

    /**
     * 通讯补
     */
    private BigDecimal communicationAllowance;

    /**
     * 其他补贴
     */
    private BigDecimal otherAllowance;

    /**
     * 社保个人承担比例
     */
    private BigDecimal socialPersonalRate;

    /**
     * 公积金个人承担比例
     */
    private BigDecimal housingFundPersonalRate;

    /**
     * 状态: 0-禁用 1-启用
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