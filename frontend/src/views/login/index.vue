<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <UserOutlined :style="{ fontSize: '48px', color: '#1890ff' }" />
        <h1 class="login-title">人力资源管理系统</h1>
        <p class="login-subtitle">Human Resource Management System</p>
      </div>

      <a-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <a-form-item name="username">
          <a-input
            v-model:value="loginForm.username"
            placeholder="请输入用户名"
            size="large"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="password">
          <a-input-password
            v-model:value="loginForm.password"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item>
          <a-checkbox v-model:checked="loginForm.rememberMe">记住我</a-checkbox>
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            size="large"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </a-button>
        </a-form-item>
      </a-form>

      <div class="login-footer">
        <p>默认账号：admin / 密码：admin123</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { authApi } from '@/api/auth'
import { apiConfig } from '@/config/api.config'
import { userConfig } from '@/config/constants.config'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: userConfig.defaultUsername,
  password: userConfig.defaultPassword,
  rememberMe: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loading.value = true
    try {
      const res = await authApi.login({
        username: loginForm.username,
        password: loginForm.password
      })

      console.log('登录响应完整数据:', res)
      console.log('登录响应 data:', res.data)
      console.log('登录响应 data.token:', res.data?.token)

      if (res.code === 200 && res.data && res.data.token) {
        // 保存 Token
        localStorage.setItem(apiConfig.tokenKey, res.data.token)
        console.log('Token 已保存:', res.data.token.substring(0, 50) + '...')

        // 保存用户信息
        if (loginForm.rememberMe) {
          localStorage.setItem(apiConfig.userInfoKey, JSON.stringify(res.data.user))
        }

        message.success('登录成功')

        // 延迟跳转，确保 token 已保存
        setTimeout(() => {
          router.push('/').catch(err => {
            console.error('路由跳转失败:', err)
            // 如果跳转失败，强制刷新页面
            window.location.href = '/'
          })
        }, 100)
      } else {
        console.error('登录失败，响应数据:', res)
        message.error(res.message || '登录失败')
      }
    } catch (error) {
      console.error('登录失败:', error)
      message.error('登录失败，请检查网络连接')
    } finally {
      loading.value = false
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  .login-title {
    font-size: 24px;
    font-weight: 500;
    color: #262626;
    margin: 16px 0 8px 0;
  }

  .login-subtitle {
    font-size: 14px;
    color: #8c8c8c;
    margin: 0;
  }
}

.login-form {
  .ant-form-item {
    margin-bottom: 24px;
  }
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;

  p {
    font-size: 12px;
    color: #8c8c8c;
    margin: 0;
  }
}
</style>
