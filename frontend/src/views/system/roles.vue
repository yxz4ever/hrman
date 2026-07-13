<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">角色管理</h1>
      <p class="page-description">管理系统角色和权限，配置访问控制</p>
    </div>

    <div class="page-content">
      <!-- 搜索栏 -->
      <a-form :model="searchForm" layout="inline" class="search-bar">
        <a-form-item label="角色名称">
          <a-input v-model:value="searchForm.roleName" placeholder="请输入角色名称" allow-clear />
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
          新增角色
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

    <!-- 新增/编辑角色对话框 -->
    <a-modal
      v-model:open="roleDialogVisible"
      :title="roleDialogTitle"
      :width="600"
      :confirm-loading="submitLoading"
      @ok="handleSubmit"
      @cancel="handleDialogCancel"
    >
      <a-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleFormRules"
        layout="vertical"
      >
        <a-form-item label="角色名称" name="roleName">
          <a-input v-model:value="roleForm.roleName" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="角色编码" name="roleCode">
          <a-input v-model:value="roleForm.roleCode" placeholder="请输入角色编码（英文）" :disabled="isEdit" />
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="roleForm.description" placeholder="请输入角色描述" :rows="3" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="roleForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 分配菜单对话框 -->
    <a-modal
      v-model:open="menuDialogVisible"
      title="分配菜单权限"
      :width="600"
      @ok="handleSaveMenus"
      @cancel="handleMenuDialogCancel"
    >
      <a-tree
        ref="menuTreeRef"
        :tree-data="menuTreeData"
        :field-names="{ title: 'menuName', children: 'children', key: 'id' }"
        checkable
        default-expand-all
        :check-strictly="false"
        v-model:checked-keys="checkedKeys"
        @check="handleTreeCheck"
      />
    </a-modal>

    <!-- 导入对话框 -->
    <a-modal
      v-model:open="importDialogVisible"
      title="批量导入角色"
      :width="600"
      @ok="handleImportSubmit"
      @cancel="importDialogVisible = false"
    >
      <a-form layout="vertical">
        <a-form-item label="下载模板">
          <a-button type="link" @click="downloadTemplate">
            <template #icon><DownloadOutlined /></template>
            下载 Excel 模板
          </a-button>
          <p class="hint">请按照模板格式填写数据，角色编码不能重复</p>
        </a-form-item>
        <a-form-item label="上传文件">
          <a-upload
            :before-upload="handleBeforeUpload"
            :on-remove="handleRemove"
            :file-list="importFileList"
            accept=".xlsx,.xls,.csv"
          >
            <a-button>
              <template #icon><UploadOutlined /></template>
              选择文件
            </a-button>
          </a-upload>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 导出对话框 -->
    <a-modal
      v-model:open="exportDialogVisible"
      title="数据导出"
      :width="500"
      @ok="handleExportSubmit"
      @cancel="exportDialogVisible = false"
    >
      <a-form layout="vertical">
        <a-form-item label="导出范围">
          <a-radio-group v-model:value="exportScope">
            <a-radio :value="'all'">全部数据</a-radio>
            <a-radio :value="'current'">当前页数据</a-radio>
            <a-radio :value="'selected'">选中数据</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="导出字段">
          <a-checkbox-group v-model:value="exportFields">
            <a-row>
              <a-col :span="12">
                <a-checkbox :value="'roleName'">角色名称</a-checkbox>
              </a-col>
              <a-col :span="12">
                <a-checkbox :value="'roleCode'">角色编码</a-checkbox>
              </a-col>
              <a-col :span="12">
                <a-checkbox :value="'description'">描述</a-checkbox>
              </a-col>
              <a-col :span="12">
                <a-checkbox :value="'status'">状态</a-checkbox>
              </a-col>
              <a-col :span="12">
                <a-checkbox :value="'createTime'">创建时间</a-checkbox>
              </a-col>
            </a-row>
          </a-checkbox-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, h, onMounted, computed, nextTick } from 'vue'
import { message, Modal } from 'ant-design-vue'
import request from '@/utils/request'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  DeleteOutlined,
  UploadOutlined,
  DownloadOutlined,
  QuestionCircleOutlined,
  EditOutlined,
  MenuOutlined
} from '@ant-design/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const roleDialogVisible = ref(false)
const menuDialogVisible = ref(false)
const importDialogVisible = ref(false)
const exportDialogVisible = ref(false)
const roleFormRef = ref(null)
const menuTreeRef = ref(null)
const isEdit = ref(false)
const selectedRows = ref([])
const importFileList = ref([])
const currentRoleId = ref(null)
const checkedKeys = ref([])

