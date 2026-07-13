<template>
  <div class="tabs-container">
    <div class="tabs-header">
      <div class="tabs-scroll-wrapper">
        <button class="tabs-scroll-btn left" @click="scrollTabs(-200)" :disabled="scrollPosition <= 0">
          <LeftOutlined />
        </button>

        <div class="tabs-list" ref="tabsListRef">
          <div
            v-for="tab in tabs"
            :key="tab.path"
            class="tab-item"
            :class="{ active: currentPath === tab.path, closable: tab.closable }"
            @click="handleTabClick(tab)"
            @contextmenu.prevent="handleContextMenu($event, tab)"
          >
            <span v-if="tab.icon" class="tab-icon">
              <component :is="iconMap[tab.icon]" />
            </span>
            <span class="tab-title">{{ tab.title }}</span>
            <CloseOutlined
              v-if="tab.closable"
              class="tab-close"
              @click.stop="handleTabClose(tab)"
            />
          </div>
        </div>

        <button class="tabs-scroll-btn right" @click="scrollTabs(200)" :disabled="isAtMaxScroll">
          <RightOutlined />
        </button>
      </div>

      <div class="tabs-actions">
        <a-dropdown @click="handleDropdownClick">
          <a-button size="small" type="text">
            <MoreOutlined />
          </a-button>
          <template #overlay>
            <a-menu @click="handleTabAction">
              <a-menu-item key="closeCurrent">关闭当前</a-menu-item>
              <a-menu-item key="closeOther">关闭其他</a-menu-item>
              <a-menu-item key="closeLeft">关闭左侧</a-menu-item>
              <a-menu-item key="closeRight">关闭右侧</a-menu-item>
              <a-menu-divider />
              <a-menu-item key="closeAll">关闭全部</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
    </div>

    <!-- 右键菜单 -->
    <div
      v-if="contextMenu.visible"
      class="tabs-context-menu"
      :style="{ top: contextMenu.y + 'px', left: contextMenu.x + 'px' }"
      @click.stop
    >
      <div class="context-menu-item" @click="handleContextMenuClick('closeCurrent')">
        <CloseOutlined />
        <span>关闭当前</span>
      </div>
      <div class="context-menu-item" @click="handleContextMenuClick('closeOther')">
        <CloseCircleOutlined />
        <span>关闭其他</span>
      </div>
      <div class="context-menu-item" @click="handleContextMenuClick('closeLeft')">
        <LeftOutlined />
        <span>关闭左侧</span>
      </div>
      <div class="context-menu-item" @click="handleContextMenuClick('closeRight')">
        <RightOutlined />
        <span>关闭右侧</span>
      </div>
      <div class="context-menu-item" @click="handleContextMenuClick('closeAll')">
        <CloseCircleOutlined />
        <span>关闭全部</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTagsStore } from '@/stores/tags'
