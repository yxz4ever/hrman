<template>
  <a-layout class="app-layout">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="isCollapse"
      :trigger="null"
      collapsible
      width="210"
      class="sidebar-sider"
    >
      <div class="sidebar-logo">
        <AppstoreOutlined :class="['logo-icon', { 'collapsed': isCollapse }]" />
        <span v-if="!isCollapse" class="logo-text">人力资源管理系统</span>
      </div>

      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        :inline-collapsed="isCollapse"
        @click="handleMenuClick"
      >
        <template v-for="menuItem in menuRoutes" :key="menuItem.path">
          <a-menu-item
            v-if="!menuItem.children"
            :key="menuItem.path"
            @click="handleMenuClick({ key: menuItem.path })"
          >
            <component :is="iconMap[menuItem.icon]" />
            <span>{{ menuItem.title }}</span>
          </a-menu-item>

          <a-sub-menu v-else :key="menuItem.path">
            <template #title>
              <component :is="iconMap[menuItem.icon]" />
              <span>{{ menuItem.title }}</span>
            </template>
            <a-menu-item
              v-for="child in menuItem.children"
              :key="child.path"
              @click="handleMenuClick({ key: child.path })"
            >
              <component :is="iconMap[child.icon]" />
              <span>{{ child.title }}</span>
            </a-menu-item>
          </a-sub-menu>
        </template>
      </a-menu>
    </a-layout-sider>

    <!-- 主体内容 -->
    <a-layout class="main-layout">
      <!-- 顶部导航 -->
      <a-layout-header class="header">
        <div class="header-left">
          <MenuUnfoldOutlined v-if="isCollapse" class="trigger" @click="toggleCollapse" />
          <MenuFoldOutlined v-else class="trigger" @click="toggleCollapse" />
          <a-breadcrumb class="breadcrumb">
            <a-breadcrumb-item>首页</a-breadcrumb-item>
            <a-breadcrumb-item v-if="currentRoute.meta.title">
              {{ currentRoute.meta.title }}
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>

        <div class="header-right">
          <a-badge count="5" class="header-action">
            <BellOutlined />
          </a-badge>
          <a-dropdown>
            <div class="user-info">
              <a-avatar :size="32" src="https://joesandbox.s3.amazonaws.com/000000.jpg" />
              <span class="username">管理员</span>
              <DownOutlined />
            </div>
            <template #overlay>
              <a-menu @click="handleCommand">
                <a-menu-item key="profile">个人中心</a-menu-item>
                <a-menu-item key="settings">系统设置</a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout">退出登录</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 标签页 -->
      <div class="tags-view">
        <Tabs />
      </div>

      <!-- 内容区域 -->
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  AppstoreOutlined,
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  BellOutlined,
  DownOutlined,
  MenuOutlined,
  UserOutlined,
  ClockCircleOutlined,
  SettingOutlined,
  CalendarOutlined,
  MoneyCollectOutlined,
  FileTextOutlined,
  ToolOutlined,
  UsergroupAddOutlined,
  DashboardOutlined,
  BarChartOutlined,
  CloseCircleOutlined,
  TeamOutlined,
  BankOutlined,
  BookOutlined,
  LineChartOutlined
} from '@ant-design/icons-vue'
import Tabs from '@/components/Tabs/index.vue'
import { apiConfig } from '@/config/api.config'
import { routeConfig } from '@/config/constants.config'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const isCollapse = ref(false)
const selectedKeys = ref([route.path])
const menuRoutes = ref([])
const loading = ref(false)

// 图标映射
const iconMap = {
  DataLine: BarChartOutlined,
  DataAnalysis: BarChartOutlined,
  User: UserOutlined,
  UserFilled: UsergroupAddOutlined,
  Clock: ClockCircleOutlined,
  ClockCircleOutlined: ClockCircleOutlined,
  Setting: SettingOutlined,
  setting: SettingOutlined,
  Calendar: CalendarOutlined,
  Money: MoneyCollectOutlined,
  MoneyCollect: MoneyCollectOutlined,
  'money-collect': MoneyCollectOutlined,
  Document: FileTextOutlined,
  FileText: FileTextOutlined,
  'file-text': FileTextOutlined,
  Tools: ToolOutlined,
  Menu: MenuOutlined,
  menu: MenuOutlined,
  OfficeBuilding: DashboardOutlined,
  'office-building': DashboardOutlined,
  CircleClose: CloseCircleOutlined,
  Team: TeamOutlined,
  team: TeamOutlined,
  Book: BookOutlined,
  book: BookOutlined,
  LineChart: LineChartOutlined,
  'line-chart': LineChartOutlined,
  Dashboard: DashboardOutlined,
  dashboard: DashboardOutlined
}

