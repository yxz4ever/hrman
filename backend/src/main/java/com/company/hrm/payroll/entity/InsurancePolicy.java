package com.company.hrm.payroll.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 社保政策实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ins_policy")
public class InsurancePolicy extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 政策名称
     */
    private String policyName;

    /**
     * 政策编码
     */
    private String policyCode;

    /**
     * 保险类型
     */
    private String insuranceType;

    /**
     * 参保城市
     */
    private String city;

    /**
     * 养老保险企业比例
     */
    private BigDecimal pensionCompanyRate;

    /**
     * 养老保险个人比例
     */
    private BigDecimal pensionPersonalRate;

    /**
     * 医疗保险企业比例
     */
    private BigDecimal medicalCompanyRate;

    /**
     * 医疗保险个人比例
     */
    private BigDecimal medicalPersonalRate;

    /**
     * 失业保险企业比例
     */
    private BigDecimal unemploymentCompanyRate;

    /**
     * 失业保险个人比例
     */
    private BigDecimal unemploymentPersonalRate;

    /**
     * 工伤保险企业比例
     */
    private BigDecimal injuryCompanyRate;

    /**
     * 生育保险企业比例
     */
    private BigDecimal maternityCompanyRate;

    /**
     * 公积金企业比例
     */
    private BigDecimal housingFundCompanyRate;

    /**
     * 公积金个人比例
     */
    private BigDecimal housingFundPersonalRate;

    /**
     * 最低基数
     */
    private BigDecimal minBase;

    /**
     * 最高基数
     */
    private BigDecimal maxBase;

    /**
     * 生效日期
     */
    private String effectiveDate;

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