<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">考勤规则管理</h1>
      <p class="page-description">配置和管理考勤规则，支持多规则组合判定</p>
    </div>

    <div class="page-content">
      <!-- 搜索栏 -->
      <a-form :model="searchForm" layout="inline" class="search-bar">
        <a-form-item label="规则名称">
          <a-input v-model:value="searchForm.ruleName" placeholder="请输入规则名称" allow-clear />
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
        <a-button type="primary" @click="handleAdd" class="action-btn primary">
          <template #icon><PlusOutlined /></template>
          新增规则
        </a-button>
        <a-button type="success" class="action-btn success">
          <template #icon><CopyOutlined /></template>
          复制规则
        </a-button>
        <a-button type="warning" class="action-btn warning">
          <template #icon><UploadOutlined /></template>
          导入规则
        </a-button>
        <a-button type="info" class="action-btn info">
          <template #icon><DownloadOutlined /></template>
          导出规则
        </a-button>
      </div>

      <!-- 规则列表 -->
      <div class="table-container">
        <a-spin :spinning="loading">
          <a-table
            :columns="columns"
            :data-source="ruleList"
            :row-key="record => record.id"
            bordered
            :pagination="false"
          />
        </a-spin>
      </div>
    </div>

    <!-- 规则对话框 -->
    <a-modal
      v-model:open="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @ok="handleSubmit"
      @cancel="dialogVisible = false"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-col :label-col="{ span: 8 }"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="规则编码" name="ruleCode">
              <a-input v-model:value="formData.ruleCode" placeholder="请输入规则编码" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="规则名称" name="ruleName">
              <a-input v-model:value="formData.ruleName" placeholder="请输入规则名称" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="规则类型" name="ruleType">
              <a-select v-model:value="formData.ruleType" placeholder="请选择规则类型" @change="handleRuleTypeChange">
                <a-select-option value="FIXED_TIME">固定班制</a-select-option>
                <a-select-option value="FLEXIBLE">弹性工时</a-select-option>
                <a-select-option value="WORK_HOURS">按工时制</a-select-option>
                <a-select-option value="SHIFT">排班制</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="优先级" name="priority">
              <a-input-number v-model:value="formData.priority" :min="0" :max="100" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>

        <!-- 固定班制配置 -->
        <template v-if="formData.ruleType === 'FIXED_TIME'">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="上班时间" name="checkInTime">
                <a-time-picker
                  v-model:value="formData.checkInTime"
                  format="HH:mm"
                  placeholder="选择上班时间"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="下班时间" name="checkOutTime">
                <a-time-picker
                  v-model:value="formData.checkOutTime"
                  format="HH:mm"
                  placeholder="选择下班时间"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="允许迟到 (分钟)" name="allowLateMinutes">
                <a-input-number v-model:value="formData.allowLateMinutes" :min="0" :max="60" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="允许早退 (分钟)" name="allowEarlyLeaveMinutes">
                <a-input-number v-model:value="formData.allowEarlyLeaveMinutes" :min="0" :max="60" style="width: 100%" />
              </a-form-item>
            </a-col>
          </a-row>
        </template>

        <!-- 弹性工时配置 -->
        <template v-if="formData.ruleType === 'FLEXIBLE'">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="最晚上班时间" name="latestCheckInTime">
                <a-time-picker
                  v-model:value="formData.latestCheckInTime"
                  format="HH:mm"
                  placeholder="选择最晚上班时间"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="要求工作分钟数" name="requiredWorkMinutes">
                <a-input-number v-model:value="formData.requiredWorkMinutes" :min="0" :max="1440" style="width: 100%" />
              </a-form-item>
            </a-col>
          </a-row>
        </template>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="休息开始时间" name="restStartTime">
              <a-time-picker
                v-model:value="formData.restStartTime"
                format="HH:mm"
                placeholder="选择休息开始时间"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="休息结束时间" name="restEndTime">
              <a-time-picker
                v-model:value="formData.restEndTime"
                format="HH:mm"
                placeholder="选择休息结束时间"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="是否跨天" name="crossDayFlag">
              <a-switch v-model:checked="formData.crossDayFlag" :checked-value="1" :unchecked-value="0" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="是否计加班" name="overtimeFlag">
              <a-switch v-model:checked="formData.overtimeFlag" :checked-value="1" :unchecked-value="0" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            :rows="3"
            placeholder="请输入规则描述"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  PlusOutlined,
  CopyOutlined,
  UploadOutlined,
  DownloadOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  EyeOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import { attendanceApi } from '@/api/attendance'

const loading = ref(false)
const ruleList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增规则')
const formRef = ref(null)

// 搜索表单
const searchForm = reactive({
  ruleName: ''
})

