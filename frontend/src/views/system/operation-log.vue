<template>
  <div class="operation-log-page">
    <a-card :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" class="search-form">
        <a-form-item label="模块标题">
          <a-input v-model:value="searchForm.title" placeholder="请输入模块标题" allow-clear />
        </a-form-item>
        <a-form-item label="操作人员">
          <a-input v-model:value="searchForm.operName" placeholder="请输入操作人员" allow-clear />
        </a-form-item>
        <a-form-item label="业务类型">
          <a-select v-model:value="searchForm.businessType" placeholder="请选择" style="width: 150px" allow-clear>
            <a-select-option :value="0">其它</a-select-option>
            <a-select-option :value="1">新增</a-select-option>
            <a-select-option :value="2">修改</a-select-option>
            <a-select-option :value="3">删除</a-select-option>
            <a-select-option :value="4">导出</a-select-option>
            <a-select-option :value="5">导入</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="操作状态">
          <a-select v-model:value="searchForm.status" placeholder="请选择" style="width: 150px" allow-clear>
            <a-select-option :value="0">正常</a-select-option>
            <a-select-option :value="1">异常</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="操作时间">
          <a-range-picker v-model:value="searchForm.dateRange" style="width: 280px" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <template #icon><SearchOutlined /></template>
            搜索
          </a-button>
          <a-button style="margin-left: 8px" @click="handleReset">
            <template #icon><ReloadOutlined /></template>
            重置
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 操作栏 -->
      <div class="table-toolbar">
        <a-popconfirm title="确定清空所有操作日志吗？" @confirm="handleClearLog">
          <a-button danger>
            <template #icon><DeleteOutlined /></template>
            清空日志
          </a-button>
        </a-popconfirm>
        <a-button style="margin-left: 8px" @click="handleExport">
          <template #icon><DownloadOutlined /></template>
          导出
        </a-button>
      </div>

      <!-- 操作日志表格 -->
      <a-table
        :columns="columns"
        :data-source="logList"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'businessType'">
            <a-tag :color="getBusinessTypeColor(record.businessType)">
              {{ getBusinessTypeText(record.businessType) }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 0 ? 'green' : 'red'">
              {{ record.status === 0 ? '正常' : '异常' }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'operTime'">
            {{ formatTime(record.operTime) }}
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a @click="handleViewDetail(record)">详情</a>
          </template>
        </template>
      </a-table>

      <!-- 日志详情弹窗 -->
      <a-modal
        v-model:open="detailModalVisible"
        title="操作日志详情"
        width="800px"
        :footer="null"
      >
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="模块标题">{{ currentLog.title }}</a-descriptions-item>
          <a-descriptions-item label="业务类型">
            <a-tag :color="getBusinessTypeColor(currentLog.businessType)">
              {{ getBusinessTypeText(currentLog.businessType) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="操作方法">{{ currentLog.method }}</a-descriptions-item>
          <a-descriptions-item label="请求方式">{{ currentLog.requestMethod }}</a-descriptions-item>
          <a-descriptions-item label="操作人员">{{ currentLog.operName }}</a-descriptions-item>
          <a-descriptions-item label="部门名称">{{ currentLog.deptName || '-' }}</a-descriptions-item>
          <a-descriptions-item label="请求 URL" :span="2">{{ currentLog.operUrl }}</a-descriptions-item>
          <a-descriptions-item label="IP 地址">{{ currentLog.operIp }}</a-descriptions-item>
          <a-descriptions-item label="操作地点">{{ currentLog.operLocation || '-' }}</a-descriptions-item>
          <a-descriptions-item label="操作状态">
            <a-tag :color="currentLog.status === 0 ? 'green' : 'red'">
              {{ currentLog.status === 0 ? '正常' : '异常' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="消耗时间">{{ currentLog.costTime }} ms</a-descriptions-item>
          <a-descriptions-item label="操作时间" :span="2">{{ formatTime(currentLog.operTime) }}</a-descriptions-item>
          <a-descriptions-item label="请求参数" :span="2">
            <a-input-textarea v-model:value="currentLog.param" readonly :rows="3" />
          </a-descriptions-item>
          <a-descriptions-item label="返回数据" :span="2">
            <a-input-textarea v-model:value="currentLog.jsonResult" readonly :rows="4" />
          </a-descriptions-item>
          <a-descriptions-item v-if="currentLog.status === 1" label="错误消息" :span="2">
            <a-input-textarea v-model:value="currentLog.errorMsg" readonly :rows="4" />
          </a-descriptions-item>
        </a-descriptions>
      </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, DeleteOutlined, DownloadOutlined } from '@ant-design/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

// 搜索表单
const searchForm = reactive({
  title: '',
  operName: '',
  businessType: null,
  status: null,
  dateRange: null
})

// 操作日志列表
const logList = ref([])
const loading = ref(false)
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 表格列
const columns = [
  { title: '模块标题', dataIndex: 'title', ellipsis: true, width: 200 },
  { title: '操作人员', dataIndex: 'operName', width: 120 },
  { title: '业务类型', dataIndex: 'businessType', width: 100 },
  { title: '请求方式', dataIndex: 'requestMethod', width: 100 },
  { title: 'IP 地址', dataIndex: 'operIp', width: 150 },
  { title: '操作状态', dataIndex: 'status', width: 100 },
  { title: '消耗时间', dataIndex: 'costTime', width: 120, customRender: ({ text }) => `${text} ms` },
  { title: '操作时间', dataIndex: 'operTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 100, fixed: 'right' }
]

// 详情弹窗
const detailModalVisible = ref(false)
const currentLog = ref({})

// 业务类型文本
const getBusinessTypeText = (type) => {
  const map = {
    0: '其它',
    1: '新增',
    2: '修改',
    3: '删除',
    4: '导出',
    5: '导入'
  }
  return map[type] || '未知'
}

// 业务类型颜色
const getBusinessTypeColor = (type) => {
  const map = {
    0: 'default',
    1: 'blue',
    2: 'cyan',
    3: 'orange',
    4: 'green',
    5: 'purple'
  }
  return map[type] || 'default'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 加载操作日志列表
const loadLogList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    }
    
    if (searchForm.title) params.title = searchForm.title
    if (searchForm.operName) params.operName = searchForm.operName
    if (searchForm.businessType !== null) params.businessType = searchForm.businessType
    if (searchForm.status !== null) params.status = searchForm.status
    if (searchForm.dateRange && searchForm.dateRange[0] && searchForm.dateRange[1]) {
      params.startTime = dayjs(searchForm.dateRange[0]).format('YYYY-MM-DD HH:mm:ss')
      params.endTime = dayjs(searchForm.dateRange[1]).format('YYYY-MM-DD HH:mm:ss')
    }
    
    const res = await request.get('/system/log/list', { params })
    if (res.code === 200) {
      logList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('加载操作日志列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadLogList()
}

// 重置
const handleReset = () => {
  searchForm.title = ''
  searchForm.operName = ''
  searchForm.businessType = null
  searchForm.status = null
  searchForm.dateRange = null
  handleSearch()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadLogList()
}

// 查看详情
const handleViewDetail = (record) => {
  currentLog.value = { ...record }
  detailModalVisible.value = true
}

// 清空日志
const handleClearLog = async () => {
  try {
    const res = await request.delete('/system/log/clear')
    if (res.code === 200) {
      message.success('清空成功')
      loadLogList()
    }
  } catch (error) {
    console.error('清空失败:', error)
  }
}

// 导出
const handleExport = () => {
  message.info('导出功能开发中')
}

onMounted(() => {
  loadLogList()
})
</script>

<style scoped lang="scss">
.operation-log-page {
  padding: 24px;

  .search-form {
    margin-bottom: 16px;
  }

  .table-toolbar {
    margin-bottom: 16px;
  }
}
</style>