const searchForm = reactive({
  roleName: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const roleForm = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const roleFormRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { max: 50, message: '角色名称不能超过 50 个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过 200 个字符', trigger: 'blur' }
  ]
}

const exportScope = ref('all')
const exportFields = ref(['roleName', 'roleCode', 'description', 'status', 'createTime'])

const roleDialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

const menuTreeData = ref([])

const columns = [
  {
    type: 'selection',
    width: 55
  },
  {
    title: '角色名称',
    dataIndex: 'roleName',
    width: 150
  },
  {
    title: '角色编码',
    dataIndex: 'roleCode',
    width: 150
  },
  {
    title: '描述',
    dataIndex: 'description',
    ellipsis: true
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
    customRender: ({ record }) => h('a-tag', { type: record.status === 1 ? 'success' : 'danger' }, record.status === 1 ? '启用' : '禁用')
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180
  },
  {
    title: '操作',
    width: 250,
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
        class: 'action-btn assign-btn',
        onClick: () => handleAssignMenus(record)
      }, {
        icon: () => h(MenuOutlined),
        default: () => '分配菜单'
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

const loadRoles = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.size,
      ...searchForm
    }
    const result = await request.get('/system/role/page', { params })
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

const loadMenuTree = async () => {
  try {
    const result = await request.get('/system/menu/tree')
    console.log('菜单树数据:', result)
    if (result.code === 200) {
      menuTreeData.value = result.data || []
      console.log('菜单树节点:', JSON.stringify(result.data, null, 2))
    }
  } catch (error) {
    console.error('加载菜单树失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadRoles()
}

const handleReset = () => {
  Object.assign(searchForm, { roleName: '' })
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(roleForm, { id: null, roleName: '', roleCode: '', description: '', status: 1 })
  roleDialogVisible.value = true
  setTimeout(() => roleFormRef.value?.clearValidate(), 0)
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(roleForm, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    description: row.description,
    status: row.status
  })
  roleDialogVisible.value = true
  setTimeout(() => roleFormRef.value?.clearValidate(), 0)
}

const handleSubmit = async () => {
  if (!roleFormRef.value) return
  try {
    await roleFormRef.value.validate()
    submitLoading.value = true
    const method = isEdit.value ? 'put' : 'post'
    const result = await request[method]('/system/role', roleForm)
    if (result.code === 200) {
      message.success(isEdit.value ? '修改成功' : '添加成功')
      roleDialogVisible.value = false
      loadRoles()
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
  roleDialogVisible.value = false
  roleFormRef.value?.resetFields()
}

const handleAssignMenus = async (row) => {
  currentRoleId.value = row.id
  checkedKeys.value = [] // 先清空

  // 打开对话框
  menuDialogVisible.value = true

  try {
    const result = await request.get(`/system/role/${row.id}/menus`)
    console.log('获取菜单权限结果:', result)
    if (result.code === 200) {
      // 后端可能返回的是对象数组或 ID 数组，需要处理
      const menuIds = result.data || []
      if (Array.isArray(menuIds)) {
        checkedKeys.value = [...menuIds] // 确保是数组副本
      } else if (menuIds && Array.isArray(menuIds.ids)) {
        checkedKeys.value = [...menuIds.ids]
      } else {
        checkedKeys.value = []
      }
      console.log('设置选中 keys:', checkedKeys.value)
    }
  } catch (error) {
    console.error('加载菜单权限失败:', error)
    checkedKeys.value = []
  }

  // 加载菜单树数据
  await loadMenuTree()

  // 确保树组件更新
  await nextTick()
}

const handleMenuDialogCancel = () => {
  menuDialogVisible.value = false
  checkedKeys.value = []
}

// 处理树节点勾选事件，实现父子联动
const handleTreeCheck = (checkedKeysValue, { checkedNodes }) => {
  console.log('勾选变化:', checkedKeysValue)
  // Ant Design Vue 在 check-strictly=false 时会自动处理父子联动
  // 这里只需要更新 checkedKeys 的值
  checkedKeys.value = Array.isArray(checkedKeysValue)
    ? [...checkedKeysValue]
    : []
}

const handleSaveMenus = async () => {
  // 确保 checkedKeys 是数组
  const checkedKeysValue = Array.isArray(checkedKeys.value)
    ? [...checkedKeys.value]
    : []

  console.log('保存的菜单 ID 列表:', checkedKeysValue)

  try {
    const result = await request.post(`/system/role/${currentRoleId.value}/menus`, checkedKeysValue)
    if (result.code === 200) {
      message.success('分配菜单权限成功')
      menuDialogVisible.value = false
      loadRoles() // 刷新列表
    } else {
      message.error(result.message || '分配失败')
    }
  } catch (error) {
    console.error('分配菜单权限失败:', error)
    message.error('分配失败，请稍后重试')
  }
}

const handleDelete = (row) => {
  Modal.confirm({
    title: '提示',
    content: `确定要删除角色${row.roleName}吗？删除后不可恢复！`,
    okText: '确定',
    cancelText: '取消',
    icon: h(QuestionCircleOutlined),
    okType: 'danger',
    onOk: async () => {
      try {
        const result = await request.delete(`/system/role/${row.id}`)
        if (result.code === 200) {
          message.success('删除成功')
          loadRoles()
        } else {
          message.error(result.message || '删除失败')
        }
      } catch (error) {
        message.error('删除失败，请稍后重试')
      }
    }
  })
}

const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    message.warning('请选择要删除的角色')
    return
  }
  const ids = selectedRows.value.map(row => row.id)
  Modal.confirm({
    title: '提示',
    content: `确定要删除选中的${selectedRows.value.length}个角色吗？`,
    okText: '确定',
    cancelText: '取消',
    icon: h(QuestionCircleOutlined),
    okType: 'danger',
    onOk: async () => {
      try {
        const result = await request.delete('/system/role/batch', { data: ids })
        if (result.code === 200) {
          message.success('批量删除成功')
          selectedRows.value = []
          loadRoles()
        } else {
          message.error(result.message || '删除失败')
        }
      } catch (error) {
        message.error('删除失败，请稍后重试')
      }
    }
  })
}

const handleImport = () => {
  importDialogVisible.value = true
  importFileList.value = []
}

const handleBeforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.ms-excel' || 
                  file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                  file.type === 'text/csv'
  if (!isExcel) {
    message.error('只能上传 Excel 或 CSV 文件！')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('文件大小不能超过 2MB！')
    return false
  }
  importFileList.value = [file]
  return false
}

const handleRemove = () => {
  importFileList.value = []
}

const handleImportSubmit = async () => {
  if (importFileList.value.length === 0) {
    message.warning('请选择要上传的文件')
    return
  }
  const file = importFileList.value[0]
  const formData = new FormData()
  formData.append('file', file)
  try {
    const result = await request.post('/system/role/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (result.code === 200) {
      message.success(`导入成功！成功${result.data.successCount || 0}条，失败${result.data.failCount || 0}条`)
      importDialogVisible.value = false
      loadRoles()
    } else {
      message.error(result.message || '导入失败')
    }
  } catch (error) {
    message.error('导入失败，请稍后重试')
  }
}

const downloadTemplate = () => {
  const templateData = '角色名称，角色编码，描述，状态\n超级管理员，super_admin，拥有所有权限，1\n人事管理员，hr_admin，负责人事管理，1'
  const blob = new Blob([templateData], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = '角色导入模板.csv'
  link.click()
  URL.revokeObjectURL(link.href)
}

const handleExport = () => {
  exportDialogVisible.value = true
}

const handleExportSubmit = async () => {
  try {
    let url = '/system/role/export?'
    if (exportScope.value === 'selected' && selectedRows.value.length > 0) {
      const ids = selectedRows.value.map(row => row.id).join(',')
      url += `ids=${ids}&scope=selected`
    } else if (exportScope.value === 'current') {
      url += `scope=current`
    }
    // fields 参数后端可能不需要，先移除
    // url += `&fields=${exportFields.value.join(',')}`

    console.log('导出 URL:', url)

    const response = await request.get(url, { responseType: 'blob' })

    // 检查返回的是否是有效的 Excel 文件
    const contentType = response.headers['content-type']
    console.log('Content-Type:', contentType)

    // 如果是 JSON 错误响应，尝试解析
    if (contentType && contentType.includes('application/json')) {
      const text = await response.data.text()
      const errorData = JSON.parse(text)
      throw new Error(errorData.message || '导出失败')
    }

    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `角色数据_${new Date().getTime()}.xlsx`
    link.click()
    URL.revokeObjectURL(link.href)
    message.success('导出成功')
    exportDialogVisible.value = false
  } catch (error) {
    console.error('导出失败:', error)
    message.error(error.message || '导出失败，请稍后重试')
  }
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

onMounted(() => {
  loadRoles()
  loadMenuTree()
})
</script>

<style scoped lang="scss">
.roles-container {
  .search-bar { margin-bottom: 20px; }
  .hint { font-size: 12px; color: #999; margin-top: 8px; }
}
</style>
