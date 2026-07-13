<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">员工管理</h1>
      <p class="page-description">管理企业员工档案信息，维护员工基础数据</p>
    </div>

    <div class="page-content">
      <!-- 搜索栏 -->
      <a-form :model="searchForm" layout="inline" class="search-bar">
        <a-form-item label="姓名">
          <a-input v-model:value="searchForm.name" placeholder="请输入姓名" allow-clear />
        </a-form-item>
        <a-form-item label="工号">
          <a-input v-model:value="searchForm.empNo" placeholder="请输入工号" allow-clear />
        </a-form-item>
        <a-form-item label="部门">
          <a-select v-model:value="searchForm.deptId" placeholder="请选择部门" allow-clear style="width: 180px;">
            <a-select-option label="技术部" :value="1">技术部</a-select-option>
            <a-select-option label="销售部" :value="2">销售部</a-select-option>
            <a-select-option label="市场部" :value="3">市场部</a-select-option>
            <a-select-option label="人事部" :value="4">人事部</a-select-option>
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
        <a-button type="primary" @click="handleAdd" class="action-btn primary">
          <template #icon><PlusOutlined /></template>
          新增员工
        </a-button>
        <a-button type="success" @click="handleImport" class="action-btn success">
          <template #icon><UploadOutlined /></template>
          批量导入
        </a-button>
        <a-button type="warning" @click="handleExport" class="action-btn warning">
          <template #icon><DownloadOutlined /></template>
          数据导出
        </a-button>
        <a-button type="danger" @click="handleBatchDelete" class="action-btn danger">
          <template #icon><DeleteOutlined /></template>
          批量删除
        </a-button>
      </div>

      <!-- 表格 -->
      <div class="table-container">
        <a-spin :spinning="loading">
          <a-table
            :columns="columns"
            :data-source="tableData"
            :row-key="record => record.id"
            bordered
            :pagination="false"
          >
          </a-table>
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

    <!-- 员工对话框 -->
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
        label-col :label-col="{ span: 6 }"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="工号" name="empNo">
              <a-input v-model:value="formData.empNo" placeholder="请输入工号" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formData.name" placeholder="请输入姓名" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="性别" name="gender">
              <a-select v-model:value="formData.gender" placeholder="请选择性别">
                <a-select-option value="男">男</a-select-option>
                <a-select-option value="女">女</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="手机号" name="mobile">
              <a-input v-model:value="formData.mobile" placeholder="请输入手机号" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="部门" name="deptId">
              <a-select v-model:value="formData.deptId" placeholder="请选择部门">
                <a-select-option label="技术部" :value="1">技术部</a-select-option>
                <a-select-option label="销售部" :value="2">销售部</a-select-option>
                <a-select-option label="市场部" :value="3">市场部</a-select-option>
                <a-select-option label="人事部" :value="4">人事部</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="岗位" name="postId">
              <a-input v-model:value="formData.postId" placeholder="请输入岗位" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="入职日期" name="hireDate">
              <a-date-picker
                v-model:value="formData.hireDate"
                placeholder="选择入职日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="员工状态" name="employeeStatus">
              <a-select v-model:value="formData.employeeStatus" placeholder="请选择状态">
                <a-select-option value="在职">在职</a-select-option>
                <a-select-option value="试用">试用</a-select-option>
                <a-select-option value="离职">离职</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="备注" name="remark">
          <a-textarea
            v-model:value="formData.remark"
            :rows="3"
            placeholder="请输入备注"
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
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  UploadOutlined,
  DownloadOutlined,
  DeleteOutlined,
  EditOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'
import { employeeApi } from '@/api/employee'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增员工')
const formRef = ref(null)

const searchForm = reactive({
  name: '',
  empNo: '',
  deptId: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  empNo: '',
  name: '',
  gender: '',
  mobile: '',
  deptId: null,
  postId: '',
  hireDate: '',
  employeeStatus: '在职',
  remark: ''
})

const formRules = {
  empNo: [
    { required: true, message: '请输入工号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  deptId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  hireDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ]
}

const columns = [
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
    title: '性别',
    dataIndex: 'gender',
    width: 80
  },
  {
    title: '手机号',
    dataIndex: 'mobile',
    width: 120
  },
  {
    title: '部门',
    dataIndex: 'deptId',
    width: 120,
    customRender: ({ record }) => getDeptName(record.deptId)
  },
  {
    title: '岗位',
    dataIndex: 'postName',
    width: 120
  },
  {
    title: '状态',
    dataIndex: 'employeeStatus',
    width: 100,
    customRender: ({ record }) => h('a-tag', { type: getStatusType(record.employeeStatus) }, getDeptName(record.employeeStatus))
  },
  {
    title: '入职日期',
    dataIndex: 'hireDate',
    width: 120
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
        onClick: () => handleView(record)
      }, {
        icon: () => h(EyeOutlined),
        default: () => '查看'
      }),
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
        danger: true,
        class: 'action-btn delete-btn',
        onClick: () => handleDelete(record)
      }, {
        icon: () => h(DeleteOutlined),
        default: () => '删除'
      })
    ])
  }
]

const getDeptName = (deptId) => {
  const deptMap = {
    1: '技术部',
    2: '销售部',
    3: '市场部',
    4: '人事部'
  }
  return deptMap[deptId] || '-'
}

const getStatusType = (status) => {
  const typeMap = {
    '在职': 'success',
    '试用': 'warning',
    '离职': 'danger'
  }
  return typeMap[status] || 'default'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await employeeApi.getList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })

    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    empNo: '',
    deptId: null
  })
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增员工'
  Object.assign(formData, {
    empNo: '',
    name: '',
    gender: '',
    mobile: '',
    deptId: null,
    postId: '',
    hireDate: '',
    employeeStatus: '在职',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑员工'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleView = (row) => {
  message.info('查看详情功能开发中')
}

const handleDelete = (row) => {
  Modal.confirm({
    title: '提示',
    content: `确定要删除员工"${row.name}"吗？`,
    okText: '确定',
    cancelText: '取消',
    icon: h('DeleteOutlined'),
    onOk: async () => {
      try {
        await employeeApi.delete(row.id)
        message.success('删除成功')
        loadData()
      } catch (error) {
        console.error('删除失败:', error)
      }
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const values = await formRef.value.validateFields()
    if (formData.id) {
      await employeeApi.update(formData.id, formData)
      message.success('更新成功')
    } else {
      await employeeApi.create(formData)
      message.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.employee-container {
  .search-bar {
    margin-bottom: 20px;
  }

  .table-container {
    background: #fff;
    border-radius: 8px;
  }
}
</style>
