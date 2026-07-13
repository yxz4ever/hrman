package com.company.hrm.employee.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 员工详情实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_employee_detail")
public class HrEmployeeDetail extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户/员工 ID（关联 sys_user.id）
     */
    private Long userId;

    /**
     * 工号
     */
    private String empNo;

    /**
     * 民族
     */
    private String nation;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 婚姻状况：0-未婚 1-已婚 2-离异 3-丧偶
     */
    private Integer maritalStatus;

    /**
     * 直接上级 ID（关联 sys_user.id）
     */
    private Long superiorId;

    /**
     * 用工形式：1-全职 2-兼职 3-劳务
     */
    private Integer employmentForm;

    /**
     * 转正日期
     */
    private LocalDate regularDate;

    /**
     * 离职日期
     */
    private LocalDate resignDate;

    /**
     * 离职原因
     */
    private String resignReason;

    /**
     * 考勤规则组 ID
     */
    private Long ruleGroupId;

    /**
     * 工资模板 ID
     */
    private Long salaryTemplateId;

    /**
     * 社保政策 ID
     */
    private Long socialPolicyId;

    /**
     * 家庭住址
     */
    private String address;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 社保号
     */
    private String socialSecurityNo;

    /**
     * 公积金账号
     */
    private String fundAccount;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 毕业院校
     */
    private String graduationSchool;

    /**
     * 专业
     */
    private String major;

    /**
     * 毕业日期
     */
    private LocalDate graduationDate;

    /**
     * 证书信息
     */
    private String certificateInfo;

    /**
     * 工作经历
     */
    private String workExperience;

    /**
     * 家庭成员
     */
    private String familyMembers;

    /**
     * 备注
     */
    private String remark;
}
