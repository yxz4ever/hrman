<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <p class="page-description">管理系统用户账号，维护用户权限和安全</p>
    </div>

    <div class="page-content">
      <!-- 搜索栏 -->
      <a-form :model="searchForm" layout="inline" class="search-bar">
        <a-form-item label="用户名">
          <a-input v-model:value="searchForm.username" placeholder="请输入用户名" allow-clear />
        </a-form-item>
        <a-form-item label="姓名">
          <a-input v-model:value="searchForm.realName" placeholder="请输入姓名" allow-clear />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="searchForm.status" placeholder="请选择状态" allow-clear>
            <a-select-option label="启用" :value="1">启用</a-select-option>
            <a-select-option label="禁用" :value="0">禁用</a-select-option>
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
          新增用户
        </a-button>
        <a-button type="success" class="action-btn success">
          <template #icon><UploadOutlined /></template>
          批量导入
        </a-button>
        <a-button type="warning" class="action-btn warning">
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
            @selection-change="handleSelectionChange"
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

    <!-- 新增/编辑用户对话框 -->
    <a-modal
      v-model:open="userDialogVisible"
      :title="userDialogTitle"
      :width="600"
      :confirm-loading="submitLoading"
      @ok="handleSubmit"
      @cancel="handleDialogCancel"
    >
      <a-form
        ref="userFormRef"
        :model="userForm"
        :rules="userFormRules"
        layout="vertical"
      >
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="userForm.username" placeholder="请输入用户名" :disabled="isEdit" />
        </a-form-item>
        <a-form-item label="姓名" name="realName">
          <a-input v-model:value="userForm.realName" placeholder="请输入姓名" />
        </a-form-item>
        <a-form-item label="邮箱" name="email">
          <a-input v-model:value="userForm.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="userForm.phone" placeholder="请输入手机号" />
        </a-form-item>
        <a-form-item label="角色" name="roleIds">
          <a-select
            v-model:value="userForm.roleIds"
            mode="multiple"
            placeholder="请选择角色"
            :options="roleOptions"
          />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="userForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item v-if="!isEdit" label="初始密码" name="password">
          <a-input-password v-model:value="userForm.password" placeholder="请输入初始密码" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 重置密码对话框 -->
    <a-modal
      v-model:open="resetDialogVisible"
      title="重置密码"
      :width="500"
      @ok="handleResetPasswordSubmit"
      @cancel="resetDialogVisible = false"
    >
      <a-form ref="resetFormRef" :model="resetForm" :rules="resetFormRules" layout="vertical">
        <a-form-item label="用户名">
          <a-input v-model:value="resetForm.username" disabled />
        </a-form-item>
        <a-form-item label="新密码" name="newPassword">
          <a-input-password v-model:value="resetForm.newPassword" placeholder="请输入新密码" />
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword">
          <a-input-password v-model:value="resetForm.confirmPassword" placeholder="请再次输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, h, onMounted, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'
import request from '@/utils/request'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  DeleteOutlined,
  UploadOutlined,
  DownloadOutlined,
  EditOutlined,
  LockOutlined,
  QuestionCircleOutlined
} from '@ant-design/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const userDialogVisible = ref(false)
const resetDialogVisible = ref(false)
const userFormRef = ref(null)
const resetFormRef = ref(null)
const isEdit = ref(false)
const selectedUser = ref(null)

const searchForm = reactive({
  username: '',
  realName: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const selectedRows = ref([])

// 用户表单
const userForm = reactive({
  id: null,
  username: '',
  realName: '',
  email: '',
  phone: '',
  roleIds: [],
  status: 1,
  password: ''
})

const userFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度 3-20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  roleIds: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入初始密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ]
}

// 重置密码表单
const resetForm = reactive({
  userId: null,
  username: '',
  newPassword: '',
  confirmPassword: ''
})

const resetFormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: (_, value) => {
        if (!value || value === resetForm.newPassword) {
          return Promise.resolve()
        }
        return Promise.reject(new Error('两次输入的密码不一致'))
      }, trigger: 'blur' }
  ]
}

// 角色选项（从后端获取）
const roleOptions = ref([
  { label: '超级管理员', value: 1 },
  { label: '管理员', value: 2 },
  { label: '普通用户', value: 3 }
])

const userDialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

