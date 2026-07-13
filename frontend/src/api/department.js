import request from '@/utils/request'

/**
 * 部门管理 API
 */

/**
 * 分页查询部门
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.deptName - 部门名称
 * @returns {Promise}
 */
export function getDepartmentPage(params) {
  return request({
    url: '/employee/department',
    method: 'get',
    params: {
      current: params.pageNum,
      size: params.pageSize,
      deptName: params.deptName
    }
  })
}

/**
 * 获取部门树
 * @returns {Promise}
 */
export function getDepartmentTree() {
  return request({
    url: '/employee/department/tree',
    method: 'get'
  })
}

/**
 * 获取所有部门
 * @returns {Promise}
 */
export function getDepartmentList() {
  return request({
    url: '/employee/department/list',
    method: 'get'
  })
}

/**
 * 获取部门详情
 * @param {number} id - 部门 ID
 * @returns {Promise}
 */
export function getDepartmentById(id) {
  return request({
    url: `/employee/department/${id}`,
    method: 'get'
  })
}

/**
 * 添加部门
 * @param {Object} data - 部门数据
 * @returns {Promise}
 */
export function addDepartment(data) {
  return request({
    url: '/employee/department',
    method: 'post',
    data
  })
}

/**
 * 修改部门
 * @param {Object} data - 部门数据
 * @returns {Promise}
 */
export function updateDepartment(data) {
  return request({
    url: '/employee/department',
    method: 'put',
    data
  })
}

/**
 * 删除部门
 * @param {number} id - 部门 ID
 * @returns {Promise}
 */
export function deleteDepartment(id) {
  return request({
    url: `/employee/department/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除部门
 * @param {Array} ids - 部门 ID 数组
 * @returns {Promise}
 */
export function deleteDepartmentBatch(ids) {
  return request({
    url: '/employee/department/batch',
    method: 'delete',
    data: ids
  })
}
