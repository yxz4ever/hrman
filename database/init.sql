-- =============================================
-- 人力资源管理系统数据库初始化脚本
-- 数据库：hrm_db
-- 字符集：utf8mb4
-- 创建日期：2026-07-08
-- 最后更新：2026-07-13
-- 版本：1.0.0
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `hrm_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `hrm_db`;

-- =============================================
-- 第一部分：系统权限相关表
-- =============================================

-- 1.1 用户/员工主表（整合用户和员工）
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户/员工 ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) DEFAULT NULL COMMENT '密码（可选，系统用户必填）',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名/员工姓名',
    `gender` TINYINT DEFAULT NULL COMMENT '性别：0-女 1-男',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
    `hire_date` DATE DEFAULT NULL COMMENT '入职日期',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `user_type` TINYINT DEFAULT 1 COMMENT '用户类型：1-系统用户 2-普通员工',
    `dept_id` BIGINT DEFAULT NULL COMMENT '部门 ID',
    `post_id` BIGINT DEFAULT NULL COMMENT '岗位 ID',
    `employee_type` TINYINT DEFAULT NULL COMMENT '员工类型：1-正式 2-试用 3-实习 4-外包',
    `employee_status` TINYINT DEFAULT 1 COMMENT '在职状态：0-离职 1-在职 2-停薪留职',
    `job_level` VARCHAR(20) DEFAULT NULL COMMENT '职级',
    `education` VARCHAR(20) DEFAULT NULL COMMENT '学历',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录 IP',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_id_card` (`id_card`),
    UNIQUE KEY `uk_mobile` (`mobile`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_mobile` (`mobile`),
    KEY `idx_status` (`status`),
    KEY `idx_employee_status` (`employee_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户/员工主表';

-- 1.2 角色表
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 1.3 用户角色关联表
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `role_id` BIGINT NOT NULL COMMENT '角色 ID',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 1.4 菜单权限表
CREATE TABLE `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单 ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单 ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `menu_type` TINYINT NOT NULL COMMENT '菜单类型：1-目录 2-菜单 3-按钮',
    `menu_code` VARCHAR(100) DEFAULT NULL COMMENT '菜单编码',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    `component` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '菜单图标',
    `permission` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '是否显示：0-隐藏 1-显示',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_menu_type` (`menu_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 1.5 角色菜单关联表
CREATE TABLE `sys_role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `role_id` BIGINT NOT NULL COMMENT '角色 ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单 ID',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- 1.6 部门表
CREATE TABLE `hr_department` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门 ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父部门 ID',
    `dept_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
    `dept_code` VARCHAR(50) DEFAULT NULL COMMENT '部门编码',
    `leader_id` BIGINT DEFAULT NULL COMMENT '负责人 ID',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_dept_code` (`dept_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 1.7 岗位表
CREATE TABLE `hr_position` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '岗位 ID',
    `post_code` VARCHAR(50) NOT NULL COMMENT '岗位编码',
    `post_name` VARCHAR(50) NOT NULL COMMENT '岗位名称',
    `position_name` VARCHAR(50) DEFAULT NULL COMMENT '职位名称',
    `post_level` VARCHAR(20) DEFAULT NULL COMMENT '岗位职级',
    `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门 ID',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '岗位描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_code` (`post_code`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位表';

-- =============================================
-- 第二部分：员工详情表
-- =============================================

-- 2.1 员工详情表（存储员工详细信息）
CREATE TABLE `hr_employee_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '详情 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户/员工 ID（关联 sys_user.id）',
    `emp_no` VARCHAR(20) DEFAULT NULL COMMENT '工号',
    `nation` VARCHAR(20) DEFAULT NULL COMMENT '民族',
    `native_place` VARCHAR(100) DEFAULT NULL COMMENT '籍贯',
    `marital_status` TINYINT DEFAULT NULL COMMENT '婚姻状况：0-未婚 1-已婚 2-离异 3-丧偶',
    `superior_id` BIGINT DEFAULT NULL COMMENT '直接上级 ID（关联 sys_user.id）',
    `employment_form` TINYINT DEFAULT NULL COMMENT '用工形式：1-全职 2-兼职 3-劳务',
    `regular_date` DATE DEFAULT NULL COMMENT '转正日期',
    `resign_date` DATE DEFAULT NULL COMMENT '离职日期',
    `resign_reason` VARCHAR(200) DEFAULT NULL COMMENT '离职原因',
    `rule_group_id` BIGINT DEFAULT NULL COMMENT '考勤规则组 ID',
    `salary_template_id` BIGINT DEFAULT NULL COMMENT '工资模板 ID',
    `social_policy_id` BIGINT DEFAULT NULL COMMENT '社保政策 ID',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '家庭住址',
    `emergency_contact` VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
    `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
    `bank_name` VARCHAR(100) DEFAULT NULL COMMENT '开户银行',
    `bank_account` VARCHAR(50) DEFAULT NULL COMMENT '银行账号',
    `social_security_no` VARCHAR(50) DEFAULT NULL COMMENT '社保号',
    `fund_account` VARCHAR(50) DEFAULT NULL COMMENT '公积金账号',
    `political_status` VARCHAR(20) DEFAULT NULL COMMENT '政治面貌',
    `graduation_school` VARCHAR(100) DEFAULT NULL COMMENT '毕业院校',
    `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
    `graduation_date` DATE DEFAULT NULL COMMENT '毕业日期',
    `certificate_info` TEXT DEFAULT NULL COMMENT '证书信息',
    `work_experience` TEXT DEFAULT NULL COMMENT '工作经历',
    `family_members` TEXT DEFAULT NULL COMMENT '家庭成员',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_emp_no` (`emp_no`),
    KEY `idx_superior_id` (`superior_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工详情表';

-- =============================================
-- 第三部分：考勤相关表
-- =============================================

-- 3.1 考勤规则组表
CREATE TABLE `att_rule_group` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则组 ID',
    `group_code` VARCHAR(50) NOT NULL COMMENT '规则组编码',
    `group_name` VARCHAR(50) NOT NULL COMMENT '规则组名称',
    `applicable_scope` TINYINT NOT NULL COMMENT '适用范围：1-全部员工 2-指定部门 3-指定岗位 4-指定员工',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `effective_start_date` DATE NOT NULL COMMENT '生效开始日期',
    `effective_end_date` DATE DEFAULT NULL COMMENT '生效结束日期',
    `priority` INT DEFAULT 0 COMMENT '优先级',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_group_code` (`group_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤规则组表';

-- 3.2 考勤规则表
CREATE TABLE `att_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则 ID',
    `rule_code` VARCHAR(50) NOT NULL COMMENT '规则编码',
    `rule_name` VARCHAR(50) NOT NULL COMMENT '规则名称',
    `rule_type` TINYINT NOT NULL COMMENT '规则类型：1-固定班制 2-弹性工时 3-按工时制 4-排班制 5-特殊岗位制',
    `priority` INT DEFAULT 0 COMMENT '优先级',
    `check_in_time` TIME DEFAULT NULL COMMENT '上班时间',
    `check_out_time` TIME DEFAULT NULL COMMENT '下班时间',
    `latest_check_in_time` TIME DEFAULT NULL COMMENT '最晚上班时间',
    `earliest_check_out_time` TIME DEFAULT NULL COMMENT '最早下班时间',
    `required_work_minutes` INT DEFAULT NULL COMMENT '要求工作时长 (分钟)',
    `allow_late_minutes` INT DEFAULT 0 COMMENT '允许迟到分钟数',
    `allow_early_leave_minutes` INT DEFAULT 0 COMMENT '允许早退分钟数',
    `rest_start_time` TIME DEFAULT NULL COMMENT '休息开始时间',
    `rest_end_time` TIME DEFAULT NULL COMMENT '休息结束时间',
    `cross_day_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '是否跨天：0-否 1-是',
    `overtime_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '是否计加班：0-否 1-是',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rule_code` (`rule_code`),
    KEY `idx_rule_type` (`rule_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤规则表';

-- 3.3 原始打卡表
CREATE TABLE `att_clock_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '打卡 ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工 ID',
    `work_date` DATE NOT NULL COMMENT '工作日期',
    `clock_time` DATETIME NOT NULL COMMENT '打卡时间',
    `clock_type` TINYINT NOT NULL COMMENT '打卡类型：1-上班 2-下班',
    `source_type` TINYINT NOT NULL COMMENT '来源类型：1-APP 2-打卡机 3-PC 4-其他',
    `device_no` VARCHAR(50) DEFAULT NULL COMMENT '设备编号',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '打卡位置',
    `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
    `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
    `photo_url` VARCHAR(255) DEFAULT NULL COMMENT '照片 URL',
    `is_valid` TINYINT NOT NULL DEFAULT 1 COMMENT '是否有效：0-无效 1-有效',
    `raw_payload` TEXT DEFAULT NULL COMMENT '原始数据',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_work_date` (`work_date`),
    KEY `idx_clock_time` (`clock_time`),
    KEY `idx_employee_date` (`employee_id`, `work_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='原始打卡表';

-- 3.4 每日考勤结果表
CREATE TABLE `att_daily_result` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '结果 ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工 ID',
    `work_date` DATE NOT NULL COMMENT '工作日期',
    `attendance_status` TINYINT NOT NULL COMMENT '考勤状态：1-正常 2-迟到 3-早退 4-缺卡 5-旷工 6-请假 7-出差 8-外勤 9-加班',
    `first_clock_in` DATETIME DEFAULT NULL COMMENT '首次上班打卡',
    `last_clock_out` DATETIME DEFAULT NULL COMMENT '最后下班打卡',
    `work_minutes` INT DEFAULT 0 COMMENT '工作时长 (分钟)',
    `late_minutes` INT DEFAULT 0 COMMENT '迟到分钟数',
    `early_leave_minutes` INT DEFAULT 0 COMMENT '早退分钟数',
    `absent_minutes` INT DEFAULT 0 COMMENT '缺勤分钟数',
    `overtime_minutes` INT DEFAULT 0 COMMENT '加班分钟数',
    `hit_rule_id` BIGINT DEFAULT NULL COMMENT '命中规则 ID',
    `hit_rule_name` VARCHAR(50) DEFAULT NULL COMMENT '命中规则名称',
    `is_manual_adjusted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否人工调整：0-否 1-是',
    `adjust_reason` VARCHAR(200) DEFAULT NULL COMMENT '调整原因',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `calculated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_date` (`employee_id`, `work_date`),
    KEY `idx_work_date` (`work_date`),
    KEY `idx_attendance_status` (`attendance_status`),
    KEY `idx_hit_rule_id` (`hit_rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日考勤结果表';

-- =============================================
-- 第四部分：薪资相关表
-- =============================================

-- 4.1 工资模板表
CREATE TABLE `pay_salary_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板 ID',
    `template_code` VARCHAR(50) NOT NULL COMMENT '模板编码',
    `template_name` VARCHAR(50) NOT NULL COMMENT '模板名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`template_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工资模板表';

-- 4.2 工资批次表
CREATE TABLE `pay_salary_batch` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '批次 ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '批次编号',
    `batch_name` VARCHAR(100) NOT NULL COMMENT '批次名称',
    `salary_month` VARCHAR(7) NOT NULL COMMENT '工资月份 (YYYY-MM)',
    `batch_status` TINYINT NOT NULL DEFAULT 0 COMMENT '批次状态：0-草稿 1-计算中 2-已计算 3-已锁定 4-已发放',
    `total_count` INT DEFAULT 0 COMMENT '总人数',
    `total_amount` DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    `calculation_time` DATETIME DEFAULT NULL COMMENT '计算时间',
    `lock_time` DATETIME DEFAULT NULL COMMENT '锁定时间',
    `pay_time` DATETIME DEFAULT NULL COMMENT '发放时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_batch_no` (`batch_no`),
    KEY `idx_salary_month` (`salary_month`),
    KEY `idx_batch_status` (`batch_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工资批次表';

-- 4.3 工资明细表
CREATE TABLE `pay_salary_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细 ID',
    `batch_id` BIGINT NOT NULL COMMENT '批次 ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工 ID',
    `salary_month` VARCHAR(7) NOT NULL COMMENT '工资月份 (YYYY-MM)',
    `base_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '基本工资',
    `post_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '岗位工资',
    `performance_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '绩效工资',
    `allowance_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '津贴补贴',
    `overtime_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '加班费',
    `attendance_deduction` DECIMAL(10,2) DEFAULT 0 COMMENT '考勤扣款',
    `social_personal_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '社保个人',
    `housing_fund_personal_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '公积金个人',
    `tax_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '个税',
    `other_deduction` DECIMAL(10,2) DEFAULT 0 COMMENT '其他扣款',
    `gross_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '应发工资',
    `net_salary` DECIMAL(10,2) DEFAULT 0 COMMENT '实发工资',
    `detail_status` TINYINT NOT NULL DEFAULT 0 COMMENT '明细状态：0-草稿 1-已计算 2-已发放',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_batch_employee` (`batch_id`, `employee_id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_salary_month` (`salary_month`),
    KEY `idx_detail_status` (`detail_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工资明细表';

-- =============================================
-- 第五部分：社保相关表
-- =============================================

-- 5.1 社保政策表
CREATE TABLE `ins_policy` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '政策 ID',
    `policy_code` VARCHAR(50) NOT NULL COMMENT '政策编码',
    `policy_name` VARCHAR(50) NOT NULL COMMENT '政策名称',
    `city` VARCHAR(50) NOT NULL COMMENT '参保城市',
    `policy_version` VARCHAR(20) NOT NULL COMMENT '政策版本',
    `effective_start_date` DATE NOT NULL COMMENT '生效开始日期',
    `effective_end_date` DATE DEFAULT NULL COMMENT '生效结束日期',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_policy_code` (`policy_code`),
    KEY `idx_city` (`city`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社保政策表';

-- 5.2 员工社保账户表
CREATE TABLE `ins_employee_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '账户 ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工 ID',
    `policy_id` BIGINT NOT NULL COMMENT '社保政策 ID',
    `social_insurance_no` VARCHAR(50) DEFAULT NULL COMMENT '社保号码',
    `housing_fund_no` VARCHAR(50) DEFAULT NULL COMMENT '公积金号码',
    `social_base` DECIMAL(10,2) DEFAULT 0 COMMENT '社保缴费基数',
    `housing_fund_base` DECIMAL(10,2) DEFAULT 0 COMMENT '公积金缴费基数',
    `social_company_rate` DECIMAL(5,4) DEFAULT 0 COMMENT '社保公司比例',
    `social_personal_rate` DECIMAL(5,4) DEFAULT 0 COMMENT '社保个人比例',
    `housing_company_rate` DECIMAL(5,4) DEFAULT 0 COMMENT '公积金公司比例',
    `housing_personal_rate` DECIMAL(5,4) DEFAULT 0 COMMENT '公积金个人比例',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `effective_start_date` DATE DEFAULT NULL COMMENT '生效开始日期',
    `effective_end_date` DATE DEFAULT NULL COMMENT '生效结束日期',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_policy` (`employee_id`, `policy_id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_policy_id` (`policy_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工社保账户表';

-- 5.3 月度社保明细表
CREATE TABLE `ins_monthly_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细 ID',
    `account_id` BIGINT NOT NULL COMMENT '社保账户 ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工 ID',
    `policy_id` BIGINT NOT NULL COMMENT '社保政策 ID',
    `salary_month` VARCHAR(7) NOT NULL COMMENT '社保月份 (YYYY-MM)',
    `social_base` DECIMAL(10,2) DEFAULT 0 COMMENT '社保基数',
    `housing_fund_base` DECIMAL(10,2) DEFAULT 0 COMMENT '公积金基数',
    `social_company_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '社保公司金额',
    `social_personal_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '社保个人金额',
    `housing_company_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '公积金公司金额',
    `housing_personal_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '公积金个人金额',
    `total_company_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '公司总金额',
    `total_personal_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '个人总金额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-草稿 1-已确认',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account_month` (`account_id`, `salary_month`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_salary_month` (`salary_month`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='月度社保明细表';

-- =============================================
-- 第六部分：字典管理相关表
-- =============================================

-- 6.1 字典类型表
CREATE TABLE `sys_dict_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典类型 ID',
    `dict_code` VARCHAR(50) NOT NULL COMMENT '字典编码',
    `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '字典描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- 6.2 字典数据表
CREATE TABLE `sys_dict_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典数据 ID',
    `dict_type_id` BIGINT NOT NULL COMMENT '字典类型 ID',
    `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
    `dict_value` VARCHAR(200) NOT NULL COMMENT '字典值',
    `sort` INT DEFAULT 0 COMMENT '字典排序',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type_id` (`dict_type_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- =============================================
-- 第七部分：操作日志表
-- =============================================

-- 7.1 操作日志表
CREATE TABLE `sys_oper_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
    `title` VARCHAR(255) DEFAULT NULL COMMENT '模块标题',
    `business_type` INT DEFAULT 0 COMMENT '业务类型：0-其它 1-新增 2-修改 3-删除 4-导出 5-导入',
    `method` VARCHAR(255) DEFAULT NULL COMMENT '方法名称',
    `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方式：GET/POST/PUT/DELETE',
    `operator_type` INT DEFAULT 0 COMMENT '操作类型：0-其它 1-后台用户 2-手机端用户',
    `oper_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人员',
    `dept_name` VARCHAR(50) DEFAULT NULL COMMENT '部门名称',
    `oper_url` VARCHAR(255) DEFAULT NULL COMMENT '请求 URL',
    `oper_ip` VARCHAR(128) DEFAULT NULL COMMENT '主机地址',
    `oper_location` VARCHAR(255) DEFAULT NULL COMMENT '操作地点',
    `param` TEXT COMMENT '请求参数',
    `json_result` TEXT COMMENT '返回数据',
    `status` INT DEFAULT 0 COMMENT '操作状态：0-正常 1-异常',
    `error_msg` TEXT COMMENT '错误消息',
    `oper_time` DATETIME DEFAULT NULL COMMENT '操作时间',
    `cost_time` BIGINT DEFAULT 0 COMMENT '消耗时间 (毫秒)',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_oper_name` (`oper_name`),
    KEY `idx_oper_time` (`oper_time`),
    KEY `idx_status` (`status`),
    KEY `idx_business_type` (`business_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =============================================
-- 第八部分：初始化数据
-- =============================================

-- 8.1 插入默认用户（密码：admin123）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `email`, `mobile`, `status`, `dept_id`) VALUES
('admin', '$2a$10$B4b2kRCJbV.YdLJUwtPzwOv3EGWCvPtqK1EnGaEJGm/9.u/4mwyZa', '系统管理员', 'admin@example.com', '13800138000', 1, 1);

-- 8.2 插入默认角色
INSERT INTO `sys_role` (`role_code`, `role_name`, `description`, `status`, `sort_order`) VALUES
('ROLE_ADMIN', '系统管理员', '拥有系统所有权限', 1, 1);

-- 8.3 插入用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

-- 8.4 插入默认部门
INSERT INTO `sys_dept` (`parent_id`, `dept_name`, `dept_code`, `phone`, `email`, `sort_order`, `status`) VALUES
(0, '总公司', 'HQ', '010-12345678', 'hq@example.com', 1, 1),
(1, '技术部', 'TECH', '010-12345679', 'tech@example.com', 1, 1),
(1, '销售部', 'SALES', '010-12345680', 'sales@example.com', 2, 1),
(1, '市场部', 'MARKET', '010-12345681', 'market@example.com', 3, 1),
(1, '人事部', 'HR', '010-12345682', 'hr@example.com', 4, 1),
(1, '财务部', 'FINANCE', '010-12345683', 'finance@example.com', 5, 1),
(1, '行政部', 'ADMIN', '010-12345684', 'admin@example.com', 6, 1);

-- 8.5 插入默认岗位
INSERT INTO `sys_post` (`post_code`, `post_name`, `post_level`, `dept_id`, `description`, `sort_order`, `status`) VALUES
('CEO', '首席执行官', 'L1', 1, '公司最高管理者', 1, 1),
('CTO', '首席技术官', 'L2', 2, '技术部门负责人', 1, 1),
('SALES_MANAGER', '销售经理', 'L3', 3, '销售部门负责人', 1, 1),
('MARKET_MANAGER', '市场经理', 'L3', 4, '市场部门负责人', 1, 1),
('HR_MANAGER', '人事经理', 'L3', 5, '人事部门负责人', 1, 1),
('FINANCE_MANAGER', '财务经理', 'L3', 6, '财务部门负责人', 1, 1),
('ADMIN_MANAGER', '行政经理', 'L3', 7, '行政部门负责人', 1, 1),
('SENIOR_ENGINEER', '高级工程师', 'L4', 2, '高级技术岗位', 2, 1),
('ENGINEER', '工程师', 'L5', 2, '技术岗位', 3, 1),
('SALES_REP', '销售代表', 'L5', 3, '销售岗位', 2, 1);

-- 8.6 插入默认菜单（path 字段存储完整的前端路由路径）
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `menu_code`, `path`, `component`, `icon`, `permission`, `sort_order`, `visible`, `status`) VALUES
-- 一级菜单：首页
(0, '首页', 2, 'dashboard', 'dashboard', 'dashboard/index', 'dashboard', NULL, 0, 1, 1),
-- 一级菜单：员工管理
(0, '员工管理', 1, 'employee', 'employee', NULL, 'user', NULL, 1, 1, 1),
-- 一级菜单：部门管理
(0, '部门管理', 1, 'department', 'department', NULL, 'office-building', NULL, 2, 1, 1),
-- 一级菜单：职位管理
(0, '职位管理', 1, 'position', 'position', NULL, 'team', NULL, 3, 1, 1),
-- 一级菜单：考勤管理
(0, '考勤管理', 1, 'attendance', 'attendance', NULL, 'clock', NULL, 4, 1, 1),
-- 考勤管理子菜单
(5, '考勤规则', 2, 'rule', 'attendance/rules', 'attendance/rules', 'setting', NULL, 1, 1, 1),
(5, '日考勤', 2, 'daily', 'attendance/daily', 'attendance/daily', 'calendar', NULL, 2, 1, 1),
(5, '考勤结果', 2, 'result', 'attendance/result', 'attendance/result', 'line-chart', NULL, 3, 1, 1),
-- 一级菜单：工资管理
(0, '工资管理', 1, 'payroll', 'payroll', NULL, 'money-collect', NULL, 5, 1, 1),
-- 一级菜单：社保管理
(0, '社保管理', 1, 'insurance', 'insurance', NULL, 'file-text', NULL, 6, 1, 1),
-- 一级菜单：系统管理
(0, '系统管理', 1, 'system', 'system', NULL, 'setting', NULL, 7, 1, 1),
-- 系统管理子菜单
(11, '用户管理', 2, 'user', 'system/users', 'system/users', 'user', NULL, 1, 1, 1),
(11, '角色管理', 2, 'role', 'system/roles', 'system/roles', 'team', NULL, 2, 1, 1),
(11, '菜单管理', 2, 'menu', 'system/menus', 'system/menus', 'menu', NULL, 3, 1, 1),
(11, '字典管理', 2, 'dictionary', 'system/dictionary', 'system/dictionary', 'book', NULL, 4, 1, 1),
(11, '操作日志', 2, 'operation_log', 'system/operation-log', 'system/operation-log', 'file-text', NULL, 5, 1, 1);

-- 8.7 插入角色菜单关联（管理员拥有所有权限）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu`;

-- 8.8 插入默认考勤规则
INSERT INTO `att_rule` (`rule_code`, `rule_name`, `rule_type`, `priority`, `check_in_time`, `check_out_time`, `latest_check_in_time`, `earliest_check_out_time`, `required_work_minutes`, `allow_late_minutes`, `allow_early_leave_minutes`, `rest_start_time`, `rest_end_time`, `cross_day_flag`, `overtime_flag`, `status`, `description`) VALUES
('FIXED_9_6', '固定班制 9:00-18:00', 1, 1, '09:00:00', '18:00:00', NULL, NULL, 480, 10, 10, '12:00:00', '13:00:00', 0, 1, 1, '标准固定班制，工作 8 小时，中午休息 1 小时'),
('FLEXIBLE_10_8', '弹性工时 最晚 10 点上班', 2, 2, NULL, NULL, '10:00:00', NULL, 480, 0, 0, NULL, NULL, 0, 1, 1, '弹性工时，最晚 10 点前上班，累计工作 8 小时'),
('WORK_HOURS_8', '按工时制 8 小时', 3, 3, NULL, NULL, NULL, NULL, 480, 0, 0, NULL, NULL, 0, 1, 1, '按工时制，任意时间段打卡，累计工作 8 小时');

-- 8.9 插入默认字典类型
INSERT INTO `sys_dict_type` (`dict_code`, `dict_name`, `description`, `status`) VALUES
('user_status', '用户状态', '用户账户状态', 1),
('menu_type', '菜单类型', '菜单类型分类', 1),
('dept_status', '部门状态', '部门状态', 1),
('post_status', '职位状态', '职位状态', 1),
('role_status', '角色状态', '角色状态', 1),
('business_type', '业务类型', '操作业务类型', 1),
('oper_status', '操作状态', '操作执行状态', 1);

-- 8.10 插入默认字典数据
INSERT INTO `sys_dict_item` (`dict_type_id`, `dict_label`, `dict_value`, `sort`, `description`)
SELECT dt.id, label, value, sort, item_desc FROM (
    -- 用户状态
    SELECT 1 as type_id, '正常' as label, '1' as value, 1 as sort, '正常状态' as item_desc UNION ALL
    SELECT 1, '禁用', '0', 2, '禁用状态' UNION ALL
    -- 菜单类型
    SELECT 2, '目录', '1', 1, '目录类型' UNION ALL
    SELECT 2, '菜单', '2', 2, '菜单类型' UNION ALL
    SELECT 2, '按钮', '3', 3, '按钮类型' UNION ALL
    -- 部门状态
    SELECT 3, '正常', '1', 1, '正常状态' UNION ALL
    SELECT 3, '停用', '0', 2, '停用状态' UNION ALL
    -- 职位状态
    SELECT 4, '正常', '1', 1, '正常状态' UNION ALL
    SELECT 4, '停用', '0', 2, '停用状态' UNION ALL
    -- 角色状态
    SELECT 5, '正常', '1', 1, '正常状态' UNION ALL
    SELECT 5, '停用', '0', 2, '停用状态' UNION ALL
    -- 业务类型
    SELECT 6, '其它', '0', 0, '其他业务' UNION ALL
    SELECT 6, '新增', '1', 1, '新增操作' UNION ALL
    SELECT 6, '修改', '2', 2, '修改操作' UNION ALL
    SELECT 6, '删除', '3', 3, '删除操作' UNION ALL
    SELECT 6, '导出', '4', 4, '导出操作' UNION ALL
    SELECT 6, '导入', '5', 5, '导入操作' UNION ALL
    -- 操作状态
    SELECT 7, '正常', '0', 0, '正常操作' UNION ALL
    SELECT 7, '异常', '1', 1, '异常操作'
) items
JOIN sys_dict_type dt ON dt.dict_code = (
    CASE items.type_id
        WHEN 1 THEN 'user_status'
        WHEN 2 THEN 'menu_type'
        WHEN 3 THEN 'dept_status'
        WHEN 4 THEN 'post_status'
        WHEN 5 THEN 'role_status'
        WHEN 6 THEN 'business_type'
        WHEN 7 THEN 'oper_status'
    END
)
WHERE dt.deleted = 0;

-- =============================================
-- 初始化完成
-- =============================================
SELECT '人力资源管理系统数据库初始化完成！' as result;
