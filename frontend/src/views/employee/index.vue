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
              <a-radio-group v-model:value="formData.gender">
                <a-radio :value="1">男</a-radio>
                <a-radio :value="0">女</a-radio>
              </a-radio-group>
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
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="身份证号" name="idCard">
              <a-input v-model:value="formData.idCard" placeholder="请输入身份证号" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="出生日期" name="birthDate">
              <a-date-picker
                v-model:value="formData.birthDate"
                placeholder="选择出生日期"
                style="width: 100%"
                format="YYYY-MM-DD"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="入职时间" name="hireDate">
              <a-date-picker
                v-model:value="formData.hireDate"
                placeholder="选择入职时间"
                style="width: 100%"
                format="YYYY-MM-DD"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="所属部门" name="deptId">
              <a-select v-model:value="formData.deptId" placeholder="请选择部门" style="width: 100%">
                <a-select-option
                  v-for="dept in departmentList"
                  :key="dept.id"
                  :value="dept.id"
                >
                  {{ dept.deptName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="所属岗位" name="postId">
              <a-select v-model:value="formData.postId" placeholder="请选择岗位" style="width: 100%">
                <a-select-option
                  v-for="pos in positionList"
                  :key="pos.id"
                  :value="pos.id"
                >
                  {{ pos.positionName || pos.postName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="员工类型" name="employeeType">
              <a-select v-model:value="formData.employeeType" style="width: 100%">
                <a-select-option :value="1">正式</a-select-option>
                <a-select-option :value="2">试用</a-select-option>
                <a-select-option :value="3">实习</a-select-option>
                <a-select-option :value="4">外包</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="在职状态" name="employeeStatus">
              <a-select v-model:value="formData.employeeStatus" style="width: 100%">
                <a-select-option :value="1">在职</a-select-option>
                <a-select-option :value="0">离职</a-select-option>
                <a-select-option :value="2">停薪留职</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="关联用户" name="userId">
              <a-select
                v-model:value="formData.userId"
                placeholder="请选择关联系统用户（可选）"
                allow-clear
                style="width: 100%"
              >
                <a-select-option
                  v-for="user in userList"
                  :key="user.id"
                  :value="user.id"
                >
                  {{ user.username }} - {{ user.realName || user.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="学历" name="education">
              <a-input v-model:value="formData.education" placeholder="请输入学历" />
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
import dayjs from 'dayjs'
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
import {
  getEmployeePage,
  addEmployee,
  updateEmployee,
  deleteEmployee
} from '@/api/employee'
import { getDepartmentList } from '@/api/department'
import { getPositionList } from '@/api/position'
import { getUserList } from '@/api/auth'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增员工')
const formRef = ref(null)

// 下拉数据
const departmentList = ref([])
const positionList = ref([])
const userList = ref([])

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
  id: null,
  empNo: '',
  name: '',
  gender: 1,
  mobile: '',
  email: '',
  idCard: '',
  birthDate: null,
  hireDate: null,
  deptId: null,
  postId: null,
  employeeType: 1,
  employeeStatus: 1,
  education: '',
  userId: null,
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
    { required: true, message: '请选择入职时间', trigger: 'change' }
  ]
}

// 获取员工类型文本
const getEmployeeTypeText = (type) => {
  const map = { 1: '正式', 2: '试用', 3: '实习', 4: '外包' }
  return map[type] || '-'
}

// 获取员工状态文本
const getEmployeeStatusText = (status) => {
  const map = { 0: '离职', 1: '在职', 2: '停薪留职' }
  return map[status] || '-'
}

const columns = [
  {
    title: '工号',
    dataIndex: 'empNo',
    width: 100
  },
  {
    title: '姓名',
    dataIndex: 'name',
    width: 100
  },
  {
    title: '性别',
    dataIndex: 'gender',
    width: 60,
    customRender: ({ record }) => h('span', { style: { color: record.gender === 1 ? '#1890ff' : '#eb2f96' } }, record.gender === 1 ? '男' : '女')
  },
  {
    title: '手机号',
    dataIndex: 'mobile',
    width: 120
  },
  {
    title: '部门',
    dataIndex: 'deptName',
    width: 120
  },
  {
    title: '入职时间',
    dataIndex: 'hireDate',
    width: 110
  },
  {
    title: '生日',
    dataIndex: 'birthDate',
    width: 110
  },
  {
    title: '员工类型',
    dataIndex: 'employeeType',
    width: 80,
    customRender: ({ record }) => h('a-tag', { color: getEmployeeTypeColor(record.employeeType) }, getEmployeeTypeText(record.employeeType))
  },
  {
    title: '在职状态',
    dataIndex: 'employeeStatus',
    width: 80,
    customRender: ({ record }) => h('a-tag', { color: record.employeeStatus === 1 ? 'green' : 'default' }, getEmployeeStatusText(record.employeeStatus))
  },
  {
    title: '操作',
    width: 150,
    fixed: 'right',
    customRender: ({ record }) => h('div', { class: 'table-actions' }, [
      h('a-button', {
        type: 'link',
        size: 'small',
        onClick: () => handleEdit(record)
      }, '编辑'),
      h('a-button', {
        type: 'link',
        size: 'small',
        danger: true,
        onClick: () => handleDelete(record)
      }, '删除')
    ])
  }
]

const getEmployeeTypeColor = (type) => {
  const map = { 1: 'blue', 2: 'orange', 3: 'green', 4: 'purple' }
  return map[type] || 'default'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getEmployeePage({
      pageNum: pagination.current,
      pageSize: pagination.size,
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

// 加载部门列表
const loadDepartmentList = async () => {
  try {
    const res = await getDepartmentList()
    if (res.code === 200) {
      departmentList.value = res.data
    }
  } catch (error) {
    console.error('加载部门列表失败:', error)
  }
}

// 加载岗位列表
const loadPositionList = async () => {
  try {
    const res = await getPositionList()
    if (res.code === 200) {
      positionList.value = res.data
    }
  } catch (error) {
    console.error('加载岗位列表失败:', error)
  }
}

// 加载用户列表
const loadUserList = async () => {
  try {
    const res = await getUserList()
    if (res.code === 200) {
      userList.value = res.data
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
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
    id: null,
    empNo: '',
    name: '',
    gender: 1,
    mobile: '',
    email: '',
    idCard: '',
    birthDate: null,
    hireDate: null,
    deptId: null,
    postId: null,
    employeeType: 1,
    employeeStatus: 1,
    education: '',
    userId: null,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑员工'
  Object.assign(formData, {
    id: row.id,
    empNo: row.empNo,
    name: row.name,
    gender: row.gender,
    mobile: row.mobile,
    email: row.email,
    idCard: row.idCard,
    birthDate: row.birthDate ? dayjs(row.birthDate) : null,
    hireDate: row.hireDate ? dayjs(row.hireDate) : null,
    deptId: row.deptId,
    postId: row.postId,
    employeeType: row.employeeType,
    employeeStatus: row.employeeStatus,
    education: row.education,
    userId: row.userId,
    remark: row.remark
  })
  dialogVisible.value = true
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
    await formRef.value.validate()
    // 转换日期格式
    const data = {
      ...formData,
      birthDate: formData.birthDate ? formData.birthDate.format('YYYY-MM-DD') : null,
      hireDate: formData.hireDate ? formData.hireDate.format('YYYY-MM-DD') : null
    }
    if (formData.id) {
      await updateEmployee(data)
      message.success('更新成功')
    } else {
      await addEmployee(data)
      message.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

onMounted(() => {
  loadData()
  loadDepartmentList()
  loadPositionList()
  loadUserList()
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
