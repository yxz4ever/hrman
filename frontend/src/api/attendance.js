import request from '@/utils/request'

// 考勤管理API
export const attendanceApi = {
  // 考勤规则
  rules: {
    // 获取所有规则
    getList() {
      return request({
        url: '/attendance/rules',
        method: 'get'
      })
    },
    
    // 获取规则详情
    getById(id) {
      return request({
        url: `/attendance/rules/${id}`,
        method: 'get'
      })
    },
    
    // 创建规则
    create(data) {
      return request({
        url: '/attendance/rules',
        method: 'post',
        data
      })
    },
    
    // 更新规则
    update(id, data) {
      return request({
        url: `/attendance/rules/${id}`,
        method: 'put',
        data
      })
    },
    
    // 删除规则
    delete(id) {
      return request({
        url: `/attendance/rules/${id}`,
        method: 'delete'
      })
    }
  },
  
  // 日考勤结果
  dailyResults: {
    // 计算日考勤
    calculate(params) {
      return request({
        url: '/attendance/daily-results/calculate',
        method: 'post',
        params
      })
    },
    
    // 获取日考勤结果
    get(params) {
      return request({
        url: '/attendance/daily-results',
        method: 'get',
        params
      })
    }
  }
}