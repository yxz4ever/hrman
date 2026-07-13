<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">日考勤查询</h1>
      <p class="page-description">查询和统计员工每日考勤结果，支持多维度筛选</p>
    </div>

    <div class="page-content">
      <!-- 搜索栏 -->
      <a-form :model="searchForm" layout="inline" class="search-bar">
        <a-form-item label="日期">
          <a-date-picker
            v-model:value="searchForm.workDate"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </a-form-item>
        <a-form-item label="员工">
          <a-input v-model:value="searchForm.empNo" placeholder="工号/姓名" allow-clear />
        </a-form-item>
        <a-form-item label="部门">
          <a-select v-model:value="searchForm.deptId" placeholder="请选择部门" allow-clear>
            <a-select-option label="技术部" :value="1">技术部</a-select-option>
            <a-select-option label="销售部" :value="2">销售部</a-select-option>
            <a-select-option label="市场部" :value="3">市场部</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="searchForm.status" placeholder="请选择状态" allow-clear>
            <a-select-option value="正常">正常</a-select-option>
            <a-select-option value="迟到">迟到</a-select-option>
            <a-select-option value="早退">早退</a-select-option>
            <a-select-option value="缺卡">缺卡</a-select-option>
            <a-select-option value="旷工">旷工</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <template #icon><SearchOutlined /></template>
            搜索
          </a-button>
          <a-button @click="handleReset">
            <template #icon><ReloadOutlined /></template>
            重置
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 操作栏 -->
      <div class="action-bar">
        <a-button type="primary" class="action-btn primary">
          <template #icon><FilterOutlined /></template>
          计算考勤
        </a-button>
        <a-button type="success" class="action-btn success">
          <template #icon><UploadOutlined /></template>
          批量导入
        </a-button>
        <a-button type="warning" class="action-btn warning">
          <template #icon><DownloadOutlined /></template>
          数据导出
        </a-button>
        <a-button type="info" class="action-btn info">
          <template #icon><EditOutlined /></template>
          批量修正
        </a-button>
      </div>

      <!-- 表格 -->
      <div class="table-container">
        <a-spin :spinning="loading">
          <a-table
            :columns="columns"
            :data-source="tableData"
            :row-key="record => record.workDate + record.empNo"
            bordered
            :pagination="false"
          />
        </a-spin>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <a-pagination
          v-model:current="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          show-size-changer
          show-quick-jumper
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, h } from 'vue'
import { message } from 'ant-design-vue'
import request from '@/utils/request'
import {
  SearchOutlined,
  ReloadOutlined,
  FilterOutlined,
  DownloadOutlined,
  UploadOutlined,
  EditOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'

const loading = ref(false)
const tableData = ref([
  {
    workDate: '2024-01-15',
    empNo: 'EMP001',
    name: '张三',
    deptName: '技术部',
    attendanceStatus: '正常',
    firstClockIn: '2024-01-15 08:55:00',
    lastClockOut: '2024-01-15 18:05:00',
    workMinutes: 510,
    lateMinutes: 0,
    earlyLeaveMinutes: 0,
    ruleExplanation: '固定班制：上班 09:00-18:00, 实际 08:55-18:05, 工作时长 510 分钟'
  }
])

const searchForm = reactive({
  workDate: new Date().toISOString().split('T')[0],
  empNo: '',
  deptId: null,
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getStatusType = (status) => {
  const typeMap = {
    '正常': 'success',
    '迟到': 'warning',
    '早退': 'warning',
    '缺卡': 'danger',
    '旷工': 'danger'
  }
  return typeMap[status] || 'default'
}

const columns = [
  {
    title: '日期',
    dataIndex: 'workDate',
    width: 120
  },
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
    title: '考勤状态',
    dataIndex: 'attendanceStatus',
    width: 100,
    customRender: ({ record }) => h('a-tag', { type: getStatusType(record.attendanceStatus) }, record.attendanceStatus)
  },
  {
    title: '首次打卡',
    dataIndex: 'firstClockIn',
    width: 160
  },
  {
    title: '最后打卡',
    dataIndex: 'lastClockOut',
    width: 160
  },
  {
    title: '工作时长 (分钟)',
    dataIndex: 'workMinutes',
    width: 120
  },
  {
    title: '迟到 (分钟)',
    dataIndex: 'lateMinutes',
    width: 100
  },
  {
    title: '早退 (分钟)',
    dataIndex: 'earlyLeaveMinutes',
    width: 100
  },
  {
    title: '规则说明',
    dataIndex: 'ruleExplanation',
    ellipsis: true
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
        onClick: () => handleView(record)
      }, {
        icon: () => h(EyeOutlined),
        default: () => '详情'
      }),
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn adjust-btn',
        danger: true,
        onClick: () => handleAdjust(record)
      }, {
        icon: () => h(EditOutlined),
        default: () => '调整'
      })
    ])
  }
]

const handleSearch = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      workDate: searchForm.workDate,
      empNo: searchForm.empNo,
      deptId: searchForm.deptId,
      attendanceStatus: searchForm.status
    }

    const result = await request.post('/attendance/daily-result/page', params)
    if (result.code === 200) {
      tableData.value = result.data.records
      pagination.total = result.data.total
    } else {
      message.error(result.message || '查询失败')
    }
  } catch (error) {
    console.error('查询失败:', error)
    message.error('查询失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  Object.assign(searchForm, {
    workDate: new Date().toISOString().split('T')[0],
    empNo: '',
    deptId: null,
    status: ''
  })
  handleSearch()
}

const handleView = (row) => {
  message.info(`查看员工${row.name}的考勤详情`)
}

const handleAdjust = (row) => {
  message.info(`调整员工${row.name}的考勤记录`)
}
</script>

<style lang="scss">
/* 日考勤页面特有样式 */
.daily-container {
  .search-bar {
    margin-bottom: 20px;
  }
}
</style>
