<template>
  <div class="page-container fade-in">
    <!-- 页面头部 -->
    <div class="page-header" :style="headerStyle">
      <h1 class="page-title">{{ title }}</h1>
      <p class="page-description">{{ description }}</p>
    </div>

    <!-- 页面内容 -->
    <div class="page-content">
      <!-- 搜索栏插槽 -->
      <div v-if="$slots.search" class="search-form">
        <slot name="search"></slot>
      </div>

      <!-- 操作栏插槽 -->
      <div v-if="$slots.actions" class="action-bar">
        <slot name="actions"></slot>
      </div>

      <!-- 主要内容插槽 -->
      <slot name="default"></slot>

      <!-- 分页插槽 -->
      <div v-if="$slots.pagination" class="pagination-container">
        <slot name="pagination"></slot>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  headerGradient: {
    type: String,
    default: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  }
})

const headerStyle = computed(() => ({
  background: props.headerGradient
}))
</script>

<script>
export default {
  name: 'PageLayout'
}
</script>

<style scoped>
/* 组件特定样式 */
.page-container {
  min-height: calc(100vh - 60px);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>