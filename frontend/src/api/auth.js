import request from '@/utils/request'
import { apiEndpoints } from '@/config/api.config'

// 认证API
export const authApi = {
  // 用户登录
  login(data) {
    return request({
      url: apiEndpoints.auth.login,
      method: 'post',
      data
    })
  },

  // 用户退出
  logout() {
    return request({
      url: apiEndpoints.auth.logout,
      method: 'post'
    })
  },

  // 获取当前用户信息
  getUserInfo() {
    return request({
      url: apiEndpoints.auth.getUserInfo,
      method: 'get'
    })
  }
}
