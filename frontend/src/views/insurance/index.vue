<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">社保管理</h1>
      <p class="page-description">管理员工社保和公积金，维护社保账户信息</p>
    </div>

    <div class="page-content">
      <!-- 快捷入口 -->
      <a-row :gutter="20" class="mb-20">
        <a-col :span="8">
          <a-card class="quick-card" @click="$router.push('/insurance/policies')">
            <div class="quick-content">
              <div class="quick-icon">
                <FileTextOutlined :style="{ fontSize: '24px' }" />
              </div>
              <div class="quick-info">
                <div class="quick-title">社保政策</div>
                <div class="quick-desc">配置社保政策和比例</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="8">
          <a-card class="quick-card" @click="$router.push('/insurance/accounts')">
            <div class="quick-content">
              <div class="quick-icon">
                <UserOutlined :style="{ fontSize: '24px' }" />
              </div>
              <div class="quick-info">
                <div class="quick-title">员工社保</div>
                <div class="quick-desc">管理员工社保账户</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="8">
          <a-card class="quick-card" @click="$router.push('/insurance/monthly')">
            <div class="quick-content">
              <div class="quick-icon">
                <CalendarOutlined :style="{ fontSize: '24px' }" />
              </div>
              <div class="quick-info">
                <div class="quick-title">月度明细</div>
                <div class="quick-desc">查看月度社保明细</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 本月社保概览 -->
      <a-card class="mb-20">
        <template #title>
          <div class="card-header">
            <span class="card-title">本月社保概览</span>
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
              <div class="overview-label">个人缴纳</div>
              <div class="overview-value">¥{{ monthStats.personalTotal }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">企业缴纳</div>
              <div class="overview-value success">¥{{ monthStats.companyTotal }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">缴纳总额</div>
              <div class="overview-value warning">¥{{ monthStats.total }}</div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="overview-item">
              <div class="overview-label">参保人数</div>
              <div class="overview-value">{{ monthStats.employeeCount }}</div>
            </div>
          </a-col>
        </a-row>
      </a-card>

      <!-- 社保明细列表 -->
      <a-card>
        <template #title>
          <div class="card-header">
            <span class="card-title">社保明细</span>
            <a-button type="primary" size="small" @click="handleExport">
              <template #icon><DownloadOutlined /></template>
              导出明细
            </a-button>
          </div>
        </template>

        <a-table :data-source="detailList" :columns="detailColumns" bordered :pagination="false" />
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
  UserOutlined,
  CalendarOutlined,
  DownloadOutlined,
  EyeOutlined,
  EditOutlined
} from '@ant-design/icons-vue'

const currentMonth = ref(new Date().toISOString().slice(0, 7))

const monthStats = reactive({
  personalTotal: '123,456.78',
  companyTotal: '246,913.56',
  total: '370,370.34',
  employeeCount: 1180
})

const detailList = ref([
  {
    empNo: 'EMP001',
    name: '张三',
    deptName: '技术部',
    pensionBase: 10000,
    medicalBase: 10000,
    housingFundBase: 12000,
    personalAmount: '1,200.00',
    companyAmount: '2,400.00',
    totalAmount: '3,600.00'
  },
  {
    empNo: 'EMP002',
    name: '李四',
    deptName: '销售部',
    pensionBase: 8000,
    medicalBase: 8000,
    housingFundBase: 10000,
    personalAmount: '1,000.00',
    companyAmount: '2,000.00',
    totalAmount: '3,000.00'
  },
  {
    empNo: 'EMP003',
    name: '王五',
    deptName: '市场部',
    pensionBase: 9000,
    medicalBase: 9000,
    housingFundBase: 11000,
    personalAmount: '1,100.00',
    companyAmount: '2,200.00',
    totalAmount: '3,300.00'
  }
])

const detailColumns = [
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
    title: '养老基数',
    dataIndex: 'pensionBase',
    width: 100
  },
  {
    title: '医疗基数',
    dataIndex: 'medicalBase',
    width: 100
  },
  {
    title: '公积金基数',
    dataIndex: 'housingFundBase',
    width: 120
  },
  {
    title: '个人缴纳',
    dataIndex: 'personalAmount',
    width: 120,
    customRender: ({ record }) => '¥' + record.personalAmount
  },
  {
    title: '企业缴纳',
    dataIndex: 'companyAmount',
    width: 120,
    customRender: ({ record }) => '¥' + record.companyAmount
  },
  {
    title: '合计',
    dataIndex: 'totalAmount',
    width: 120,
    customRender: ({ record }) => '¥' + record.totalAmount
  },
  {
    title: '操作',
    width: 150,
    fixed: 'right',
    customRender: ({ record }) => h('div', { class: 'table-actions' }, [
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn view-btn',
        onClick: () => handleViewDetail(record)
      }, {
        icon: () => h(EyeOutlined),
        default: () => '详情'
      }),
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn edit-btn',
        onClick: () => handleEdit(record)
      }, {
        icon: () => h(EditOutlined),
        default: () => '编辑'
      })
    ])
  }
]

const loadMonthData = async () => {
  try {
    // 请求社保政策接口的月度统计
    const result = await request.get(`/payroll/insurance-policy/month-stats`, {
      params: { month: currentMonth.value }
    })
    if (result.code === 200) {
      Object.assign(monthStats, result.data)
    }
  } catch (error) {
    console.error('加载月度数据失败:', error)
  }
}

const handleExport = () => {
  message.info('导出社保明细')
}

const handleViewDetail = (row) => {
  message.info(`查看${row.name}的社保详情`)
}

const handleEdit = (row) => {
  message.info(`编辑${row.name}的社保信息`)
}

onMounted(() => {
  loadMonthData()
})
</script>

<style scoped lang="scss">
.insurance-container {
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