const formData = reactive({
  ruleCode: '',
  ruleName: '',
  ruleType: 'FIXED_TIME',
  priority: 0,
  checkInTime: '',
  checkOutTime: '',
  latestCheckInTime: '',
  requiredWorkMinutes: 480,
  allowLateMinutes: 0,
  allowEarlyLeaveMinutes: 0,
  restStartTime: '',
  restEndTime: '',
  crossDayFlag: 0,
  overtimeFlag: 0,
  status: 1,
  description: ''
})

const formRules = {
  ruleCode: [
    { required: true, message: '请输入规则编码', trigger: 'blur' }
  ],
  ruleName: [
    { required: true, message: '请输入规则名称', trigger: 'blur' }
  ],
  ruleType: [
    { required: true, message: '请选择规则类型', trigger: 'change' }
  ]
}

const getRuleTypeName = (type) => {
  const typeMap = {
    'FIXED_TIME': '固定班制',
    'FLEXIBLE': '弹性工时',
    'WORK_HOURS': '按工时制',
    'SHIFT': '排班制'
  }
  return typeMap[type] || type
}

const getRuleTypeColor = (type) => {
  const colorMap = {
    'FIXED_TIME': 'primary',
    'FLEXIBLE': 'success',
    'WORK_HOURS': 'warning',
    'SHIFT': 'info'
  }
  return colorMap[type] || 'default'
}

const columns = [
  {
    title: '规则编码',
    dataIndex: 'ruleCode',
    width: 150
  },
  {
    title: '规则名称',
    dataIndex: 'ruleName',
    width: 200
  },
  {
    title: '规则类型',
    dataIndex: 'ruleType',
    width: 120,
    customRender: ({ record }) => h('a-tag', { type: getRuleTypeColor(record.ruleType) }, getRuleTypeName(record.ruleType))
  },
  {
    title: '上班时间',
    dataIndex: 'checkInTime',
    width: 100
  },
  {
    title: '下班时间',
    dataIndex: 'checkOutTime',
    width: 100
  },
  {
    title: '允许迟到 (分钟)',
    dataIndex: 'allowLateMinutes',
    width: 120
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 80
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
    customRender: ({ record }) => h('a-tag', { type: record.status === 1 ? 'success' : 'danger' }, record.status === 1 ? '启用' : '禁用')
  },
  {
    title: '描述',
    dataIndex: 'description',
    ellipsis: true
  },
  {
    title: '操作',
    width: 200,
    fixed: 'right',
    customRender: ({ record }) => h('div', { class: 'table-actions' }, [
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn edit-btn',
        onClick: () => handleEdit(record)
      }, {
        icon: () => h(EditOutlined),
        default: () => '编辑'
      }),
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn view-btn',
        onClick: () => handleView(record)
      }, {
        icon: () => h(EyeOutlined),
        default: () => '查看'
      }),
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn delete-btn',
        danger: true,
        onClick: () => handleDelete(record)
      }, {
        icon: () => h(DeleteOutlined),
        default: () => '删除'
      })
    ])
  }
]

const loadRules = async () => {
  loading.value = true
  try {
    const res = await attendanceApi.rules.getList()
    if (res.code === 200) {
      ruleList.value = res.data || []
    }
  } catch (error) {
    console.error('加载规则失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增规则'
  Object.assign(formData, {
    ruleCode: '',
    ruleName: '',
    ruleType: 'FIXED_TIME',
    priority: 0,
    checkInTime: '',
    checkOutTime: '',
    latestCheckInTime: '',
    requiredWorkMinutes: 480,
    allowLateMinutes: 0,
    allowEarlyLeaveMinutes: 0,
    restStartTime: '',
    restEndTime: '',
    crossDayFlag: 0,
    overtimeFlag: 0,
    status: 1,
    description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑规则'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleView = (row) => {
  message.info('查看规则详情功能开发中')
}

const handleDelete = (row) => {
  Modal.confirm({
    title: '提示',
    content: `确定要删除规则"${row.ruleName}"吗？`,
    okText: '确定',
    cancelText: '取消',
    icon: h('DeleteOutlined'),
    onOk: async () => {
      try {
        await attendanceApi.rules.delete(row.id)
        message.success('删除成功')
        loadRules()
      } catch (error) {
        console.error('删除失败:', error)
      }
    }
  })
}

const handleRuleTypeChange = () => {
  // 规则类型变化时的处理
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const values = await formRef.value.validateFields()
    if (formData.id) {
      await attendanceApi.rules.update(formData.id, formData)
      message.success('更新成功')
    } else {
      await attendanceApi.rules.create(formData)
      message.success('创建成功')
    }
    dialogVisible.value = false
    loadRules()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 搜索处理
const handleSearch = () => {
  message.info(`搜索规则：${searchForm.ruleName || '全部'}`)
  loadRules()
}

// 重置处理
const handleReset = () => {
  searchForm.ruleName = ''
  loadRules()
}

onMounted(() => {
  loadRules()
})
</script>

<style lang="scss">
/* 考勤规则页面特有样式 */
.rules-container {
  .search-bar {
    margin-bottom: 20px;
  }
}
</style>