import {
  LeftOutlined,
  RightOutlined,
  CloseOutlined,
  MoreOutlined,
  CloseCircleOutlined,
  BarChartOutlined,
  UserOutlined,
  ClockCircleOutlined,
  SettingOutlined,
  CalendarOutlined,
  MoneyCollectOutlined,
  FileTextOutlined,
  ToolOutlined,
  TeamOutlined,
  MenuOutlined,
  DashboardOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const tagsStore = useTagsStore()

const tabsListRef = ref(null)
const scrollPosition = ref(0)
const contextMenu = ref({
  visible: false,
  x: 0,
  y: 0,
  targetTab: null
})

// 图标映射
const iconMap = {
  DataLine: BarChartOutlined,
  User: UserOutlined,
  Clock: ClockCircleOutlined,
  Setting: SettingOutlined,
  Calendar: CalendarOutlined,
  Money: MoneyCollectOutlined,
  Document: FileTextOutlined,
  Tools: ToolOutlined,
  UserFilled: TeamOutlined,
  Menu: MenuOutlined,
  OfficeBuilding: DashboardOutlined,
  DataAnalysis: BarChartOutlined,
  CircleClose: CloseCircleOutlined
}

// 计算属性
const tabs = computed(() => tagsStore.tabs)
const currentPath = computed(() => route.path)
const isAtMaxScroll = computed(() => {
  if (!tabsListRef.value) return true
  const maxScroll = tabsListRef.value.scrollWidth - tabsListRef.value.clientWidth
  return scrollPosition.value >= maxScroll - 10
})

// 方法
const handleTabClick = (tab) => {
  if (tab.path !== route.path) {
    router.push(tab.path)
  }
}

const handleTabClose = async (tab) => {
  if (!tab.closable) return

  const success = await tagsStore.removeTab(tab.path)
  if (success && route.path === tab.path) {
    const lastTab = tagsStore.tabs[tagsStore.tabs.length - 1]
    if (lastTab) {
      router.push(lastTab.path)
    }
  }
}

const scrollTabs = (offset) => {
  if (!tabsListRef.value) return

  const newScrollPosition = scrollPosition.value + offset
  const maxScroll = tabsListRef.value.scrollWidth - tabsListRef.value.clientWidth

  scrollPosition.value = Math.max(0, Math.min(newScrollPosition, maxScroll))
  tabsListRef.value.scrollLeft = scrollPosition.value
}

const handleContextMenu = (event, tab) => {
  contextMenu.value = {
    visible: true,
    x: event.clientX,
    y: event.clientY,
    targetTab: tab
  }
}

const handleContextMenuClick = (command) => {
  handleTabAction({ key: command })
  contextMenu.value.visible = false
}

const handleDropdownClick = (e) => {
  e.preventDefault()
}

const handleTabAction = async ({ key }) => {
  const currentPath = route.path
  const targetTab = contextMenu.value.targetTab?.path || currentPath

  switch (key) {
    case 'closeCurrent':
      if (targetTab !== '/') {
        await handleTabClose(tagsStore.tabs.find(t => t.path === targetTab))
      }
      break
    case 'closeOther':
      await tagsStore.closeOtherTabs(targetTab)
      if (targetTab !== currentPath) {
        router.push(targetTab)
      }
      break
    case 'closeLeft':
      await tagsStore.closeLeftTabs(targetTab)
      if (targetTab !== currentPath) {
        router.push(targetTab)
      }
      break
    case 'closeRight':
      await tagsStore.closeRightTabs(targetTab)
      if (targetTab !== currentPath) {
        router.push(targetTab)
      }
      break
    case 'closeAll':
      await tagsStore.closeAllTabs()
      router.push('/')
      break
  }

  contextMenu.value.visible = false
}

// 监听路由变化，自动添加标签页
watch(
  () => route.path,
  async (newPath) => {
    const { findMenuItemByPath } = await import('@/config/menu.config.js')
    const menuItem = findMenuItemByPath(newPath)

    if (menuItem) {
      await tagsStore.addTab({
        path: newPath,
        title: menuItem.title,
        icon: menuItem.icon,
        closable: newPath !== '/dashboard'
      })
    }

    await nextTick()
    scrollToCurrentTab()
  },
  { immediate: true }
)

// 滚动到当前标签页
const scrollToCurrentTab = () => {
  if (!tabsListRef.value) return

  const currentTabEl = tabsListRef.value.querySelector('.tab-item.active')
  if (currentTabEl) {
    const tabLeft = currentTabEl.offsetLeft
    const tabWidth = currentTabEl.offsetWidth
    const containerWidth = tabsListRef.value.clientWidth

    if (tabLeft < scrollPosition.value) {
      scrollPosition.value = tabLeft
      tabsListRef.value.scrollLeft = tabLeft
    } else if (tabLeft + tabWidth > scrollPosition.value + containerWidth) {
      scrollPosition.value = tabLeft + tabWidth - containerWidth
      tabsListRef.value.scrollLeft = tabLeft + tabWidth - containerWidth
    }
  }
}

// 点击外部关闭右键菜单
const handleClickOutside = () => {
  contextMenu.value.visible = false
}

// 生命周期
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  window.addEventListener('resize', scrollToCurrentTab)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  window.removeEventListener('resize', scrollToCurrentTab)
})
</script>

<style scoped lang="scss">
.tabs-container {
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
}

.tabs-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  padding: 0 12px;
}

.tabs-scroll-wrapper {
  display: flex;
  align-items: center;
  flex: 1;
  overflow: hidden;
  position: relative;
}

.tabs-scroll-btn {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 2px;
  transition: all 0.3s;
  color: rgba(0, 0, 0, 0.65);
  flex-shrink: 0;

  &:hover:not(:disabled) {
    background: #f5f5f5;
    color: #1890ff;
  }

  &:disabled {
    cursor: not-allowed;
    opacity: 0.5;
  }
}

.tabs-list {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
  padding: 4px 0;

  &::-webkit-scrollbar {
    display: none;
  }
}

.tab-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0 12px;
  height: 28px;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 2px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.65);
  position: relative;

  &:hover {
    color: #1890ff;
    border-color: #1890ff;
  }

  &.active {
    background: #1890ff;
    border-color: #1890ff;
    color: #fff;

    &:hover {
      color: #fff;
    }
  }

  .tab-icon {
    font-size: 12px;
  }

  .tab-title {
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .tab-close {
    width: 14px;
    height: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 2px;
    transition: all 0.2s;
    margin-left: 4px;

    &:hover {
      background: rgba(0, 0, 0, 0.15);
      color: #fff;
    }

    .active & {
      &:hover {
        background: rgba(255, 255, 255, 0.2);
        color: #fff;
      }
    }
  }
}

.tabs-actions {
  flex-shrink: 0;
  margin-left: 8px;
}

.tabs-context-menu {
  position: fixed;
  z-index: 1000;
  background: #fff;
  border-radius: 2px;
  box-shadow: 0 3px 6px -4px rgba(0, 0, 0, 0.12), 0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 9px 28px 8px rgba(0, 0, 0, 0.05);
  min-width: 120px;
  padding: 4px 0;

  .context-menu-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 12px;
    cursor: pointer;
    font-size: 13px;
    color: rgba(0, 0, 0, 0.85);
    transition: all 0.3s;

    &:hover {
      background: #f5f5f5;
      color: #1890ff;
    }

    svg {
      font-size: 14px;
    }
  }
}
</style>
