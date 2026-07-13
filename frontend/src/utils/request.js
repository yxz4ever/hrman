import axios from 'axios'
import { message } from 'ant-design-vue'
import router from '@/router'
import { apiConfig, httpStatus } from '@/config/api.config'
import { routeConfig } from '@/config/constants.config'

// 创建 axios 实例
const service = axios.create({
  baseURL: apiConfig.baseURL,
  timeout: apiConfig.timeout
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem(apiConfig.tokenKey)
    console.log('[Request Interceptor] Token:', token ? token.substring(0, 50) + '...' : 'null')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
      console.log('[Request Interceptor] Authorization header:', config.headers['Authorization'].substring(0, 60) + '...')
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 如果是 blob 类型的数据，直接返回，不进行 JSON 解析
    if (response.config.responseType === 'blob') {
      return response
    }

    const res = response.data

    // 如果返回的状态码不是 200，则判断为错误
    if (res.code !== httpStatus.SUCCESS) {
      message.error(res.message || '请求失败')

      // 401: 未授权，跳转到登录页
      if (res.code === httpStatus.UNAUTHORIZED) {
        localStorage.removeItem(apiConfig.tokenKey)
        router.push(routeConfig.loginPath)
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)

    if (error.response) {
      const { status, data } = error.response

      if (status === httpStatus.UNAUTHORIZED) {
        message.error('未授权，请重新登录')
        localStorage.removeItem(apiConfig.tokenKey)
        router.push(routeConfig.loginPath)
      } else if (status === httpStatus.FORBIDDEN) {
        message.error('拒绝访问')
      } else if (status === httpStatus.NOT_FOUND) {
        message.error('请求的资源不存在')
      } else if (status === httpStatus.SERVER_ERROR) {
        message.error('服务器内部错误')
      } else {
        message.error(data.message || '请求失败')
      }
    } else if (error.request) {
      message.error('网络错误，请检查网络连接')
    } else {
      message.error('请求失败')
    }

    return Promise.reject(error)
  }
)

export default service
