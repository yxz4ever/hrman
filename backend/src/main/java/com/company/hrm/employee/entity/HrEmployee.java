package com.company.hrm.employee.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_employee")
public class HrEmployee extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工号
     */
    private String empNo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别: 0-女 1-男
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 民族
     */
    private String nation;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 婚姻状况: 0-未婚 1-已婚 2-离异 3-丧偶
     */
    private Integer maritalStatus;

    /**
     * 学历
     */
    private String education;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 岗位ID
     */
    private Long postId;

    /**
     * 职级
     */
    private String jobLevel;

    /**
     * 直接上级ID
     */
    private Long superiorId;

    /**
     * 员工类型: 1-正式 2-试用 3-实习 4-外包
     */
    private Integer employeeType;

    /**
     * 用工形式: 1-全职 2-兼职 3-劳务
     */
    private Integer employmentForm;

    /**
     * 入职日期
     */
    private LocalDate hireDate;

    /**
     * 转正日期
     */
    private LocalDate regularDate;

    /**
     * 在职状态: 0-离职 1-在职 2-停薪留职
     */
    private Integer employeeStatus;

    /**
     * 离职日期
     */
    private LocalDate resignDate;

    /**
     * 离职原因
     */
    private String resignReason;

    /**
     * 考勤规则组ID
     */
    private Long ruleGroupId;

    /**
     * 工资模板ID
     */
    private Long salaryTemplateId;

    /**
     * 社保政策ID
     */
    private Long socialPolicyId;

    /**
     * 系统用户ID
     */
    private Long userId;

    /**
     * 头像URL
     */
    private String avatar;

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
     * 备注
     */
    private String remark;
}
