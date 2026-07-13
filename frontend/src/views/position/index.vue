<template>
  <div class="position-page">
    <a-card :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" class="search-bar">
        <a-form-item label="职位名称">
          <a-input
            v-model:value="searchForm.positionName"
            placeholder="请输入职位名称"
            allow-clear
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="所属部门">
          <a-select
            v-model:value="searchForm.deptId"
            placeholder="请选择部门"
            allow-clear
            style="width: 200px"
          >
            <a-select-option
              v-for="dept in departmentList"
              :key="dept.id"
              :value="dept.id"
            >
              {{ dept.deptName }}
            </a-select-option>
          </a-select>
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
            新增职位
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 表格 -->
      <a-table
        :columns="columns"
        :data-source="positionList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
        :scroll="{ x: 900 }"
      >
        <!-- 职位名称 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'positionName'">
            <span style="font-weight: 500">{{ record.positionName }}</span>
          </template>
          <!-- 部门名称 -->
          <template v-else-if="column.dataIndex === 'deptName'">
            <span>{{ record.deptName || '-' }}</span>
          </template>
          <!-- 职级 -->
          <template v-else-if="column.dataIndex === 'postLevel'">
            <a-tag v-if="record.postLevel" color="blue">{{ record.postLevel }}</a-tag>
            <span v-else>-</span>
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
        <a-form-item label="所属部门" name="deptId">
          <a-select
            v-model:value="formData.deptId"
            placeholder="请选择所属部门"
            style="width: 100%"
          >
            <a-select-option
              v-for="dept in departmentList"
              :key="dept.id"
              :value="dept.id"
            >
              {{ dept.deptName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="职位名称" name="positionName">
          <a-input v-model:value="formData.positionName" placeholder="请输入职位名称" />
        </a-form-item>
        <a-form-item label="岗位编码" name="postCode">
          <a-input v-model:value="formData.postCode" placeholder="请输入岗位编码" />
        </a-form-item>
        <a-form-item label="职级" name="postLevel">
          <a-input v-model:value="formData.postLevel" placeholder="请输入职级（如：P1、M2 等）" />
        </a-form-item>
        <a-form-item label="岗位描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入岗位描述"
            :rows="4"
          />
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
  PlusOutlined
} from '@ant-design/icons-vue'
import {
  getPositionPage,
  addPosition,
  updatePosition,
  deletePosition,
  getPositionList
} from '@/api/position'
import { getDepartmentList } from '@/api/department'

// 搜索表单
const searchForm = reactive({
  positionName: '',
  deptId: null
})

// 表格数据
const positionList = ref([])
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

// 部门列表
const departmentList = ref([])

// 对话框
const modalVisible = ref(false)
const modalTitle = ref('')
const formRef = ref(null)
const formData = reactive({
  id: null,
  deptId: null,
  positionName: '',
  postCode: '',
  postLevel: '',
  description: '',
  sortOrder: 0,
  status: 1
})

// 表单验证规则
const formRules = {
  positionName: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
  postCode: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择所属部门', trigger: 'change' }]
}

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 60
  },
  {
    title: '职位名称',
    dataIndex: 'positionName',
    scopedSlots: true
  },
  {
    title: '岗位编码',
    dataIndex: 'postCode',
    width: 120
  },
  {
    title: '所属部门',
    dataIndex: 'deptName',
    scopedSlots: true,
    width: 150
  },
  {
    title: '职级',
    dataIndex: 'postLevel',
    scopedSlots: true,
    width: 100
  },
  {
    title: '岗位描述',
    dataIndex: 'description',
    ellipsis: true,
    customRender: ({ record }) => ({
      children: record.description || '-'
    })
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

// 加载职位列表
const loadPositionList = async () => {
  loading.value = true
  try {
    const res = await getPositionPage({
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    if (res.code === 200) {
      positionList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载职位列表失败:', error)
    message.error('加载职位列表失败')
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

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadPositionList()
}

// 重置
const handleReset = () => {
  searchForm.positionName = ''
  searchForm.deptId = null
  handleSearch()
}

// 表格变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadPositionList()
}

// 新增
const handleAdd = () => {
  modalTitle.value = '新增职位'
  Object.assign(formData, {
    id: null,
    deptId: null,
    positionName: '',
    postCode: '',
    postLevel: '',
    description: '',
    sortOrder: 0,
    status: 1
  })
  modalVisible.value = true
}

// 编辑
const handleEdit = (record) => {
  modalTitle.value = '编辑职位'
  Object.assign(formData, {
    id: record.id,
    deptId: record.deptId,
    positionName: record.positionName,
    postCode: record.postCode,
    postLevel: record.postLevel,
    description: record.description,
    sortOrder: record.sortOrder,
    status: record.status
  })
  modalVisible.value = true
}

// 对话框确认
const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    const api = formData.id ? updatePosition : addPosition
    const res = await api(formData)
    if (res.code === 200) {
      message.success(formData.id ? '修改成功' : '添加成功')
      modalVisible.value = false
      loadPositionList()
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 删除
const handleDelete = (record) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除职位 "${record.positionName}" 吗？`,
    okText: '确认',
    cancelText: '取消',
    okType: 'danger',
    onOk: async () => {
      try {
        const res = await deletePosition(record.id)
        if (res.code === 200) {
          message.success('删除成功')
          loadPositionList()
        }
      } catch (error) {
        console.error('删除失败:', error)
        message.error('删除失败')
      }
    }
  })
}

onMounted(() => {
  loadPositionList()
  loadDepartmentList()
})
</script>

<style scoped lang="scss">
.position-page {
  padding: 24px;

  .search-bar {
    margin-bottom: 16px;
  }
}
</style>
