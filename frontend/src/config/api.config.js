/**
 * API配置文件
 */

// API基础配置
export const apiConfig = {
  // 开发环境API地址
  baseURL: '/api',
  
  // 请求超时时间（毫秒）
  timeout: 10000,
  
  // Token存储键名
  tokenKey: 'token',
  
  // 用户信息存储键名
  userInfoKey: 'userInfo'
}

// API端点配置
export const apiEndpoints = {
  // 认证相关
  auth: {
    login: '/auth/login',
    logout: '/auth/logout',
    getUserInfo: '/auth/user/info'
  },
  
  // 员工管理
  employee: {
    list: '/employee/list',
    detail: '/employee/detail',
    create: '/employee/create',
    update: '/employee/update',
    delete: '/employee/delete',
    export: '/employee/export'
  },
  
  // 考勤管理
  attendance: {
    list: '/attendance/list',
    daily: '/attendance/daily',
    rules: '/attendance/rules',
    import: '/attendance/import',
    export: '/attendance/export'
  },
  
  // 工资管理
  payroll: {
    list: '/payroll/list',
    calculate: '/payroll/calculate',
    templates: '/payroll/templates',
    batches: '/payroll/batches',
    salarySlip: '/payroll/salary-slip'
  },
  
  // 社保管理
  insurance: {
    policies: '/insurance/policies',
    accounts: '/insurance/accounts',
    monthly: '/insurance/monthly',
    list: '/insurance/list'
  },
  
  // 系统管理
  system: {
    users: '/system/users',
    roles: '/system/roles',
    menus: '/system/menus',
    depts: '/system/depts',
    posts: '/system/posts',
    dicts: '/system/dicts',
    logs: '/system/logs',
    settings: '/system/settings'
  }
}

// HTTP状态码配置
export const httpStatus = {
  SUCCESS: 200,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500
}