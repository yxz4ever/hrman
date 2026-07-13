# 人力资源管理系统 (HRM)

> 基于 Vue 3 + Spring Boot + MySQL 的企业级人力资源管理系统

[![Java](https://img.shields.io/badge/Java-17+-orange)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.3.4-blue)](https://vuejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue)](https://mysql.com/)

## 📋 目录

- [项目概述](#项目概述)
- [功能模块](#功能模块)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [API 文档](#api 文档)
- [默认账号](#默认账号)
- [开发说明](#开发说明)

---

## 项目概述

本系统是一个完整的企业人力资源管理系统，涵盖员工管理、考勤管理、薪资管理、社保管理、系统权限等核心模块。系统采用前后端分离架构，提供友好的用户界面和高效的业务流程。

### 核心特性

- ✅ **完整的 RBAC 权限体系** - 用户、角色、菜单、部门、岗位多维度权限管理
- ✅ **灵活的考勤规则引擎** - 支持固定班制、弹性工时、按工时制等多种考勤模式
- ✅ **自动化薪资计算** - 支持工资模板、批次管理、自动计算
- ✅ **社保政策管理** - 多城市社保政策配置，自动计算社保金额
- ✅ **员工全生命周期管理** - 从入职到离职的完整流程管理

---

## 功能模块

### 1️⃣ 系统权限管理

| 功能 | 说明 |
|------|------|
| 用户管理 | 系统用户的增删改查、状态管理 |
| 角色管理 | 角色创建、权限分配 |
| 菜单管理 | 菜单权限配置、路由管理 |
| 部门管理 | 组织架构管理、部门层级 |
| 岗位管理 | 职位体系、职级管理 |
| 字典管理 | 系统字典数据维护 |
| 操作日志 | 用户操作行为记录 |

### 2️⃣ 员工管理

| 功能 | 说明 |
|------|------|
| 员工档案 | 完整的员工信息档案管理 |
| 员工列表 | 多维度查询、筛选、分页 |
| 员工增删改 | 员工信息的完整 CRUD 操作 |
| 工号管理 | 自动生成/手动设置工号 |
| 部门关联 | 员工与部门关联管理 |
| 岗位分配 | 员工岗位分配与调整 |

### 3️⃣ 考勤管理

| 功能 | 说明 |
|------|------|
| 考勤规则 | 支持固定班制、弹性工时、按工时制 |
| 规则组管理 | 规则组配置、适用范围设置 |
| 打卡记录 | 原始打卡数据记录与查询 |
| 日考勤 | 每日考勤结果自动计算 |
| 考勤结果 | 考勤统计、异常标记 |
| 考勤引擎 | 自动计算迟到、早退、缺勤、加班 |

### 4️⃣ 薪资管理

| 功能 | 说明 |
|------|------|
| 工资模板 | 自定义工资项、计算公式 |
| 工资批次 | 按月/季度批次管理 |
| 工资计算 | 自动计算应发、实发工资 |
| 工资明细 | 个人工资明细查询 |
| 批量发放 | 工资批量发放处理 |

### 5️⃣ 社保管理

| 功能 | 说明 |
|------|------|
| 社保政策 | 多城市社保政策配置 |
| 社保计算 | 自动计算社保个人/公司部分 |
| 社保账户 | 员工社保账户管理 |
| 月度明细 | 月度社保缴费明细 |

---

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.1.5 | 核心框架 |
| MySQL | 8.0+ | 数据库 |
| MyBatis-Plus | 3.5.3.1 | ORM 框架 |
| Spring Security | - | 安全框架 |
| JWT | - | Token 认证 |
| Maven | 3.6+ | 构建工具 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.3.4 | 前端框架 |
| Vite | 4.4.9 | 构建工具 |
| Element Plus | 2.3.14 | UI 组件库 |
| Pinia | 2.1.6 | 状态管理 |
| Vue Router | 4.2.4 | 路由管理 |
| Axios | 1.5.0 | HTTP 客户端 |
| ECharts | 5.4.3 | 图表库 |

---

## 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 1. 克隆项目

```bash
git clone <repository-url>
cd HR
```

### 2. 数据库初始化

```bash
# 创建数据库并导入初始化脚本
mysql -u root -p < database/init.sql
```

**默认数据库配置：**
- 数据库名：`hrm_db`
- 用户名：`root`
- 密码：`root` (请根据实际情况修改)

### 3. 后端启动

**Windows:**
```bash
start-backend.bat
```

**Linux/Mac:**
```bash
chmod +x start-backend.sh
./start-backend.sh
```

**手动启动:**
```bash
cd backend
# 修改配置文件 src/main/resources/application.yml 中的数据库连接
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 4. 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

---

## 项目结构

```
HR/
├── backend/                      # 后端 Spring Boot 项目
│   ├── src/main/java/com/company/hrm/
│   │   ├── attendance/           # 考勤模块
│   │   │   ├── controller/       # 控制器
│   │   │   ├── entity/           # 实体类
│   │   │   ├── mapper/           # 数据访问层
│   │   │   ├── service/          # 业务逻辑层
│   │   │   ├── engine/           # 考勤引擎
│   │   │   └── scheduler/        # 定时任务
│   │   ├── employee/             # 员工模块
│   │   ├── payroll/              # 薪资模块
│   │   ├── system/               # 系统模块
│   │   │   ├── security/         # 安全认证
│   │   └── common/               # 公共模块
│   ├── src/main/resources/
│   │   ├── mapper/               # MyBatis XML
│   │   └── application.yml       # 配置文件
│   └── pom.xml
├── frontend/                     # 前端 Vue 3 项目
│   ├── src/
│   │   ├── api/                  # API 接口
│   │   ├── assets/               # 静态资源
│   │   ├── components/           # 公共组件
│   │   ├── layout/               # 布局组件
│   │   ├── router/               # 路由配置
│   │   ├── store/                # 状态管理
│   │   ├── styles/               # 样式文件
│   │   ├── utils/                # 工具函数
│   │   └── views/                # 页面组件
│   │       ├── attendance/       # 考勤页面
│   │       ├── dashboard/        # 首页
│   │       ├── department/       # 部门管理
│   │       ├── employee/         # 员工管理
│   │       ├── insurance/        # 社保管理
│   │       ├── login/            # 登录页
│   │       ├── payroll/          # 薪资管理
│   │       ├── position/         # 职位管理
│   │       └── system/           # 系统管理
│   ├── package.json
│   └── vite.config.js
├── database/                     # 数据库脚本
│   ├── init.sql                  # 初始化脚本
│   └── update_menu_paths.sql     # 菜单路径更新脚本
├── start-backend.bat             # 后端启动脚本 (Windows)
├── start-backend.sh              # 后端启动脚本 (Linux/Mac)
└── README.md
```

---

## API 文档

### 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 退出登录 |
| GET | /api/auth/user/info | 获取当前用户信息 |

### 员工接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/employees | 分页查询员工列表 |
| GET | /api/employees/{id} | 获取员工详情 |
| GET | /api/employees/emp-no/{empNo} | 根据工号查询 |
| POST | /api/employees | 新增员工 |
| PUT | /api/employees/{id} | 更新员工 |
| DELETE | /api/employees/{id} | 删除员工 |

### 考勤接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/attendance/rules | 获取考勤规则列表 |
| GET | /api/attendance/rules/{id} | 获取规则详情 |
| POST | /api/attendance/rules | 创建规则 |
| PUT | /api/attendance/rules/{id} | 更新规则 |
| DELETE | /api/attendance/rules/{id} | 删除规则 |
| GET | /api/attendance/daily | 获取日考勤数据 |
| GET | /api/attendance/result | 获取考勤结果 |

### 薪资接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/payroll/templates | 获取工资模板列表 |
| GET | /api/payroll/batches | 获取工资批次列表 |
| GET | /api/payroll/records | 获取工资明细 |

### 社保接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/insurance/policies | 获取社保政策列表 |
| GET | /api/insurance/accounts | 获取社保账户列表 |

### 系统接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/users | 获取用户列表 |
| GET | /api/system/roles | 获取角色列表 |
| GET | /api/system/menus | 获取菜单列表 |
| GET | /api/system/departments | 获取部门列表 |
| GET | /api/system/posts | 获取岗位列表 |

---

## 默认账号

| 用户名 | 密码 | 角色 | 权限 |
|--------|------|------|------|
| admin | admin123 | 系统管理员 | 所有权限 |

---

## 开发说明

### 后端开发规范

1. **代码结构**: 遵循 Controller-Service-Mapper 三层架构
2. **命名规范**: 
   - 实体类：驼峰命名 (如 `HrEmployee`)
   - 表名：下划线命名 (如 `hr_employee`)
3. **统一返回**: 使用 `Result<T>` 包装类
4. **异常处理**: 使用 `@RestControllerAdvice` 全局异常处理
5. **日志记录**: 使用 SLF4J 进行日志输出

### 前端开发规范

1. **组件开发**: 使用 Composition API
2. **状态管理**: 使用 Pinia 进行状态管理
3. **API 调用**: 封装 Axios，统一处理请求响应
4. **路由守卫**: 实现 Token 验证和权限控制
5. **样式规范**: 使用 SCSS，遵循 BEM 命名规范

### 数据库规范

1. **表命名**: 模块前缀 + 下划线 + 表名 (如 `hr_employee`)
2. **字段命名**: 下划线命名法
3. **必填字段**: 每个表必须包含 `created_at`, `updated_at`, `deleted` 字段
4. **索引规范**: 主键 `pk_`, 唯一索引 `uk_`, 普通索引 `idx_`

---

## 数据库表结构

### 系统权限表

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表 |
| `sys_role` | 角色表 |
| `sys_user_role` | 用户角色关联表 |
| `sys_menu` | 菜单权限表 |
| `sys_role_menu` | 角色菜单关联表 |
| `sys_dept` | 部门表 |
| `sys_post` | 岗位表 |
| `sys_dict_type` | 字典类型表 |
| `sys_dict_item` | 字典数据表 |
| `sys_oper_log` | 操作日志表 |

### 员工管理表

| 表名 | 说明 |
|------|------|
| `hr_employee` | 员工主表 |

### 考勤管理表

| 表名 | 说明 |
|------|------|
| `att_rule_group` | 考勤规则组表 |
| `att_rule` | 考勤规则表 |
| `att_clock_record` | 打卡记录表 |
| `att_daily_result` | 日考勤结果表 |

### 薪资管理表

| 表名 | 说明 |
|------|------|
| `pay_salary_template` | 工资模板表 |
| `pay_salary_batch` | 工资批次表 |
| `pay_salary_record` | 工资明细表 |

### 社保管理表

| 表名 | 说明 |
|------|------|
| `ins_policy` | 社保政策表 |

---

## 常见问题

### 1. 后端启动失败

**检查项:**
- JDK 版本是否为 17+
- MySQL 服务是否正常运行
- `application.yml` 中的数据库连接配置是否正确
- 端口 8080 是否被占用

### 2. 前端无法连接后端

**检查项:**
- 后端服务是否已启动
- `frontend/src/utils/request.js` 中的 `baseURL` 配置
- 浏览器控制台是否有跨域错误
- 防火墙是否阻止了端口访问

### 3. 登录失败

**检查项:**
- 数据库中是否存在默认用户数据
- 用户名和密码是否正确
- JWT 配置是否正确
- 浏览器是否禁用了 Cookie

### 4. 数据库导入失败

**检查项:**
- MySQL 版本是否为 8.0+
- 字符集是否为 utf8mb4
- 是否有足够的权限创建数据库

---

## 更新日志

### v1.0.0 (2026-07-13)

**新增功能:**
- ✅ 完整的系统权限管理模块
- ✅ 员工信息管理模块
- ✅ 考勤规则引擎（支持多种考勤模式）
- ✅ 薪资管理模块
- ✅ 社保管理模块
- ✅ 字典管理功能
- ✅ 操作日志记录

**技术特性:**
- ✅ Spring Boot 3.1.5
- ✅ Vue 3.3.4 + Element Plus
- ✅ MyBatis-Plus ORM
- ✅ JWT 认证
- ✅ RBAC 权限模型

---

## 许可证

本项目仅供学习和内部使用。

---

**开发团队**: 人力资源管理系统项目组  
**最后更新**: 2026 年 7 月 13 日
