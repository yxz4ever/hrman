<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">考勤管理</h1>
      <p class="page-description">管理员工考勤记录和规则，监控考勤数据</p>
    </div>

    <div class="page-content">
      <!-- 快捷入口 -->
      <a-row :gutter="20" class="mb-20">
        <a-col :span="8">
          <a-card class="quick-card" @click="$router.push('/attendance/rules')">
            <div class="quick-content">
              <div class="quick-icon">
                <SettingOutlined :style="{ fontSize: '24px' }" />
              </div>
              <div class="quick-info">
                <div class="quick-title">考勤规则</div>
                <div class="quick-desc">配置考勤规则和规则组</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="8">
          <a-card class="quick-card" @click="$router.push('/attendance/daily')">
            <div class="quick-content">
              <div class="quick-icon">
                <CalendarOutlined :style="{ fontSize: '24px' }" />
              </div>
              <div class="quick-info">
                <div class="quick-title">日考勤</div>
                <div class="quick-desc">查看每日考勤结果</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="8">
          <a-card class="quick-card">
            <div class="quick-content">
              <div class="quick-icon">
                <LineChartOutlined :style="{ fontSize: '24px' }" />
              </div>
              <div class="quick-info">
                <div class="quick-title">考勤报表</div>
                <div class="quick-desc">考勤数据统计分析</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 今日考勤概览 -->
      <a-card class="mb-20">
        <template #title>
          <div class="card-header">
            <span class="card-title">今日考勤概览</span>
            <a-date-picker
              v-model:value="currentDate"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              @change="loadTodayData"
            />
          </div>
        </template>

        <a-row :gutter="20">
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">应出勤</div>
              <div class="overview-value">{{ todayStats.shouldAttend }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">实出勤</div>
              <div class="overview-value success">{{ todayStats.actualAttend }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">迟到</div>
              <div class="overview-value warning">{{ todayStats.late }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">缺卡</div>
              <div class="overview-value danger">{{ todayStats.missing }}</div>
            </div>
          </a-col>
        </a-row>
      </a-card>

      <!-- 考勤异常列表 -->
      <a-card>
        <template #title>
          <div class="card-header">
            <span class="card-title">今日考勤异常</span>
            <a-button type="primary" size="small" @click="loadExceptions">
              <template #icon><ReloadOutlined /></template>
              刷新
            </a-button>
          </div>
        </template>

        <a-table :data-source="exceptionList" :columns="exceptionColumns" bordered :pagination="false" />
      </a-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  SettingOutlined,
  CalendarOutlined,
  LineChartOutlined,
  ReloadOutlined,
  CheckCircleOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'

const currentDate = ref(dayjs().format('YYYY-MM-DD'))

const todayStats = reactive({
  shouldAttend: 1180,
  actualAttend: 1138,
  late: 28,
  missing: 14
})

const exceptionList = ref([
  {
    empNo: 'EMP001',
    name: '张三',
    deptName: '技术部',
    exceptionType: '迟到',
    exceptionDesc: '迟到 15 分钟'
  },
  {
    empNo: 'EMP002',
    name: '李四',
    deptName: '销售部',
    exceptionType: '缺卡',
    exceptionDesc: '缺少上班打卡'
  },
  {
    empNo: 'EMP003',
    name: '王五',
    deptName: '市场部',
    exceptionType: '早退',
    exceptionDesc: '早退 10 分钟'
  }
])

const getExceptionType = (type) => {
  const typeMap = {
    '迟到': 'warning',
    '早退': 'warning',
    '缺卡': 'danger',
    '旷工': 'danger'
  }
  return typeMap[type] || 'default'
}

const exceptionColumns = [
  {
    title: '工号',
    dataIndex: 'empNo',
    width: 120
  },
  {
    title: '姓名',
    dataIndex: 'name',
    width: 100
  },
  {
    title: '部门',
    dataIndex: 'deptName',
    width: 120
  },
  {
    title: '异常类型',
    dataIndex: 'exceptionType',
    width: 100,
    customRender: ({ record }) => h('a-tag', { type: getExceptionType(record.exceptionType) }, record.exceptionType)
  },
  {
    title: '异常说明',
    dataIndex: 'exceptionDesc'
  },
  {
    title: '操作',
    width: 150,
    customRender: ({ record }) => h('div', { class: 'table-actions' }, [
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn process-btn',
        onClick: () => handleHandleException(record)
      }, {
        icon: () => h(CheckCircleOutlined),
        default: () => '处理'
      }),
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn view-btn',
        onClick: () => handleViewDetail(record)
      }, {
        icon: () => h(EyeOutlined),
        default: () => '详情'
      })
    ])
  }
]

const loadTodayData = () => {
  message.info(`加载${currentDate.value}的考勤数据`)
}

const loadExceptions = () => {
  message.success('刷新成功')
}

const handleHandleException = (row) => {
  message.info(`处理${row.name}的异常`)
}

const handleViewDetail = (row) => {
  message.info(`查看${row.name}的考勤详情`)
}

onMounted(() => {
  loadTodayData()
})
</script>

<style lang="scss">
/* 考勤页面特有样式 */
.attendance-container {
  /* 快捷卡片 - vue-element-admin 风格 */
  .quick-card {
    cursor: pointer;
    transition: all 0.3s;
    border: none !important;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08) !important;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 21, 41, 0.15) !important;
    }

    .quick-content {
      display: flex;
      align-items: center;
      gap: 16px;

      .quick-info {
        flex: 1;

        .quick-title {
          font-size: 15px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 4px;
        }

        .quick-desc {
          font-size: 13px;
          color: #909399;
        }
      }
    }
  }

  /* 概览项样式 */
  .overview-item {
    text-align: center;
    padding: 20px 0;

    .overview-label {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }

    .overview-value {
      font-size: 32px;
      font-weight: 600;
      color: #303133;

      &.success {
        color: #67c23a;
      }

      &.warning {
        color: #e6a23c;
      }

      &.danger {
        color: #f56c6c;
      }
    }
  }

  .mb-20 {
    margin-bottom: 20px;
  }
}
</style>