const columns = [
  {
    type: 'selection',
    width: 55
  },
  {
    title: '用户名',
    dataIndex: 'username',
    width: 120
  },
  {
    title: '姓名',
    dataIndex: 'realName',
    width: 100
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    width: 180
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    width: 120
  },
  {
    title: '角色',
    dataIndex: 'roleName',
    width: 120
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
    customRender: ({ record }) => h('a-tag', { type: record.status === 1 ? 'success' : 'danger' }, record.status === 1 ? '启用' : '禁用')
  },
  {
    title: '最后登录时间',
    dataIndex: 'lastLoginTime',
    width: 180
  },
  {
    title: '操作',
    width: 220,
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
        class: 'action-btn reset-btn',
        onClick: () => handleResetPassword(record)
      }, {
        icon: () => h(LockOutlined),
        default: () => '重置密码'
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

// 获取角色列表
const loadRoles = async () => {
  try {
    const result = await request.get('/system/role/list')
    if (result.code === 200) {
      roleOptions.value = result.data.map(role => ({
        label: role.roleName,
        value: role.id
      }))
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

// 分页查询用户
const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      ...searchForm
    }

    const result = await request.post('/system/user/page', params)
    if (result.code === 200) {
      tableData.value = result.data.records || []
      pagination.total = result.data.total || 0
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

const handleSearch = () => {
  pagination.current = 1
  loadUsers()
}

const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    realName: '',
    status: null
  })
  handleSearch()
}

// 新增用户
const handleAdd = () => {
  isEdit.value = false
  Object.assign(userForm, {
    id: null,
    username: '',
    realName: '',
    email: '',
    phone: '',
    roleIds: [],
    status: 1,
    password: ''
  })
  userDialogVisible.value = true
  setTimeout(() => {
    userFormRef.value?.clearValidate()
  }, 0)
}

// 编辑用户
const handleEdit = (row) => {
  isEdit.value = true
  selectedUser.value = row
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    email: row.email,
    phone: row.phone,
    roleIds: row.roleIds || [],
    status: row.status
  })
  userDialogVisible.value = true
  setTimeout(() => {
    userFormRef.value?.clearValidate()
  }, 0)
}

// 提交用户表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    submitLoading.value = true
    
    const url = isEdit.value ? '/system/user' : '/system/user'
    const method = isEdit.value ? 'put' : 'post'
    
    const result = await request[method](url, userForm)
    
    if (result.code === 200) {
      message.success(isEdit.value ? '修改成功' : '添加成功')
      userDialogVisible.value = false
      loadUsers()
    } else {
      message.error(result.message || '操作失败')
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const handleDialogCancel = () => {
  userDialogVisible.value = false
  userFormRef.value?.resetFields()
}

// 重置密码
const handleResetPassword = (row) => {
  selectedUser.value = row
  Object.assign(resetForm, {
    userId: row.id,
    username: row.username,
    newPassword: '',
    confirmPassword: ''
  })
  resetDialogVisible.value = true
  setTimeout(() => {
    resetFormRef.value?.clearValidate()
  }, 0)
}

const handleResetPasswordSubmit = async () => {
  if (!resetFormRef.value) return
  
  try {
    await resetFormRef.value.validate()
    
    const result = await request.put('/system/user/reset-password', {
      userId: resetForm.userId,
      newPassword: resetForm.newPassword
    })
    
    if (result.code === 200) {
      message.success('密码重置成功')
      resetDialogVisible.value = false
    } else {
      message.error(result.message || '重置失败')
    }
  } catch (error) {
    console.error('密码重置失败:', error)
  }
}

// 删除用户
const handleDelete = (row) => {
  Modal.confirm({
    title: '提示',
    content: `确定要删除用户${row.realName}吗？删除后不可恢复！`,
    okText: '确定',
    cancelText: '取消',
    icon: h(QuestionCircleOutlined),
    okType: 'danger',
    onOk: async () => {
      try {
        const result = await request.delete(`/system/user/${row.id}`)
        if (result.code === 200) {
          message.success('删除成功')
          loadUsers()
        } else {
          message.error(result.message || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        message.error('删除失败，请稍后重试')
      }
    }
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    message.warning('请选择要删除的用户')
    return
  }

  const ids = selectedRows.value.map(row => row.id)
  
  Modal.confirm({
    title: '提示',
    content: `确定要删除选中的${selectedRows.value.length}个用户吗？删除后不可恢复！`,
    okText: '确定',
    cancelText: '取消',
    icon: h(QuestionCircleOutlined),
    okType: 'danger',
    onOk: async () => {
      try {
        const result = await request.delete('/system/user/batch', {
          data: ids
        })
        if (result.code === 200) {
          message.success('批量删除成功')
          selectedRows.value = []
          loadUsers()
        } else {
          message.error(result.message || '删除失败')
        }
      } catch (error) {
        console.error('批量删除失败:', error)
        message.error('删除失败，请稍后重试')
      }
    }
  })
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

onMounted(() => {
  loadRoles()
  loadUsers()
})
</script>

<style scoped lang="scss">
.users-container {
  .search-bar {
    margin-bottom: 20px;
  }
}
</style>
