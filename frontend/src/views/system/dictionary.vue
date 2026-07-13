<template>
  <div class="dictionary-page">
    <a-card :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" class="search-form">
        <a-form-item label="字典名称">
          <a-input v-model:value="searchForm.dictName" placeholder="请输入字典名称" allow-clear />
        </a-form-item>
        <a-form-item label="字典编码">
          <a-input v-model:value="searchForm.dictCode" placeholder="请输入字典编码" allow-clear />
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
        <a-button type="primary" @click="handleAddType">
          <template #icon><PlusOutlined /></template>
          新增字典类型
        </a-button>
      </div>

      <!-- 字典类型表格 -->
      <a-table
        :columns="typeColumns"
        :data-source="typeList"
        :loading="typeLoading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 1 ? 'green' : 'red'">
              {{ record.status === 1 ? '启用' : '禁用' }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a @click="handleViewItems(record)">字典数据</a>
              <a @click="handleEditType(record)">编辑</a>
              <a-popconfirm title="确定删除该字典类型吗？" @confirm="handleDeleteType(record)">
                <a class="error-color">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 字典类型弹窗 -->
    <a-modal
      v-model:open="typeModalVisible"
      :title="typeModalTitle"
      @ok="handleTypeModalOk"
      :confirm-loading="typeModalConfirmLoading"
    >
      <a-form :model="typeForm" :rules="typeRules" ref="typeFormRef" layout="vertical">
        <a-form-item label="字典名称" name="dictName">
          <a-input v-model:value="typeForm.dictName" placeholder="请输入字典名称" />
        </a-form-item>
        <a-form-item label="字典编码" name="dictCode">
          <a-input v-model:value="typeForm.dictCode" placeholder="请输入字典编码" />
        </a-form-item>
        <a-form-item label="字典描述" name="description">
          <a-textarea v-model:value="typeForm.description" placeholder="请输入字典描述" :rows="3" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="typeForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 字典数据弹窗 -->
    <a-modal
      v-model:open="itemModalVisible"
      :title="itemModalTitle"
      @ok="handleItemModalOk"
      :confirm-loading="itemModalConfirmLoading"
    >
      <a-form :model="itemForm" :rules="itemRules" ref="itemFormRef" layout="vertical">
        <a-form-item label="字典标签" name="dictLabel">
          <a-input v-model:value="itemForm.dictLabel" placeholder="请输入字典标签" />
        </a-form-item>
        <a-form-item label="字典值" name="dictValue">
          <a-input v-model:value="itemForm.dictValue" placeholder="请输入字典值" />
        </a-form-item>
        <a-form-item label="排序" name="dictSort">
          <a-input-number v-model:value="itemForm.dictSort" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="备注" name="description">
          <a-textarea v-model:value="itemForm.description" placeholder="请输入备注" :rows="3" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="itemForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 字典数据列表弹窗 -->
    <a-modal
      v-model:open="itemListModalVisible"
      title="字典数据"
      width="800px"
      :footer="null"
    >
      <div class="table-toolbar" style="margin-bottom: 16px">
        <a-button type="primary" @click="handleAddItem">
          <template #icon><PlusOutlined /></template>
          新增字典数据
        </a-button>
      </div>
      <a-table
        :columns="itemColumns"
        :data-source="itemList"
        :pagination="false"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 1 ? 'green' : 'red'">
              {{ record.status === 1 ? '启用' : '禁用' }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a @click="handleEditItem(record)">编辑</a>
              <a-popconfirm title="确定删除该字典数据吗？" @confirm="handleDeleteItem(record)">
                <a class="error-color">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue'
import request from '@/utils/request'

// 搜索表单
const searchForm = reactive({
  dictName: '',
  dictCode: ''
})

// 字典类型列表
const typeList = ref([])
const typeLoading = ref(false)
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

// 字典类型表格列
const typeColumns = [
  { title: '字典名称', dataIndex: 'dictName', width: 150 },
  { title: '字典编码', dataIndex: 'dictCode', width: 150 },
  { title: '字典描述', dataIndex: 'description', ellipsis: true },
  { title: '状态', dataIndex: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createdAt', width: 180 },
  { title: '操作', dataIndex: 'action', width: 200, fixed: 'right' }
]

// 字典类型弹窗
const typeModalVisible = ref(false)
const typeModalTitle = ref('新增字典类型')
const typeModalConfirmLoading = ref(false)
const typeFormRef = ref(null)
const typeForm = reactive({
  id: null,
  dictName: '',
  dictCode: '',
  description: '',
  status: 1
})
const typeRules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictCode: [{ required: true, message: '请输入字典编码', trigger: 'blur' }]
}

// 字典数据列表
const itemListModalVisible = ref(false)
const itemList = ref([])
const currentItemTypeId = ref(null)

// 字典数据弹窗
const itemModalVisible = ref(false)
const itemModalTitle = ref('新增字典数据')
const itemModalConfirmLoading = ref(false)
const itemFormRef = ref(null)
const itemForm = reactive({
  id: null,
  dictTypeId: null,
  dictLabel: '',
  dictValue: '',
  dictSort: 0,
  description: '',
  status: 1
})
const itemRules = {
  dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }]
}

