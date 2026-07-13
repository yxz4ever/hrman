<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">工作台</h1>
      <p class="page-description">欢迎使用人力资源管理系统，实时掌握企业人力资源状况</p>
    </div>

    <div class="page-content">
      <!-- 统计卡片 -->
      <a-row :gutter="20" class="mb-20">
        <a-col :span="6">
          <a-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: rgba(24, 144, 255, 0.1)">
                <UserOutlined :style="{ fontSize: '32px', color: '#1890ff' }" />
              </div>
              <div class="stat-info">
                <div class="stat-value">1,234</div>
                <div class="stat-label">员工总数</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: rgba(82, 196, 26, 0.1)">
                <CheckCircleOutlined :style="{ fontSize: '32px', color: '#52c41a' }" />
              </div>
              <div class="stat-info">
                <div class="stat-value">1,180</div>
                <div class="stat-label">在职员工</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: rgba(250, 173, 20, 0.1)">
                <ClockCircleOutlined :style="{ fontSize: '32px', color: '#faad14' }" />
              </div>
              <div class="stat-info">
                <div class="stat-value">96.5%</div>
                <div class="stat-label">今日出勤率</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: rgba(255, 77, 79, 0.1)">
                <FileOutlined :style="{ fontSize: '32px', color: '#ff4d4f' }" />
              </div>
              <div class="stat-info">
                <div class="stat-value">12</div>
                <div class="stat-label">待审批事项</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 图表区域 -->
      <a-row :gutter="20" class="mb-20">
        <a-col :span="16">
          <a-card>
            <template #title>
              <div class="card-header">
                <span class="card-title">员工入离职趋势</span>
              </div>
            </template>
            <div ref="trendChartRef" style="height: 350px"></div>
          </a-card>
        </a-col>

        <a-col :span="8">
          <a-card>
            <template #title>
              <div class="card-header">
                <span class="card-title">部门人数分布</span>
              </div>
            </template>
            <div ref="deptChartRef" style="height: 350px"></div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 快捷操作 -->
      <a-row :gutter="20">
        <a-col :span="24">
          <a-card>
            <template #title>
              <div class="card-header">
                <span class="card-title">快捷操作</span>
              </div>
            </template>
            <div class="quick-actions">
              <a-button type="primary" @click="$router.push('/employee')">
                <template #icon><PlusOutlined /></template>
                新增员工
              </a-button>
              <a-button type="success" @click="$router.push('/attendance')">
                <template #icon><UploadOutlined /></template>
                导入考勤
              </a-button>
              <a-button type="warning" @click="$router.push('/payroll')">
                <template #icon><MoneyCollectOutlined /></template>
                计算工资
              </a-button>
              <a-button type="info" @click="$router.push('/attendance/rules')">
                <template #icon><SettingOutlined /></template>
                考勤设置
              </a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  UserOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  FileOutlined,
  PlusOutlined,
  UploadOutlined,
  MoneyCollectOutlined,
  SettingOutlined
} from '@ant-design/icons-vue'

const trendChartRef = ref(null)
const deptChartRef = ref(null)
let trendChart = null
let deptChart = null

onMounted(() => {
  initTrendChart()
  initDeptChart()

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (trendChart) {
    trendChart.dispose()
  }
  if (deptChart) {
    deptChart.dispose()
  }
  window.removeEventListener('resize', handleResize)
})

const handleResize = () => {
  trendChart?.resize()
  deptChart?.resize()
}

const initTrendChart = () => {
  trendChart = echarts.init(trendChartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['入职', '离职']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1 月', '2 月', '3 月', '4 月', '5 月', '6 月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '入职',
        type: 'line',
        smooth: true,
        data: [12, 18, 15, 22, 19, 25],
        itemStyle: {
          color: '#1890ff'
        }
      },
      {
        name: '离职',
        type: 'line',
        smooth: true,
        data: [5, 8, 6, 9, 7, 10],
        itemStyle: {
          color: '#ff4d4f'
        }
      }
    ]
  }

  trendChart.setOption(option)
}

const initDeptChart = () => {
  deptChart = echarts.init(deptChartRef.value)

  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '部门人数',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 320, name: '技术部' },
          { value: 280, name: '销售部' },
          { value: 250, name: '市场部' },
          { value: 180, name: '人事部' },
          { value: 120, name: '财务部' },
          { value: 84, name: '行政部' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  deptChart.setOption(option)
}
</script>

<style scoped lang="scss">
.dashboard-container {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 64px;
        height: 64px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 28px;
          font-weight: 500;
          color: #262626;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #8c8c8c;
        }
      }
    }
  }

  .quick-actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }
}
</style>
