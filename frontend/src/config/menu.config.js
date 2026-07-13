/**
 * 菜单配置文件
 */

export const menuConfig = [
  {
    path: '/dashboard',
    title: '首页',
    icon: 'DataLine',
    order: 1
  },
  {
    path: '/employee',
    title: '员工管理',
    icon: 'User',
    order: 2
  },
  {
    path: '/department',
    title: '部门管理',
    icon: 'OfficeBuilding',
    order: 2.1
  },
  {
    path: '/position',
    title: '职位管理',
    icon: 'Team',
    order: 2.2
  },
  {
    path: '/attendance',
    title: '考勤管理',
    icon: 'Clock',
    order: 3,
    children: [
      {
        path: '/attendance/rules',
        title: '考勤规则',
        icon: 'Setting',
        order: 1
      },
      {
        path: '/attendance/daily',
        title: '日考勤',
        icon: 'Calendar',
        order: 2
      },
      {
        path: '/attendance/result',
        title: '考勤结果',
        icon: 'DataAnalysis',
        order: 3
      }
    ]
  },
  {
    path: '/payroll',
    title: '工资管理',
    icon: 'Money',
    order: 4
  },
  {
    path: '/insurance',
    title: '社保管理',
    icon: 'Document',
    order: 5
  },
  {
    path: '/system',
    title: '系统管理',
    icon: 'Tools',
    order: 6,
    children: [
      {
        path: '/system/users',
        title: '用户管理',
        icon: 'User',
        order: 1
      },
      {
        path: '/system/roles',
        title: '角色管理',
        icon: 'UserFilled',
        order: 2
      },
      {
        path: '/system/menus',
        title: '菜单管理',
        icon: 'Menu',
        order: 3
      },
      {
        path: '/system/dictionary',
        title: '字典管理',
        icon: 'Book',
        order: 4
      },
      {
        path: '/system/operation-log',
        title: '操作日志',
        icon: 'FileText',
        order: 5
      }
    ]
  }
]

// 根据路径查找菜单项
export function findMenuItemByPath(path, menuItems = menuConfig) {
  for (const item of menuItems) {
    if (item.path === path) {
      return item
    }
    if (item.children) {
      const found = findMenuItemByPath(path, item.children)
      if (found) return found
    }
  }
  return null
}

// 获取面包屑导航
export function getBreadcrumb(path) {
  const breadcrumbs = []
  const findPath = (items, targetPath, parent = null) => {
    for (const item of items) {
      if (item.path === targetPath) {
        if (parent) {
          breadcrumbs.unshift(parent)
        }
        breadcrumbs.unshift(item)
        return true
      }
      if (item.children) {
        if (findPath(item.children, targetPath, item)) {
          return true
        }
      }
    }
    return false
  }

  findPath(menuConfig, path)
  return breadcrumbs
}

// 获取所有菜单项（扁平化）
export function getAllMenuItems(menuItems = menuConfig, result = []) {
  for (const item of menuItems) {
    result.push(item)
    if (item.children) {
      getAllMenuItems(item.children, result)
    }
  }
  return result
}