// 从后端加载菜单
const loadMenu = async () => {
  loading.value = true
  try {
    console.log('[Menu] 开始加载菜单...')
    const result = await request.get('/system/menu/user/tree')
    console.log('[Menu] 菜单加载结果:', result)

    if (result.code === 200) {
      menuRoutes.value = transformMenu(result.data || [])
      console.log('[Menu] 菜单加载成功:', menuRoutes.value)
    } else {
      console.error('[Menu] 菜单加载失败:', result.message)
      message.error(result.message || '加载菜单失败')
    }
  } catch (error) {
    console.error('[Menu] 加载菜单异常:', error)
    console.error('[Menu] 错误详情:', error.response?.data || error.message)

    // 如果是 401 未授权，跳转到登录页
    if (error.response?.status === 401) {
      console.log('[Menu] 未授权，跳转到登录页')
      message.error('请先登录')
      router.push(routeConfig.loginPath)
    } else {
      message.error('加载菜单失败：' + (error.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
}

// 转换菜单格式
const transformMenu = (menus) => {
  return menus.map(menu => {
    // 确保 path 以 / 开头，变成绝对路径
    const fullPath = menu.path.startsWith('/') ? menu.path : `/${menu.path}`

    const item = {
      path: fullPath,
      title: menu.menuName,
      icon: menu.icon,
      order: menu.sortOrder
    }

    // 如果有子菜单，递归处理
    if (menu.children && menu.children.length > 0) {
      item.children = transformMenu(menu.children)
    }

    return item
  })
}

// 当前激活的菜单
watch(() => route.path, (newPath) => {
  selectedKeys.value = [newPath]
})

// 当前路由
const currentRoute = computed(() => route)

// 组件挂载时加载菜单
onMounted(() => {
  loadMenu()
})

// 切换侧边栏
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 处理菜单点击
const handleMenuClick = ({ key }) => {
  router.push(key)
}

// 处理下拉菜单命令
const handleCommand = ({ key }) => {
  switch (key) {
    case 'profile':
      message.info('个人中心功能开发中')
      break
    case 'settings':
      message.info('系统设置功能开发中')
      break
    case 'logout':
      Modal.confirm({
        title: '提示',
        content: '确定要退出登录吗？',
        okText: '确定',
        cancelText: '取消',
        okType: 'warning',
        onOk: () => {
          localStorage.removeItem(apiConfig.tokenKey)
          router.push(routeConfig.loginPath)
          message.success('退出登录成功')
        }
      })
      break
  }
}
</script>

<style scoped lang="scss">
.app-layout {
  min-height: 100vh;
}

.sidebar-sider {
  background: #001529;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);

  .sidebar-logo {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #002140;
    padding: 0 16px;

    .logo-icon {
      font-size: 24px;
      color: #1890ff;
      margin-right: 10px;

      &.collapsed {
        margin-right: 0;
      }
    }

    .logo-text {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      white-space: nowrap;
    }
  }

  :deep(.ant-menu) {
    background: #001529;
    border-right: none;

    .ant-menu-item,
    .ant-menu-submenu-title {
      color: rgba(255, 255, 255, 0.65);

      &:hover {
        color: #fff;
      }

      &.ant-menu-item-selected,
      &.ant-menu-item-active {
        color: #fff;
        background: #1890ff;
      }
    }

    .ant-menu-submenu-title:hover {
      color: #fff;
    }
  }
}

.main-layout {
  min-height: 100vh;
}

.header {
  background: #fff;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 10;
  position: relative;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .trigger {
      font-size: 18px;
      cursor: pointer;
      transition: color 0.3s;
      color: rgba(0, 0, 0, 0.65);

      &:hover {
        color: #1890ff;
      }
    }

    .breadcrumb {
      margin-left: 12px;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .header-action {
      cursor: pointer;
      font-size: 18px;
      color: rgba(0, 0, 0, 0.65);

      &:hover {
        color: #1890ff;
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 6px 12px;
      border-radius: 4px;
      transition: all 0.3s;

      &:hover {
        background: #f5f5f5;
      }

      .username {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.85);
        font-weight: 500;
      }
    }
  }
}

.tags-view {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12);
}

.content {
  background: #f0f2f5;
  padding: 24px;
  min-height: calc(100vh - 158px);
  overflow-y: auto;
  max-height: calc(100vh - 158px);

  // 自定义滚动条样式
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: #bfbfbf;
    border-radius: 3px;

    &:hover {
      background: #999;
    }
  }
}
</style>
