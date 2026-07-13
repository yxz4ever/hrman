import request from '@/utils/request'

/**
 * 员工管理 API
 */

/**
 * 分页查询员工
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getEmployeePage(params) {
  return request({
    url: '/employee/employee/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有员工（用于下拉选择）
 * @returns {Promise}
 */
export function getEmployeeList() {
  return request({
    url: '/employee/employee/list',
    method: 'get'
  })
}

/**
 * 获取员工详情
 * @param {number} id - 员工 ID
 * @returns {Promise}
 */
export function getEmployeeById(id) {
  return request({
    url: `/employee/employee/${id}`,
    method: 'get'
  })
}

/**
 * 根据工号获取员工
 * @param {string} empNo - 工号
 * @returns {Promise}
 */
export function getEmployeeByEmpNo(empNo) {
  return request({
    url: `/employee/employee/emp-no/${empNo}`,
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
    url: '/employee/employee',
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
    url: '/employee/employee',
    method: 'put',
    data
  })
}

/**
 * 删除员工
 * @param {number} id - 员工 ID
 * @returns {Promise}
 */
export function deleteEmployee(id) {
  return request({
    url: `/employee/employee/${id}`,
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
    url: '/employee/employee/batch',
    method: 'delete',
    data: ids
  })
}
