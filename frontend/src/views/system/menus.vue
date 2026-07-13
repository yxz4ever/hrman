<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">菜单管理</h1>
      <p class="page-description">管理系统菜单结构和权限配置</p>
    </div>

    <div class="page-content">
      <!-- 操作栏 -->
      <div class="action-bar">
        <a-button type="primary" @click="handleAdd" class="action-btn primary">
          <template #icon><PlusOutlined /></template>
          新增根菜单
        </a-button>
        <a-button type="success" @click="handleImport" class="action-btn success">
          <template #icon><UploadOutlined /></template>
          导入菜单
        </a-button>
        <a-button type="warning" @click="handleExport" class="action-btn warning">
          <template #icon><DownloadOutlined /></template>
          导出菜单
        </a-button>
        <a-button type="primary" @click="handleRefresh" class="action-btn info">
          <template #icon><ReloadOutlined /></template>
          刷新
        </a-button>
      </div>

      <!-- 菜单树表格 -->
      <div class="table-container">
        <a-spin :spinning="loading">
          <a-table
            :columns="columns"
            :data-source="tableData"
            :row-key="record => record.id"
            :scroll="{ x: 1200 }"
            bordered
            :pagination="false"
            :tree-data="tableData"
          />
        </a-spin>
      </div>
    </div>

    <!-- 新增/编辑菜单对话框 -->
    <a-modal
      v-model:open="menuDialogVisible"
      :title="menuDialogTitle"
      :width="700"
      :confirm-loading="submitLoading"
      @ok="handleSubmit"
      @cancel="handleDialogCancel"
    >
      <a-form
        ref="menuFormRef"
        :model="menuForm"
        :rules="menuFormRules"
        layout="vertical"
      >
        <a-form-item label="菜单类型" name="menuType">
          <a-radio-group v-model:value="menuForm.menuType" @change="handleMenuTypeChange">
            <a-radio :value="1">目录</a-radio>
            <a-radio :value="2">菜单</a-radio>
            <a-radio :value="3">按钮</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="父级菜单" name="parentId">
          <a-tree-select
            v-model:value="menuForm.parentId"
            :tree-data="menuTreeSelectData"
            placeholder="选择父级菜单"
            allow-clear
            :field-names="{ label: 'menuName', value: 'id', children: 'children' }"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="菜单名称" name="menuName">
          <a-input v-model:value="menuForm.menuName" placeholder="请输入菜单名称" />
        </a-form-item>
        <a-form-item label="路由路径" name="path" v-if="menuForm.menuType !== 3">
          <a-input v-model:value="menuForm.path" placeholder="请输入路由路径" />
        </a-form-item>
        <a-form-item label="组件路径" name="component" v-if="menuForm.menuType === 2">
          <a-input v-model:value="menuForm.component" placeholder="请输入组件路径，如：system/users" />
        </a-form-item>
        <a-form-item label="权限标识" name="permission" v-if="menuForm.menuType === 3">
          <a-input v-model:value="menuForm.permission" placeholder="如：system:user:add" />
        </a-form-item>
        <a-form-item label="图标" name="icon" v-if="menuForm.menuType !== 3">
          <a-input v-model:value="menuForm.icon" placeholder="如：SettingOutlined" allow-clear>
            <template #addonAfter>
              <a-popover title="常用图标">
                <template #content>
                  <div class="icon-picker">
                    <span @click="selectIcon('SettingOutlined')">SettingOutlined</span>
                    <span @click="selectIcon('UserOutlined')">UserOutlined</span>
                    <span @click="selectIcon('TeamOutlined')">TeamOutlined</span>
                    <span @click="selectIcon('MenuOutlined')">MenuOutlined</span>
                    <span @click="selectIcon('HomeOutlined')">HomeOutlined</span>
                  </div>
                </template>
                <a-icon type="question-circle" />
              </a-popover>
            </template>
          </a-input>
        </a-form-item>
        <a-form-item label="排序" name="sortOrder">
          <a-input-number v-model:value="menuForm.sortOrder" :min="0" :step="1" style="width: 100%" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="menuForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 导入对话框 -->
    <a-modal
      v-model:open="importDialogVisible"
      title="导入菜单"
      :width="600"
      @ok="handleImportSubmit"
      @cancel="importDialogVisible = false"
    >
      <a-form layout="vertical">
        <a-form-item label="上传 JSON 文件">
          <a-upload
            :before-upload="handleImportBeforeUpload"
            :on-remove="handleImportRemove"
            :file-list="importFileList"
            accept=".json"
          >
            <a-button>
              <template #icon><UploadOutlined /></template>
              选择 JSON 文件
            </a-button>
          </a-upload>
          <p class="hint">请按照 JSON 格式上传菜单数据</p>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 导出对话框 -->
    <a-modal
      v-model:open="exportDialogVisible"
      title="导出菜单"
      :width="500"
      @ok="handleExportSubmit"
      @cancel="exportDialogVisible = false"
    >
      <a-form layout="vertical">
        <a-form-item label="导出范围">
          <a-radio-group v-model:value="exportScope">
            <a-radio :value="'all'">全部菜单</a-radio>
            <a-radio :value="'selected'">选中菜单</a-radio>
          </a-radio-group>
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
  PlusOutlined,
  ReloadOutlined,
  UploadOutlined,
  DownloadOutlined,
  QuestionCircleOutlined,
  EditOutlined,
  DeleteOutlined,
  PlusCircleOutlined
} from '@ant-design/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const menuDialogVisible = ref(false)
const importDialogVisible = ref(false)
const exportDialogVisible = ref(false)
const menuFormRef = ref(null)
const isEdit = ref(false)
const importFileList = ref([])
const selectedMenu = ref(null)

