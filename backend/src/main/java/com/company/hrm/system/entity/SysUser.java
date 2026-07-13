package com.company.hrm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 用户/员工实体（整合用户和员工）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（可选，系统用户必填）
     */
    private String password;

    /**
     * 真实姓名/员工姓名
     */
    private String realName;

    /**
     * 性别：0-女 1-男
     */
    private Integer gender;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 入职日期
     */
    private LocalDate hireDate;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 用户类型：1-系统用户 2-普通员工
     */
    private Integer userType;

    /**
     * 部门 ID
     */
    private Long deptId;

    /**
     * 岗位 ID
     */
    private Long postId;

    /**
     * 员工类型：1-正式 2-试用 3-实习 4-外包
     */
    private Integer employeeType;

    /**
     * 在职状态：0-离职 1-在职 2-停薪留职
     */
    private Integer employeeStatus;

    /**
     * 职级
     */
    private String jobLevel;

    /**
     * 学历
     */
    private String education;

    /**
     * 最后登录时间
     */
    private String lastLoginTime;

    /**
     * 最后登录 IP
     */
    private String lastLoginIp;
}