// 字典数据表格列
const itemColumns = [
  { title: '字典标签', dataIndex: 'dictLabel', width: 150 },
  { title: '字典值', dataIndex: 'dictValue', width: 150 },
  { title: '排序', dataIndex: 'dictSort', width: 100 },
  { title: '备注', dataIndex: 'description', ellipsis: true },
  { title: '状态', dataIndex: 'status', width: 100 },
  { title: '操作', dataIndex: 'action', width: 150, fixed: 'right' }
]

// 加载字典类型列表
const loadTypeList = async () => {
  typeLoading.value = true
  try {
    const res = await request.get('/system/dict/type/list')
    if (res.code === 200) {
      typeList.value = res.data || []
      pagination.total = typeList.value.length
    }
  } catch (error) {
    console.error('加载字典类型列表失败:', error)
  } finally {
    typeLoading.value = false
  }
}

// 加载字典数据列表
const loadItemList = async (dictTypeId) => {
  try {
    const res = await request.get('/system/dict/item/list', { params: { dictTypeId } })
    if (res.code === 200) {
      itemList.value = res.data || []
    }
  } catch (error) {
    console.error('加载字典数据列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadTypeList()
}

// 重置
const handleReset = () => {
  searchForm.dictName = ''
  searchForm.dictCode = ''
  handleSearch()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadTypeList()
}

// 新增字典类型
const handleAddType = () => {
  typeModalTitle.value = '新增字典类型'
  Object.assign(typeForm, {
    id: null,
    dictName: '',
    dictCode: '',
    description: '',
    status: 1
  })
  typeModalVisible.value = true
}

// 编辑字典类型
const handleEditType = (record) => {
  typeModalTitle.value = '编辑字典类型'
  Object.assign(typeForm, record)
  typeModalVisible.value = true
}

// 查看字典数据
const handleViewItems = (record) => {
  currentItemTypeId.value = record.id
  loadItemList(record.id)
  itemListModalVisible.value = true
}

// 字典类型弹窗确认
const handleTypeModalOk = async () => {
  try {
    await typeFormRef.value.validate()
    typeModalConfirmLoading.value = true
    
    const url = typeForm.id ? `/system/dict/type/${typeForm.id}` : '/system/dict/type'
    const method = typeForm.id ? 'put' : 'post'
    
    const res = await request[method](url, typeForm)
    if (res.code === 200) {
      message.success(typeForm.id ? '更新成功' : '新增成功')
      typeModalVisible.value = false
      loadTypeList()
    }
  } catch (error) {
    if (error.response?.status !== 422) {
      console.error('保存失败:', error)
    }
  } finally {
    typeModalConfirmLoading.value = false
  }
}

// 删除字典类型
const handleDeleteType = async (record) => {
  try {
    const res = await request.delete(`/system/dict/type/${record.id}`)
    if (res.code === 200) {
      message.success('删除成功')
      loadTypeList()
    }
  } catch (error) {
    console.error('删除失败:', error)
  }
}

// 新增字典数据
const handleAddItem = () => {
  itemModalTitle.value = '新增字典数据'
  Object.assign(itemForm, {
    id: null,
    dictTypeId: currentItemTypeId.value,
    dictLabel: '',
    dictValue: '',
    dictSort: 0,
    description: '',
    status: 1
  })
  itemModalVisible.value = true
}

// 编辑字典数据
const handleEditItem = (record) => {
  itemModalTitle.value = '编辑字典数据'
  Object.assign(itemForm, record)
  itemModalVisible.value = true
}

// 字典数据弹窗确认
const handleItemModalOk = async () => {
  try {
    await itemFormRef.value.validate()
    itemModalConfirmLoading.value = true
    
    const url = itemForm.id ? `/system/dict/item/${itemForm.id}` : '/system/dict/item'
    const method = itemForm.id ? 'put' : 'post'
    
    const res = await request[method](url, itemForm)
    if (res.code === 200) {
      message.success(itemForm.id ? '更新成功' : '新增成功')
      itemModalVisible.value = false
      loadItemList(currentItemTypeId.value)
    }
  } catch (error) {
    if (error.response?.status !== 422) {
      console.error('保存失败:', error)
    }
  } finally {
    itemModalConfirmLoading.value = false
  }
}

// 删除字典数据
const handleDeleteItem = async (record) => {
  try {
    const res = await request.delete(`/system/dict/item/${record.id}`)
    if (res.code === 200) {
      message.success('删除成功')
      loadItemList(currentItemTypeId.value)
    }
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => {
  loadTypeList()
})
</script>

<style scoped lang="scss">
.dictionary-page {
  padding: 24px;

  .search-form {
    margin-bottom: 16px;
  }

  .table-toolbar {
    margin-bottom: 16px;
  }

  .error-color {
    color: #ff4d4f;

    &:hover {
      color: #ff7875;
    }
  }
}
</style>