const menuForm = reactive({
  id: null,
  parentId: null,
  menuName: '',
  menuType: 1,  // 1-目录，2-菜单，3-按钮
  icon: '',
  path: '',
  component: '',
  permission: '',
  sortOrder: 1,
  status: 1
})

const menuFormRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  menuType: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ],
  path: [
    { required: true, message: '请输入路由路径', trigger: 'blur' }
  ],
  component: [
    { required: true, message: '请输入组件路径', trigger: 'blur' }
  ],
  permission: [
    { required: true, message: '请输入权限标识', trigger: 'blur' }
  ]
}

const exportScope = ref('all')

const menuDialogTitle = computed(() => isEdit.value ? '编辑菜单' : '新增菜单')

const menuTreeSelectData = ref([])

const columns = [
  {
    title: '菜单名称',
    dataIndex: 'menuName',
    width: 200
  },
  {
    title: '类型',
    dataIndex: 'menuType',
    width: 100,
    customRender: ({ record }) => {
      const typeMap = {
        1: { text: '目录', type: 'primary' },
        2: { text: '菜单', type: 'success' },
        3: { text: '按钮', type: 'warning' }
      }
      const type = typeMap[record.menuType] || { text: record.menuType, type: 'default' }
      return h('a-tag', { type: type.type }, type.text)
    }
  },
  {
    title: '图标',
    dataIndex: 'icon',
    width: 120,
    customRender: ({ record }) => record.icon || '-'
  },
  {
    title: '路由路径',
    dataIndex: 'path',
    width: 180,
    ellipsis: true
  },
  {
    title: '组件路径',
    dataIndex: 'component',
    width: 180,
    ellipsis: true
  },
  {
    title: '权限标识',
    dataIndex: 'permission',
    width: 150,
    ellipsis: true
  },
  {
    title: '排序',
    dataIndex: 'sortOrder',
    width: 70
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
    customRender: ({ record }) => h('a-tag', { type: record.status === 1 ? 'success' : 'danger' }, record.status === 1 ? '启用' : '禁用')
  },
  {
    title: '操作',
    width: 220,
    fixed: 'right',
    customRender: ({ record }) => h('div', { class: 'table-actions' }, [
      h('a-button', {
        type: 'primary',
        size: 'small',
        class: 'action-btn add-btn',
        onClick: () => handleAddChild(record)
      }, {
        icon: () => h(PlusCircleOutlined),
        default: () => '新增子菜单'
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

const loadMenuTree = async () => {
  loading.value = true
  try {
    const result = await request.get('/system/menu/tree')
    if (result.code === 200) {
      tableData.value = result.data || []
      menuTreeSelectData.value = buildTreeSelectData(result.data || [])
    } else {
      message.error(result.message || '加载失败')
    }
  } catch (error) {
    console.error('加载失败:', error)
    message.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const buildTreeSelectData = (data) => {
  if (!data || data.length === 0) return []
  return data.map(item => ({
    id: item.id,
    menuName: item.menuName,
    children: item.children ? buildTreeSelectData(item.children) : []
  }))
}

const handleRefresh = () => {
  loadMenuTree()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(menuForm, {
    id: null,
    parentId: null,
    menuName: '',
    menuType: 'directory',
    icon: '',
    path: '',
    component: '',
    permission: '',
    sortOrder: 1,
    status: 1
  })
  menuDialogVisible.value = true
  setTimeout(() => menuFormRef.value?.clearValidate(), 0)
}

const handleAddChild = (row) => {
  isEdit.value = false
  Object.assign(menuForm, {
    id: null,
    parentId: row.id,
    menuName: '',
    menuType: row.menuType === 'button' ? 'button' : 'menu',
    icon: '',
    path: '',
    component: '',
    permission: '',
    sortOrder: 1,
    status: 1
  })
  menuDialogVisible.value = true
  setTimeout(() => menuFormRef.value?.clearValidate(), 0)
}

const handleEdit = (row) => {
  isEdit.value = true
  selectedMenu.value = row
  Object.assign(menuForm, {
    id: row.id,
    parentId: row.parentId || null,
    menuName: row.menuName,
    menuType: row.menuType,
    icon: row.icon || '',
    path: row.path || '',
    component: row.component || '',
    permission: row.permission || '',
    sortOrder: row.sortOrder || 1,
    status: row.status
  })
  menuDialogVisible.value = true
  setTimeout(() => menuFormRef.value?.clearValidate(), 0)
}

const handleMenuTypeChange = () => {
  if (menuForm.menuType === 1) {  // 目录
    menuForm.path = ''
    menuForm.component = ''
    menuForm.permission = ''
  } else if (menuForm.menuType === 2) {  // 菜单
    menuForm.permission = ''
  } else if (menuForm.menuType === 3) {  // 按钮
    menuForm.path = ''
    menuForm.component = ''
    menuForm.icon = ''
  }
}

const selectIcon = (icon) => {
  menuForm.icon = icon
}

const handleSubmit = async () => {
  if (!menuFormRef.value) return
  try {
    await menuFormRef.value.validate()
    submitLoading.value = true
    const method = isEdit.value ? 'put' : 'post'
    const result = await request[method]('/system/menu', menuForm)
    if (result.code === 200) {
      message.success(isEdit.value ? '修改成功' : '添加成功')
      menuDialogVisible.value = false
      loadMenuTree()
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
  menuDialogVisible.value = false
  menuFormRef.value?.resetFields()
}

const handleDelete = (row) => {
  if (row.children && row.children.length > 0) {
    message.warning('请先删除子菜单')
    return
  }
  Modal.confirm({
    title: '提示',
    content: `确定要删除菜单${row.menuName}吗？删除后不可恢复！`,
    okText: '确定',
    cancelText: '取消',
    icon: h(QuestionCircleOutlined),
    okType: 'danger',
    onOk: async () => {
      try {
        const result = await request.delete(`/system/menu/${row.id}`)
        if (result.code === 200) {
          message.success('删除成功')
          loadMenuTree()
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

const handleImportBeforeUpload = (file) => {
  const isJson = file.type === 'application/json' || file.name.endsWith('.json')
  if (!isJson) {
    message.error('只能上传 JSON 文件！')
    return false
  }
  importFileList.value = [file]
  return false
}

const handleImportRemove = () => {
  importFileList.value = []
}

const handleImportSubmit = async () => {
  if (importFileList.value.length === 0) {
    message.warning('请选择要上传的文件')
    return
  }
  const file = importFileList.value[0]
  const reader = new FileReader()
  reader.onload = async (e) => {
    try {
      const data = JSON.parse(e.target.result)
      const result = await request.post('/system/menu/import', data)
      if (result.code === 200) {
        message.success('导入成功')
        importDialogVisible.value = false
        loadMenuTree()
      } else {
        message.error(result.message || '导入失败')
      }
    } catch (error) {
      message.error('JSON 格式错误')
    }
  }
  reader.readAsText(file)
}

const handleExport = () => {
  exportDialogVisible.value = true
}

const handleExportSubmit = async () => {
  try {
    let url = '/system/menu/export?'
    if (exportScope.value === 'selected') {
      // TODO: 实现选中导出
      url += 'scope=selected'
    }

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
    link.download = `菜单数据_${new Date().getTime()}.xlsx`
    link.click()
    URL.revokeObjectURL(link.href)
    message.success('导出成功')
    exportDialogVisible.value = false
  } catch (error) {
    console.error('导出失败:', error)
    message.error(error.message || '导出失败，请稍后重试')
  }
}

onMounted(() => {
  loadMenuTree()
})
</script>

<style scoped lang="scss">
.menus-container {
  .search-bar { margin-bottom: 20px; }
  .hint { font-size: 12px; color: #999; margin-top: 8px; }
  .icon-picker {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
    span {
      cursor: pointer;
      padding: 4px 8px;
      border: 1px solid #d9d9d9;
      border-radius: 4px;
      &:hover {
        border-color: #1890ff;
        color: #1890ff;
      }
    }
  }
}
</style>
