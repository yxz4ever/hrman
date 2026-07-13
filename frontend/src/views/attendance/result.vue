<template>
  <div class="attendance-result-page">
    <a-card title="考勤结果" :bordered="false">
      <a-result
        status="info"
        title="功能开发中"
        sub-title="考勤结果功能正在开发中，请稍后再试。"
      >
        <template #extra>
          <a-button type="primary" @click="$router.back()">返回上一页</a-button>
        </template>
      </a-result>
    </a-card>
  </div>
</template>

<script setup>
// 考勤结果页面
</script>

<style scoped lang="scss">
.attendance-result-page {
  padding: 24px;
}
</style>
