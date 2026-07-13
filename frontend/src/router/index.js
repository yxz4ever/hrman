import { createRouter, createWebHistory } from 'vue-router'
import { routeConfig, appConfig } from '@/config/constants.config'
import { apiConfig } from '@/config/api.config'

const routes = [
  {
    path: routeConfig.loginPath,
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: routeConfig.homePath,
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'DataLine' }
      },
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('@/views/employee/index.vue'),
        meta: { title: '员工管理', icon: 'User' }
      },
      {
        path: 'attendance',
        name: 'Attendance',
        component: () => import('@/views/attendance/index.vue'),
        meta: { title: '考勤管理', icon: 'Clock' }
      },
      {
        path: 'attendance/rules',
        name: 'AttendanceRules',
        component: () => import('@/views/attendance/rules.vue'),
        meta: { title: '考勤规则', icon: 'Setting' }
      },
      {
        path: 'attendance/daily',
        name: 'AttendanceDaily',
        component: () => import('@/views/attendance/daily.vue'),
        meta: { title: '日考勤', icon: 'Calendar' }
      },
      {
        path: 'attendance/result',
        name: 'AttendanceResult',
        component: () => import('@/views/attendance/result.vue'),
        meta: { title: '考勤结果', icon: 'DataAnalysis' }
      },
      {
        path: 'payroll',
        name: 'Payroll',
        component: () => import('@/views/payroll/index.vue'),
        meta: { title: '工资管理', icon: 'Money' }
      },
      {
        path: 'insurance',
        name: 'Insurance',
        component: () => import('@/views/insurance/index.vue'),
        meta: { title: '社保管理', icon: 'Document' }
      },
      {
        path: 'system',
        name: 'System',
        component: () => import('@/views/system/index.vue'),
        meta: { title: '系统管理', icon: 'Tools' },
        redirect: '/system/users'
      },
      {
        path: 'system/users',
        name: 'SystemUsers',
        component: () => import('@/views/system/users.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/roles',
        name: 'SystemRoles',
        component: () => import('@/views/system/roles.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' }
      },
      {
        path: 'system/menus',
        name: 'SystemMenus',
        component: () => import('@/views/system/menus.vue'),
        meta: { title: '菜单管理', icon: 'Menu' }
      },
      {
        path: 'system/dictionary',
        name: 'SystemDictionary',
        component: () => import('@/views/system/dictionary.vue'),
        meta: { title: '字典管理', icon: 'Book' }
      },
      {
        path: 'system/operation-log',
        name: 'SystemOperationLog',
        component: () => import('@/views/system/operation-log.vue'),
        meta: { title: '操作日志', icon: 'FileText' }
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/views/department/index.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'position',
        name: 'Position',
        component: () => import('@/views/position/index.vue'),
        meta: { title: '职位管理', icon: 'Team' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - ${appConfig.appName}` : appConfig.appName

  // 获取token
  const token = localStorage.getItem(apiConfig.tokenKey)

  // 如果访问的不是登录页且没有token，则跳转到登录页
  if (to.path !== routeConfig.loginPath && !token) {
    next(routeConfig.loginPath)
  }
  // 如果访问的是登录页且已经有token，则跳转到首页
  else if (to.path === routeConfig.loginPath && token) {
    next(routeConfig.homePath)
  }
  // 其他情况正常放行
  else {
    next()
  }
})

export default router