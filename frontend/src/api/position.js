import request from '@/utils/request'

/**
 * 职位管理 API
 */

/**
 * 分页查询职位
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.positionName - 职位名称
 * @param {number} params.deptId - 部门 ID
 * @returns {Promise}
 */
export function getPositionPage(params) {
  return request({
    url: '/employee/position',
    method: 'get',
    params: {
      current: params.pageNum,
      size: params.pageSize,
      positionName: params.positionName,
      deptId: params.deptId
    }
  })
}

/**
 * 获取所有职位
 * @returns {Promise}
 */
export function getPositionList() {
  return request({
    url: '/employee/position/list',
    method: 'get'
  })
}

/**
 * 根据部门 ID 获取职位
 * @param {number} deptId - 部门 ID
 * @returns {Promise}
 */
export function getPositionByDeptId(deptId) {
  return request({
    url: `/employee/position/dept/${deptId}`,
    method: 'get'
  })
}

/**
 * 获取职位详情
 * @param {number} id - 职位 ID
 * @returns {Promise}
 */
export function getPositionById(id) {
  return request({
    url: `/employee/position/${id}`,
    method: 'get'
  })
}

/**
 * 添加职位
 * @param {Object} data - 职位数据
 * @returns {Promise}
 */
export function addPosition(data) {
  return request({
    url: '/employee/position',
    method: 'post',
    data
  })
}

/**
 * 修改职位
 * @param {Object} data - 职位数据
 * @returns {Promise}
 */
export function updatePosition(data) {
  return request({
    url: '/employee/position',
    method: 'put',
    data
  })
}

/**
 * 删除职位
 * @param {number} id - 职位 ID
 * @returns {Promise}
 */
export function deletePosition(id) {
  return request({
    url: `/employee/position/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除职位
 * @param {Array} ids - 职位 ID 数组
 * @returns {Promise}
 */
export function deletePositionBatch(ids) {
  return request({
    url: '/employee/position/batch',
    method: 'delete',
    data: ids
  })
}
