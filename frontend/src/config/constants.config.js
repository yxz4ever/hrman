/**
 * 常量配置文件
 */

// 应用配置
export const appConfig = {
  // 应用名称
  appName: '人力资源管理系统',
  // 应用英文名称
  appNameEn: 'Human Resource Management System',
  // 应用版本
  version: '1.0.0',
  // 默认分页大小
  defaultPageSize: 10,
  // 分页大小选项
  pageSizes: [10, 20, 50, 100]
}

// 路由配置
export const routeConfig = {
  // 默认首页路由
  homePath: '/dashboard',
  // 登录页路由
  loginPath: '/login',
  // 默认重定向路由
  defaultRedirect: '/dashboard'
}

// 用户配置
export const userConfig = {
  // 默认用户名
  defaultUsername: 'admin',
  // 默认密码
  defaultPassword: 'admin123',
  // Token过期时间（毫秒）
  tokenExpireTime: 7 * 24 * 60 * 60 * 1000, // 7天
  // 记住我过期时间（毫秒）
  rememberMeExpireTime: 30 * 24 * 60 * 60 * 1000 // 30天
}

// 考勤配置
export const attendanceConfig = {
  // 工作时间（分钟）
  workMinutes: 8 * 60, // 8小时
  // 迟到阈值（分钟）
  lateThreshold: 10,
  // 早退阈值（分钟）
  earlyLeaveThreshold: 10,
  // 缺卡阈值（分钟）
  missingCardThreshold: 30
}

// 工资配置
export const payrollConfig = {
  // 默认基本工资
  defaultBaseSalary: 5000,
  // 社保个人缴纳比例
  socialInsurancePersonalRate: 0.105, // 10.5%
  // 公积金个人缴纳比例
  housingFundPersonalRate: 0.12, // 12%
  // 个人所得税起征点
  taxThreshold: 5000
}

// 文件上传配置
export const uploadConfig = {
  // 最大文件大小（MB）
  maxSize: 10,
  // 允许的文件类型
  allowedTypes: [
    'image/jpeg',
    'image/png',
    'image/gif',
    'application/pdf',
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'application/vnd.ms-word',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  ],
  // 上传路径
  uploadPath: '/api/upload'
}

// 消息提示配置
export const messageConfig = {
  // 显示时长（毫秒）
  duration: 3000,
  // 最大显示数量
  maxCount: 3
}

// 表格配置
export const tableConfig = {
  // 默认表格高度
  defaultHeight: 400,
  // 是否显示边框
  border: true,
  // 是否显示斑马纹
  stripe: true,
  // 是否显示表头
  showHeader: true,
  // 空数据提示文本
  emptyText: '暂无数据'
}

// 表单配置
export const formConfig = {
  // 标签宽度
  labelWidth: '100px',
  // 标签位置
  labelPosition: 'right',
  // 是否显示星号
  requiredAsterisk: true
}

// 颜色配置
export const colorConfig = {
  // 主题色
  primary: '#1890ff',
  // 成功色
  success: '#52c41a',
  // 警告色
  warning: '#faad14',
  // 危险色
  danger: '#ff4d4f',
  // 信息色
  info: '#13c2c2'
}

// 日期格式配置
export const dateFormat = {
  // 日期格式
  date: 'YYYY-MM-DD',
  // 时间格式
  time: 'HH:mm:ss',
  // 日期时间格式
  dateTime: 'YYYY-MM-DD HH:mm:ss',
  // 月份格式
  month: 'YYYY-MM',
  // 年份格式
  year: 'YYYY'
}