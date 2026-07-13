import request from '@/utils/request'

/**
 * 员工管理 API（整合到用户管理）
 */

/**
 * 分页查询员工（用户）
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getEmployeePage(params) {
  return request({
    url: '/system/user',
    method: 'get',
    params: {
      current: params.pageNum,
      size: params.pageSize,
      username: params.username,
      realName: params.realName,
      userType: params.userType,
      employeeStatus: params.employeeStatus,
      deptId: params.deptId
    }
  })
}

/**
 * 获取所有员工（用于下拉选择）
 * @returns {Promise}
 */
export function getEmployeeList() {
  return request({
    url: '/system/user/list',
    method: 'get'
  })
}

/**
 * 获取员工详情（包含用户和详情）
 * @param {number} id - 员工 ID
 * @returns {Promise}
 */
export function getEmployeeById(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

/**
 * 添加员工
 * @param {Object} data - 员工数据
 * @returns {Promise}
 */
export function addEmployee(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

/**
 * 修改员工
 * @param {Object} data - 员工数据
 * @returns {Promise}
 */
export function updateEmployee(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

/**
 * 保存员工及详情
 * @param {Object} data - 包含 user 和 detail 的对象
 * @returns {Promise}
 */
export function saveEmployeeWithDetail(data) {
  return request({
    url: '/system/user/with-detail',
    method: 'put',
    data
  })
}

/**
 * 更新员工详情
 * @param {Object} data - 员工详情数据
 * @returns {Promise}
 */
export function updateEmployeeDetail(data) {
  return request({
    url: '/system/user/detail',
    method: 'post',
    data
  })
}

/**
 * 获取员工详情
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export function getEmployeeDetail(userId) {
  return request({
    url: `/system/user/detail/${userId}`,
    method: 'get'
  })
}

/**
 * 删除员工
 * @param {number} id - 员工 ID
 * @returns {Promise}
 */
export function deleteEmployee(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除员工
 * @param {Array} ids - 员工 ID 数组
 * @returns {Promise}
 */
export function deleteEmployeeBatch(ids) {
  return request({
    url: '/system/user/batch',
    method: 'delete',
    data: ids
  })
}
