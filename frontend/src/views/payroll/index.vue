<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">工资管理</h1>
      <p class="page-description">管理员工工资和工资批次，处理薪资核算</p>
    </div>

    <div class="page-content">
      <!-- 快捷入口 -->
      <a-row :gutter="20" class="mb-20">
        <a-col :span="8">
          <a-card class="quick-card" @click="$router.push('/payroll/templates')">
            <div class="quick-content">
              <div class="quick-icon">
                <FileTextOutlined :style="{ fontSize: '24px' }" />
              </div>
              <div class="quick-info">
                <div class="quick-title">工资模板</div>
                <div class="quick-desc">配置工资项目和模板</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="8">
          <a-card class="quick-card" @click="$router.push('/payroll/batches')">
            <div class="quick-content">
              <div class="quick-icon">
                <FolderOutlined :style="{ fontSize: '24px' }" />
              </div>
              <div class="quick-info">
                <div class="quick-title">工资批次</div>
                <div class="quick-desc">创建和管理工资批次</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="8">
          <a-card class="quick-card" @click="$router.push('/payroll/salary-slip')">
            <div class="quick-content">
              <div class="quick-icon">
                <FileOutlined :style="{ fontSize: '24px' }" />
              </div>
              <div class="quick-info">
                <div class="quick-title">工资条</div>
                <div class="quick-desc">查看和导出工资条</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 本月工资概览 -->
      <a-card class="mb-20">
        <template #title>
          <div class="card-header">
            <span class="card-title">本月工资概览</span>
            <a-month-picker
              v-model:value="currentMonth"
              placeholder="选择月份"
              value-format="YYYY-MM"
              @change="loadMonthData"
            />
          </div>
        </template>

        <a-row :gutter="20">
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">应发总额</div>
              <div class="overview-value">¥{{ monthStats.totalGross }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">实发总额</div>
              <div class="overview-value success">¥{{ monthStats.totalNet }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">扣款总额</div>
              <div class="overview-value warning">¥{{ monthStats.totalDeduction }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">发放人数</div>
              <div class="overview-value">{{ monthStats.employeeCount }}</div>
            </div>
          </a-col>
        </a-row>
      </a-card>

      <!-- 最近工资批次 -->
      <a-card>
        <template #title>
          <div class="card-header">
            <span class="card-title">最近工资批次</span>
            <a-button type="primary" size="small" @click="handleCreateBatch">
              <template #icon><PlusOutlined /></template>
              创建批次
            </a-button>
          </div>
        </template>

        <a-table :data-source="batchList" :columns="batchColumns" bordered :pagination="false" />
      </a-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h } from 'vue'
import { message } from 'ant-design-vue'
import request from '@/utils/request'
import {
  FileTextOutlined,
  FolderOutlined,
  FileOutlined,
  PlusOutlined,
  ReloadOutlined,
  EyeOutlined,
  CalculatorOutlined,
  DownloadOutlined
} from '@ant-design/icons-vue'

const currentMonth = ref(new Date().toISOString().slice(0, 7))

const monthStats = reactive({
  totalGross: '1,234,567.89',
  totalNet: '987,654.32',
  totalDeduction: '246,913.57',
  employeeCount: 1180
})

const batchList = ref([
  {
    batchNo: 'BATCH202606001',
    salaryMonth: '2026-06',
    employeeCount: 1180,
    totalAmount: '987,654.32',
    status: '已发放',
    createdAt: '2026-06-05 10:30:00'
  },
  {
    batchNo: 'BATCH202605001',
    salaryMonth: '2026-05',
    employeeCount: 1175,
    totalAmount: '965,432.10',
    status: '已发放',
    createdAt: '2026-05-05 10:30:00'
  },
  {
    batchNo: 'BATCH202604001',
    salaryMonth: '2026-04',
    employeeCount: 1170,
    totalAmount: '954,321.00',
    status: '已发放',
    createdAt: '2026-04-05 10:30:00'
  }
])

const getBatchStatusType = (status) => {
  const typeMap = {
    '草稿': 'default',
    '已锁定': 'warning',
    '已发放': 'success'
  }
  return typeMap[status] || 'default'
}

const batchColumns = [
  {
    title: '批次号',
    dataIndex: 'batchNo',
    width: 150
  },
  {
    title: '工资月份',
    dataIndex: 'salaryMonth',
    width: 120
  },
  {
    title: '人数',
    dataIndex: 'employeeCount',
    width: 100
  },
  {
    title: '总额',
    dataIndex: 'totalAmount',
    width: 150,
    customRender: ({ record }) => '¥' + record.totalAmount
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({ record }) => h('a-tag', { type: getBatchStatusType(record.status) }, record.status)
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    width: 180
  },
  {
    title: '操作',
    width: 200,
    fixed: 'right',
    customRender: ({ record }) => h('div', { class: 'table-actions' }, [
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn view-btn',
        onClick: () => handleViewBatch(record)
      }, {
        icon: () => h(EyeOutlined),
        default: () => '查看'
      }),
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn calculate-btn',
        onClick: () => handleCalculate(record)
      }, {
        icon: () => h(CalculatorOutlined),
        default: () => '计算'
      }),
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn export-btn',
        onClick: () => handleExport(record)
      }, {
        icon: () => h(DownloadOutlined),
        default: () => '导出'
      })
    ])
  }
]

const loadMonthData = async () => {
  try {
    const result = await request.get(`/payroll/batch/month-stats`, {
      params: { month: currentMonth.value }
    })
    if (result.code === 200) {
      Object.assign(monthStats, result.data)
    }
  } catch (error) {
    console.error('加载月度数据失败:', error)
  }
}

const handleCreateBatch = () => {
  message.info('创建工资批次功能开发中')
}

const handleViewBatch = (row) => {
  message.info(`查看批次${row.batchNo}详情`)
}

const handleCalculate = async (row) => {
  try {
    const result = await request.post(`/payroll/batch/${row.id}/calculate`)
    if (result.code === 200) {
      message.success('工资计算成功')
      loadMonthData()
    } else {
      message.error(result.message || '计算失败')
    }
  } catch (error) {
    console.error('计算失败:', error)
    message.error('计算失败，请稍后重试')
  }
}

const handleExport = (row) => {
  message.info(`导出批次${row.batchNo}工资`)
}

onMounted(() => {
  loadMonthData()
})
</script>

<style scoped lang="scss">
.payroll-container {
  .search-bar {
    margin-bottom: 20px;
  }

  .quick-card {
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .quick-content {
      display: flex;
      align-items: center;
      gap: 16px;

      .quick-info {
        flex: 1;

        .quick-title {
          font-size: 16px;
          font-weight: 500;
          color: #262626;
          margin-bottom: 4px;
        }

        .quick-desc {
          font-size: 12px;
          color: #8c8c8c;
        }
      }
    }
  }

  .overview-item {
    text-align: center;
    padding: 20px 0;

    .overview-label {
      font-size: 14px;
      color: #8c8c8c;
      margin-bottom: 8px;
    }

    .overview-value {
      font-size: 28px;
      font-weight: 500;
      color: #262626;

      &.success {
        color: #52c41a;
      }

      &.warning {
        color: #faad14;
      }
    }
  }
}
</style>
