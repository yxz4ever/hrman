<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">员工管理</h1>
      <p class="page-description">管理企业员工档案，整合用户与员工信息</p>
    </div>

    <div class="page-content">
      <!-- 搜索栏 -->
      <a-form :model="searchForm" layout="inline" class="search-bar">
        <a-form-item label="姓名">
          <a-input v-model:value="searchForm.realName" placeholder="请输入姓名" allow-clear style="width: 150px" />
        </a-form-item>
        <a-form-item label="工号">
          <a-input v-model:value="searchForm.empNo" placeholder="请输入工号" allow-clear style="width: 150px" />
        </a-form-item>
        <a-form-item label="部门">
          <a-select v-model:value="searchForm.deptId" placeholder="请选择部门" allow-clear style="width: 150px">
            <a-select-option v-for="dept in departmentList" :key="dept.id" :value="dept.id">
              {{ dept.deptName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="searchForm.employeeStatus" placeholder="请选择状态" allow-clear style="width: 120px">
            <a-select-option :value="1">在职</a-select-option>
            <a-select-option :value="0">离职</a-select-option>
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
            新增员工
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 表格 -->
      <div class="table-container">
        <a-spin :spinning="loading">
          <a-table
            :columns="columns"
            :data-source="tableData"
            :row-key="record => record.id"
            bordered
            :pagination="false"
            :scroll="{ x: 1400 }"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'avatar'">
                <a-avatar :src="record.avatar || defaultAvatar" size="default">
                  {{ record.realName?.charAt(0) }}
                </a-avatar>
              </template>
              <template v-else-if="column.dataIndex === 'gender'">
                <span :style="{ color: record.gender === 1 ? '#1890ff' : '#eb2f96' }">
                  {{ record.gender === 1 ? '男' : '女' }}
                </span>
              </template>
              <template v-else-if="column.dataIndex === 'employeeType'">
                <a-tag :color="getEmployeeTypeColor(record.employeeType)">
                  {{ getEmployeeTypeText(record.employeeType) }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'employeeStatus'">
                <a-tag :color="record.employeeStatus === 1 ? 'green' : 'default'">
                  {{ getEmployeeStatusText(record.employeeStatus) }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'userType'">
                <a-tag :color="record.userType === 1 ? 'blue' : 'orange'">
                  {{ record.userType === 1 ? '系统用户' : '普通员工' }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'action'">
                <a-space>
                  <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
                  <a-button type="link" size="small" @click="handleDetail(record)">详情</a-button>
                  <a-button type="link" size="small" danger @click="handleDelete(record)">删除</a-button>
                </a-space>
              </template>
            </template>
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

    <!-- 新增/编辑对话框 -->
    <a-modal
      v-model:open="dialogVisible"
      :title="dialogTitle"
      width="900px"
      @ok="handleSubmit"
      :confirm-loading="submitting"
    >
      <a-tabs v-model:activeKey="activeTab">
        <!-- 基本信息 Tab -->
        <a-tab-pane key="basic" tab="基本信息">
          <a-form
            ref="formRef"
            :model="formData"
            :rules="formRules"
            layout="vertical"
            :label-col="{ span: 5 }"
            :wrapper-col="{ span: 17 }"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="用户名" name="username">
                  <a-input v-model:value="formData.username" placeholder="系统登录用户名（可选）" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="密码" name="password" v-if="!formData.id">
                  <a-input-password v-model:value="formData.password" placeholder="系统登录密码（可选）" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="姓名" name="realName">
                  <a-input v-model:value="formData.realName" placeholder="请输入姓名" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="性别" name="gender">
                  <a-radio-group v-model:value="formData.gender">
                    <a-radio :value="1">男</a-radio>
                    <a-radio :value="0">女</a-radio>
                  </a-radio-group>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="手机号" name="mobile">
                  <a-input v-model:value="formData.mobile" placeholder="请输入手机号" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="邮箱" name="email">
                  <a-input v-model:value="formData.email" placeholder="请输入邮箱" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="身份证号" name="idCard">
                  <a-input v-model:value="formData.idCard" placeholder="请输入身份证号" />
                </a-form-item>
              </a-col>
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
            </a-row>

            <a-row :gutter="16">
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
              <a-col :span="12">
                <a-form-item label="用户类型" name="userType">
                  <a-select v-model:value="formData.userType" style="width: 100%">
                    <a-select-option :value="1">系统用户</a-select-option>
                    <a-select-option :value="2">普通员工</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-tab-pane>

        <!-- 工作信息 Tab -->
        <a-tab-pane key="work" tab="工作信息">
          <a-form
            ref="workFormRef"
            :model="formData"
            layout="vertical"
            :label-col="{ span: 5 }"
            :wrapper-col="{ span: 17 }"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="所属部门" name="deptId">
                  <a-select v-model:value="formData.deptId" placeholder="请选择部门" style="width: 100%">
                    <a-select-option v-for="dept in departmentList" :key="dept.id" :value="dept.id">
                      {{ dept.deptName }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="所属岗位" name="postId">
                  <a-select v-model:value="formData.postId" placeholder="请选择岗位" style="width: 100%">
                    <a-select-option v-for="pos in positionList" :key="pos.id" :value="pos.id">
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
                <a-form-item label="职级" name="jobLevel">
                  <a-input v-model:value="formData.jobLevel" placeholder="请输入职级" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="学历" name="education">
                  <a-input v-model:value="formData.education" placeholder="请输入学历" />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-tab-pane>

        <!-- 详细信息 Tab -->
        <a-tab-pane key="detail" tab="详细信息">
          <a-form
            ref="detailFormRef"
            :model="detailData"
            layout="vertical"
            :label-col="{ span: 5 }"
            :wrapper-col="{ span: 17 }"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="工号">
                  <a-input v-model:value="detailData.empNo" placeholder="员工工号" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="民族">
                  <a-input v-model:value="detailData.nation" placeholder="请输入民族" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="籍贯">
                  <a-input v-model:value="detailData.nativePlace" placeholder="请输入籍贯" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="婚姻状况">
                  <a-select v-model:value="detailData.maritalStatus" style="width: 100%">
                    <a-select-option :value="0">未婚</a-select-option>
                    <a-select-option :value="1">已婚</a-select-option>
                    <a-select-option :value="2">离异</a-select-option>
                    <a-select-option :value="3">丧偶</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="用工形式">
                  <a-select v-model:value="detailData.employmentForm" style="width: 100%">
                    <a-select-option :value="1">全职</a-select-option>
                    <a-select-option :value="2">兼职</a-select-option>
                    <a-select-option :value="3">劳务</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="转正日期">
                  <a-date-picker
                    v-model:value="detailData.regularDate"
                    placeholder="选择转正日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="家庭住址">
                  <a-input v-model:value="detailData.address" placeholder="请输入家庭住址" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="紧急联系人">
                  <a-input v-model:value="detailData.emergencyContact" placeholder="紧急联系人" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="紧急联系电话">
                  <a-input v-model:value="detailData.emergencyPhone" placeholder="紧急联系电话" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="政治面貌">
                  <a-input v-model:value="detailData.politicalStatus" placeholder="政治面貌" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="毕业院校">
                  <a-input v-model:value="detailData.graduationSchool" placeholder="毕业院校" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="专业">
                  <a-input v-model:value="detailData.major" placeholder="专业" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="毕业日期">
                  <a-date-picker
                    v-model:value="detailData.graduationDate"
                    placeholder="选择毕业日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="银行账号">
                  <a-input v-model:value="detailData.bankAccount" placeholder="银行账号" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-form-item label="备注">
              <a-textarea v-model:value="detailData.remark" :rows="3" placeholder="备注信息" />
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </tabs>
    </a-modal>

    <!-- 详情对话框 -->
    <a-modal
      v-model:open="detailDialogVisible"
      title="员工详情"
      width="800px"
      :footer="null"
    >
      <a-descriptions bordered :column="2">
        <a-descriptions-item label="姓名">{{ currentDetail.realName }}</a-descriptions-item>
        <a-descriptions-item label="工号">{{ currentDetail.empNo || '-' }}</a-descriptions-item>
        <a-descriptions-item label="性别">{{ currentDetail.gender === 1 ? '男' : '女' }}</a-descriptions-item>
        <a-descriptions-item label="手机号">{{ currentDetail.mobile || '-' }}</a-descriptions-item>
        <a-descriptions-item label="部门">{{ currentDetail.deptName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="岗位">{{ currentDetail.postName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="入职时间">{{ currentDetail.hireDate || '-' }}</a-descriptions-item>
        <a-descriptions-item label="生日">{{ currentDetail.birthDate || '-' }}</a-descriptions-item>
        <a-descriptions-item label="民族">{{ currentDetail.nation || '-' }}</a-descriptions-item>
        <a-descriptions-item label="籍贯">{{ currentDetail.nativePlace || '-' }}</a-descriptions-item>
        <a-descriptions-item label="学历">{{ currentDetail.education || '-' }}</a-descriptions-item>
        <a-descriptions-item label="毕业院校">{{ currentDetail.graduationSchool || '-' }}</a-descriptions-item>
        <a-descriptions-item label="专业">{{ currentDetail.major || '-' }}</a-descriptions-item>
        <a-descriptions-item label="家庭住址">{{ currentDetail.address || '-' }}</a-descriptions-item>
        <a-descriptions-item label="紧急联系人">{{ currentDetail.emergencyContact || '-' }}</a-descriptions-item>
        <a-descriptions-item label="紧急电话">{{ currentDetail.emergencyPhone || '-' }}</a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">{{ currentDetail.remark || '-' }}</a-descriptions-item>
      </a-descriptions>
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
  PlusOutlined
} from '@ant-design/icons-vue'
import {
  getEmployeePage,
  addEmployee,
  updateEmployee,
  deleteEmployee,
  getEmployeeDetail,
  updateEmployeeDetail
} from '@/api/employee'
import { getDepartmentList } from '@/api/department'
import { getPositionList } from '@/api/position'

const defaultAvatar = 'https://gw.alipayobjects.com/zos/antfincdn/aPkFc8Sj7n/default-avatar.png'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('新增员工')
const formRef = ref(null)
const workFormRef = ref(null)
const detailFormRef = ref(null)
const activeTab = ref('basic')

const searchForm = reactive({
  realName: '',
  empNo: '',
  deptId: null,
  employeeStatus: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
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
  userType: 2,
  jobLevel: '',
  education: ''
})

const detailData = reactive({
  id: null,
  userId: null,
  empNo: '',
  nation: '',
  nativePlace: '',
  maritalStatus: null,
  employmentForm: null,
  regularDate: null,
  address: '',
  emergencyContact: '',
  emergencyPhone: '',
  politicalStatus: '',
  graduationSchool: '',
  major: '',
  graduationDate: null,
  bankAccount: '',
  remark: ''
})

const formRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const columns = [
  { title: '头像', dataIndex: 'avatar', width: 70, scopedSlots: true },
  { title: '姓名', dataIndex: 'realName', width: 100 },
  { title: '性别', dataIndex: 'gender', width: 60, scopedSlots: true },
  { title: '手机号', dataIndex: 'mobile', width: 120 },
  { title: '部门', dataIndex: 'deptName', width: 120 },
  { title: '入职时间', dataIndex: 'hireDate', width: 110 },
  { title: '员工类型', dataIndex: 'employeeType', width: 80, scopedSlots: true },
  { title: '在职状态', dataIndex: 'employeeStatus', width: 80, scopedSlots: true },
  { title: '用户类型', dataIndex: 'userType', width: 90, scopedSlots: true },
  { title: '操作', dataIndex: 'action', width: 180, fixed: 'right', scopedSlots: true }
]

const departmentList = ref([])
const positionList = ref([])
const currentDetail = ref({})

const getEmployeeTypeText = (type) => {
  const map = { 1: '正式', 2: '试用', 3: '实习', 4: '外包' }
  return map[type] || '-'
}

const getEmployeeTypeColor = (type) => {
  const map = { 1: 'blue', 2: 'orange', 3: 'green', 4: 'purple' }
  return map[type] || 'default'
}

const getEmployeeStatusText = (status) => {
  const map = { 0: '离职', 1: '在职', 2: '停薪留职' }
  return map[status] || '-'
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
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

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

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.realName = ''
  searchForm.empNo = ''
  searchForm.deptId = null
  searchForm.employeeStatus = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增员工'
  activeTab.value = 'basic'
  Object.assign(formData, {
    id: null, username: '', password: '', realName: '', gender: 1,
    mobile: '', email: '', idCard: '', birthDate: null, hireDate: null,
    deptId: null, postId: null, employeeType: 1, employeeStatus: 1,
    userType: 2, jobLevel: '', education: ''
  })
  Object.assign(detailData, {
    id: null, userId: null, empNo: '', nation: '', nativePlace: '',
    maritalStatus: null, employmentForm: null, regularDate: null,
    address: '', emergencyContact: '', emergencyPhone: '',
    politicalStatus: '', graduationSchool: '', major: '',
    graduationDate: null, bankAccount: '', remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑员工'
  activeTab.value = 'basic'
  Object.assign(formData, {
    id: row.id,
    username: row.username || '',
    password: '',
    realName: row.realName || '',
    gender: row.gender,
    mobile: row.mobile || '',
    email: row.email || '',
    idCard: row.idCard || '',
    birthDate: row.birthDate ? dayjs(row.birthDate) : null,
    hireDate: row.hireDate ? dayjs(row.hireDate) : null,
    deptId: row.deptId,
    postId: row.postId,
    employeeType: row.employeeType,
    employeeStatus: row.employeeStatus,
    userType: row.userType,
    jobLevel: row.jobLevel || '',
    education: row.education || ''
  })

  // 加载详情
  try {
    const res = await getEmployeeDetail(row.id)
    if (res.code === 200 && res.data) {
      const d = res.data
      Object.assign(detailData, {
        id: d.id,
        userId: d.userId,
        empNo: d.empNo || '',
        nation: d.nation || '',
        nativePlace: d.nativePlace || '',
        maritalStatus: d.maritalStatus,
        employmentForm: d.employmentForm,
        regularDate: d.regularDate ? dayjs(d.regularDate) : null,
        address: d.address || '',
        emergencyContact: d.emergencyContact || '',
        emergencyPhone: d.emergencyPhone || '',
        politicalStatus: d.politicalStatus || '',
        graduationSchool: d.graduationSchool || '',
        major: d.major || '',
        graduationDate: d.graduationDate ? dayjs(d.graduationDate) : null,
        bankAccount: d.bankAccount || '',
        remark: d.remark || ''
      })
    }
  } catch (error) {
    console.error('加载详情失败:', error)
  }

  dialogVisible.value = true
}

const handleDetail = async (row) => {
  currentDetail.value = { ...row }
  try {
    const res = await getEmployeeDetail(row.id)
    if (res.code === 200 && res.data) {
      Object.assign(currentDetail.value, res.data)
    }
  } catch (error) {
    console.error('加载详情失败:', error)
  }
  detailDialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    // 转换日期格式
    const user = {
      ...formData,
      birthDate: formData.birthDate ? formData.birthDate.format('YYYY-MM-DD') : null,
      hireDate: formData.hireDate ? formData.hireDate.format('YYYY-MM-DD') : null
    }

    const detail = {
      ...detailData,
      regularDate: detailData.regularDate ? detailData.regularDate.format('YYYY-MM-DD') : null,
      graduationDate: detailData.graduationDate ? detailData.graduationDate.format('YYYY-MM-DD') : null
    }

    if (formData.id) {
      // 更新
      await updateEmployee(user)
      if (detailData.id || detailData.empNo || detailData.nation) {
        detail.userId = formData.id
        await updateEmployeeDetail(detail)
      }
      message.success('更新成功')
    } else {
      // 新增
      await addEmployee(user)
      message.success('添加成功')
    }

    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
    message.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = (row) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除员工 "${row.realName}" 吗？`,
    okText: '确认',
    cancelText: '取消',
    okType: 'danger',
    onOk: async () => {
      try {
        await deleteEmployee(row.id)
        message.success('删除成功')
        loadData()
      } catch (error) {
        console.error('删除失败:', error)
        message.error('删除失败')
      }
    }
  })
}

onMounted(() => {
  loadData()
  loadDepartmentList()
  loadPositionList()
})
</script>

<style scoped lang="scss">
.page-container {
  padding: 24px;

  .search-bar {
    margin-bottom: 16px;
  }

  .table-container {
    background: #fff;
    border-radius: 8px;
    margin-bottom: 16px;
  }
}
</style>
