import request from '@/utils/request'

// 员工管理API
export const employeeApi = {
  // 获取员工列表
  getList(params) {
    return request({
      url: '/employees',
      method: 'get',
      params
    })
  },
  
  // 获取员工详情
  getById(id) {
    return request({
      url: `/employees/${id}`,
      method: 'get'
    })
  },
  
  // 根据工号获取员工
  getByEmpNo(empNo) {
    return request({
      url: `/employees/emp-no/${empNo}`,
      method: 'get'
    })
  },
  
  // 创建员工
  create(data) {
    return request({
      url: '/employees',
      method: 'post',
      data
    })
  },
  
  // 更新员工
  update(id, data) {
    return request({
      url: `/employees/${id}`,
      method: 'put',
      data
    })
  },
  
  // 删除员工
  delete(id) {
    return request({
      url: `/employees/${id}`,
      method: 'delete'
    })
  }
}