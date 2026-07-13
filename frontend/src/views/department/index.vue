<template>
  <div class="department-page">
    <a-card :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" class="search-bar">
        <a-form-item label="部门名称">
          <a-input
            v-model:value="searchForm.deptName"
            placeholder="请输入部门名称"
            allow-clear
            style="width: 200px"
          />
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
        <a-form-item style="float: right">
          <a-button type="primary" @click="handleAdd">
            <template #icon><PlusOutlined /></template>
            新增部门
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 表格 -->
      <a-table
        :columns="columns"
        :data-source="departmentList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
        :scroll="{ x: 900 }"
      >
        <!-- 部门名称 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'deptName'">
            <span style="font-weight: 500">
              <component :is="getIcon(record)" />
              {{ record.deptName }}
            </span>
          </template>
          <!-- 状态 -->
          <template v-else-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 1 ? 'green' : 'red'">
              {{ record.status === 1 ? '启用' : '禁用' }}
            </a-tag>
          </template>
          <!-- 操作 -->
          <template v-else-if="column.dataIndex === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleEdit(record)">
                编辑
              </a-button>
              <a-button
                type="link"
                size="small"
                danger
                @click="handleDelete(record)"
              >
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑对话框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="handleModalOk"
      width="600px"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <a-form-item label="父级部门" name="parentId">
          <a-tree-select
            v-model:value="formData.parentId"
            :tree-data="departmentTree"
            placeholder="请选择父级部门（根部门不选）"
            allow-clear
            tree-default-expand-all
            :field-names="{ label: 'deptName', value: 'id' }"
          />
        </a-form-item>
        <a-form-item label="部门名称" name="deptName">
          <a-input v-model:value="formData.deptName" placeholder="请输入部门名称" />
        </a-form-item>
        <a-form-item label="部门编码" name="deptCode">
          <a-input v-model:value="formData.deptCode" placeholder="请输入部门编码" />
        </a-form-item>
        <a-form-item label="负责人" name="leaderId">
          <a-select
            v-model:value="formData.leaderId"
            placeholder="请选择负责人"
            style="width: 100%"
          >
            <a-select-option
              v-for="emp in employeeList"
              :key="emp.id"
              :value="emp.id"
            >
              {{ emp.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="联系电话" name="phone">
          <a-input v-model:value="formData.phone" placeholder="请输入联系电话" />
        </a-form-item>
        <a-form-item label="邮箱" name="email">
          <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item label="排序" name="sortOrder">
          <a-input-number
            v-model:value="formData.sortOrder"
            :min="0"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  TeamOutlined,
  FolderOutlined
} from '@ant-design/icons-vue'
import {
  getDepartmentPage,
  getDepartmentTree,
  addDepartment,
  updateDepartment,
  deleteDepartment
} from '@/api/department'
import { getEmployeeList } from '@/api/employee'

// 搜索表单
const searchForm = reactive({
  deptName: ''
})

// 表格数据
const departmentList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条`
})

// 部门树
const departmentTree = ref([])

// 员工列表
const employeeList = ref([])

// 对话框
const modalVisible = ref(false)
const modalTitle = ref('')
const formRef = ref(null)
const formData = reactive({
  id: null,
  parentId: null,
  deptName: '',
  deptCode: '',
  leaderId: null,
  phone: '',
  email: '',
  sortOrder: 0,
  status: 1
})

// 表单验证规则
const formRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  deptCode: [{ required: true, message: '请输入部门编码', trigger: 'blur' }]
}

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 60
  },
  {
    title: '部门名称',
    dataIndex: 'deptName',
    scopedSlots: true
  },
  {
    title: '部门编码',
    dataIndex: 'deptCode',
    width: 120
  },
  {
    title: '负责人',
    dataIndex: 'leaderName',
    width: 100
  },
  {
    title: '联系电话',
    dataIndex: 'phone',
    width: 120
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
    scopedSlots: true
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 120,
    fixed: 'right',
    scopedSlots: true
  }
]

// 获取部门图标
const getIcon = (record) => {
  return record.parentId ? TeamOutlined : FolderOutlined
}

// 加载部门列表
const loadDepartmentList = async () => {
  loading.value = true
  try {
    const res = await getDepartmentPage({
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    if (res.code === 200) {
      departmentList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载部门列表失败:', error)
    message.error('加载部门列表失败')
  } finally {
    loading.value = false
  }
}

// 加载部门树
const loadDepartmentTree = async () => {
  try {
    const res = await getDepartmentTree()
    if (res.code === 200) {
      departmentTree.value = res.data
    }
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

// 加载员工列表
const loadEmployeeList = async () => {
  try {
    const res = await getEmployeeList()
    if (res.code === 200) {
      employeeList.value = res.data
    }
  } catch (error) {
    console.error('加载员工列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadDepartmentList()
}

// 重置
const handleReset = () => {
  searchForm.deptName = ''
  handleSearch()
}

// 表格变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadDepartmentList()
}

// 新增
const handleAdd = () => {
  modalTitle.value = '新增部门'
  Object.assign(formData, {
    id: null,
    parentId: null,
    deptName: '',
    deptCode: '',
    leaderId: null,
    phone: '',
    email: '',
    sortOrder: 0,
    status: 1
  })
  modalVisible.value = true
}

// 编辑
const handleEdit = (record) => {
  modalTitle.value = '编辑部门'
  Object.assign(formData, {
    id: record.id,
    parentId: record.parentId,
    deptName: record.deptName,
    deptCode: record.deptCode,
    leaderId: record.leaderId,
    phone: record.phone,
    email: record.email,
    sortOrder: record.sortOrder,
    status: record.status
  })
  modalVisible.value = true
}

// 对话框确认
const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    const api = formData.id ? updateDepartment : addDepartment
    const res = await api(formData)
    if (res.code === 200) {
      message.success(formData.id ? '修改成功' : '添加成功')
      modalVisible.value = false
      loadDepartmentList()
      loadDepartmentTree()
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 删除
const handleDelete = (record) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除部门 "${record.deptName}" 吗？`,
    okText: '确认',
    cancelText: '取消',
    okType: 'danger',
    onOk: async () => {
      try {
        const res = await deleteDepartment(record.id)
        if (res.code === 200) {
          message.success('删除成功')
          loadDepartmentList()
          loadDepartmentTree()
        }
      } catch (error) {
        console.error('删除失败:', error)
        message.error('删除失败')
      }
    }
  })
}

onMounted(() => {
  loadDepartmentList()
  loadDepartmentTree()
  loadEmployeeList()
})
</script>

<style scoped lang="scss">
.department-page {
  padding: 24px;

  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
