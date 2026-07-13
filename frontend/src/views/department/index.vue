<template>
  <div class="department-page">
    <a-card title="部门管理" :bordered="false">
      <a-result
        status="info"
        title="功能开发中"
        sub-title="部门管理功能正在开发中，请稍后再试。"
      >
        <template #extra>
          <a-button type="primary" @click="$router.back()">返回上一页</a-button>
        </template>
      </a-result>
    </a-card>
  </div>
</template>

<script setup>
// 部门管理页面
</script>

<style scoped lang="scss">
.department-page {
  padding: 24px;
}
</style>
