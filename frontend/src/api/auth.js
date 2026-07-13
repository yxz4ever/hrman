import request from '@/utils/request'

/**
 * 认证相关 API
 */

/**
 * 登录
 * @param {Object} data - 登录数据
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 登出
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 * @returns {Promise}
 */
export function getCurrentUser() {
  return request({
    url: '/auth/current',
    method: 'get'
  })
}

/**
 * 获取用户列表
 * @returns {Promise}
 */
export function getUserList() {
  return request({
    url: '/system/user/list',
    method: 'get'
  })
}
