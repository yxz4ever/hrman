import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useTagsStore = defineStore('tags', () => {
  // 状态
  const tabs = ref([
    {
      path: '/',
      title: '仪表盘',
      icon: 'DataAnalysis',
      closable: false
    }
  ])
  const activeTab = ref('/')

  // 计算属性
  const tabCount = computed(() => tabs.value.length)

  // 方法
  const addTab = async (tab) => {
    const existingTab = tabs.value.find(t => t.path === tab.path)
    if (existingTab) {
      return true
    }

    tabs.value.push({
      ...tab,
      closable: tab.closable !== false
    })
    activeTab.value = tab.path
    return true
  }

  const removeTab = async (path) => {
    const index = tabs.value.findIndex(t => t.path === path)
    if (index === -1) return false

    const tab = tabs.value[index]
    if (!tab.closable) return false

    tabs.value.splice(index, 1)
    return true
  }

  const closeOtherTabs = async (targetPath) => {
    tabs.value = tabs.value.filter(tab => 
      tab.path === targetPath || !tab.closable
    )
    return true
  }

  const closeLeftTabs = async (targetPath) => {
    const index = tabs.value.findIndex(t => t.path === targetPath)
    if (index <= 1) return true // 第一个标签页无法关闭左侧

    // 保留第一个不可关闭的标签页
    const firstClosableIndex = tabs.value.findIndex(tab => tab.closable)
    const startIndex = firstClosableIndex === -1 ? 1 : firstClosableIndex
    
    tabs.value = tabs.value.slice(0, startIndex).concat(tabs.value.slice(index))
    return true
  }

  const closeRightTabs = async (targetPath) => {
    const index = tabs.value.findIndex(t => t.path === targetPath)
    if (index === -1 || index === tabs.value.length - 1) return true

    // 从后往前删除可关闭的标签页
    for (let i = tabs.value.length - 1; i > index; i--) {
      if (tabs.value[i].closable) {
        tabs.value.splice(i, 1)
      }
    }
    return true
  }

  const closeAllTabs = async () => {
    // 保留首页
    const homeTab = tabs.value.find(t => t.path === '/')
    tabs.value = homeTab ? [homeTab] : []
    return true
  }

  const setActiveTab = (path) => {
    activeTab.value = path
  }

  const updateTabTitle = (path, title) => {
    const tab = tabs.value.find(t => t.path === path)
    if (tab) {
      tab.title = title
    }
  }

  const isTabActive = (path) => {
    return activeTab.value === path
  }

  const getTabIndex = (path) => {
    return tabs.value.findIndex(t => t.path === path)
  }

  // 重置状态
  const resetTags = () => {
    tabs.value = [
      {
        path: '/',
        title: '仪表盘',
        icon: 'DataAnalysis',
        closable: false
      }
    ]
    activeTab.value = '/'
  }

  return {
    // 状态
    tabs,
    activeTab,
    tabCount,
    // 方法
    addTab,
    removeTab,
    closeOtherTabs,
    closeLeftTabs,
    closeRightTabs,
    closeAllTabs,
    setActiveTab,
    updateTabTitle,
    isTabActive,
    getTabIndex,
    resetTags
  }
})
