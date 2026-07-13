<template>
  <div class="page-container fade-in">
    <div class="page-header">
      <h1 class="page-title">系统管理</h1>
      <p class="page-description">管理系统用户、角色、菜单等基础配置</p>
    </div>

    <div class="page-content">
      <!-- 快捷入口 -->
      <a-row :gutter="20" class="mb-20">
        <a-col :span="6">
          <a-card class="quick-card" @click="$router.push('/system/users')">
            <div class="quick-content">
              <div class="quick-icon">
                <UserOutlined :size="24" />
              </div>
              <div class="quick-info">
                <div class="quick-title">用户管理</div>
                <div class="quick-desc">管理系统用户</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card class="quick-card" @click="$router.push('/system/roles')">
            <div class="quick-content">
              <div class="quick-icon">
                <TeamOutlined :size="24" />
              </div>
              <div class="quick-info">
                <div class="quick-title">角色管理</div>
                <div class="quick-desc">管理系统角色</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card class="quick-card" @click="$router.push('/system/menus')">
            <div class="quick-content">
              <div class="quick-icon">
                <MenuOutlined :size="24" />
              </div>
              <div class="quick-info">
                <div class="quick-title">菜单管理</div>
                <div class="quick-desc">管理系统菜单</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card class="quick-card" @click="$router.push('/system/depts')">
            <div class="quick-content">
              <div class="quick-icon">
                <BankOutlined :size="24" />
              </div>
              <div class="quick-info">
                <div class="quick-title">部门管理</div>
                <div class="quick-desc">管理部门信息</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <a-row :gutter="20" class="mb-20">
        <a-col :span="6">
          <a-card class="quick-card" @click="$router.push('/system/posts')">
            <div class="quick-content">
              <div class="quick-icon">
                <BankOutlined :size="24" />
              </div>
              <div class="quick-info">
                <div class="quick-title">岗位管理</div>
                <div class="quick-desc">管理岗位信息</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card class="quick-card" @click="$router.push('/system/dicts')">
            <div class="quick-content">
              <div class="quick-icon">
                <BookOutlined :size="24" />
              </div>
              <div class="quick-info">
                <div class="quick-title">字典管理</div>
                <div class="quick-desc">管理数据字典</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card class="quick-card" @click="$router.push('/system/logs')">
            <div class="quick-content">
              <div class="quick-icon">
                <FileTextOutlined :size="24" />
              </div>
              <div class="quick-info">
                <div class="quick-title">操作日志</div>
                <div class="quick-desc">查看操作日志</div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :span="6">
          <a-card class="quick-card" @click="$router.push('/system/settings')">
            <div class="quick-content">
              <div class="quick-icon">
                <SettingOutlined :size="24" />
              </div>
              <div class="quick-info">
                <div class="quick-title">系统设置</div>
                <div class="quick-desc">系统参数配置</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 系统统计 -->
      <a-card>
        <template #title>
          <span class="card-title">系统统计</span>
        </template>

        <a-row :gutter="20">
          <a-col :span="6">
            <div class="stat-item">
              <div class="stat-icon" style="background: rgba(24, 144, 255, 0.1)">
                <UserOutlined :size="32" style="color: #1890ff" />
              </div>
              <div class="stat-info">
                <div class="stat-value">25</div>
                <div class="stat-label">系统用户</div>
              </div>
            </div>
          </a-col>

          <a-col :span="6">
            <div class="stat-item">
              <div class="stat-icon" style="background: rgba(82, 196, 26, 0.1)">
                <TeamOutlined :size="32" style="color: #52c41a" />
              </div>
              <div class="stat-info">
                <div class="stat-value">8</div>
                <div class="stat-label">系统角色</div>
              </div>
            </div>
          </a-col>

          <a-col :span="6">
            <div class="stat-item">
              <div class="stat-icon" style="background: rgba(250, 173, 20, 0.1)">
                <MenuOutlined :size="32" style="color: #faad14" />
              </div>
              <div class="stat-info">
                <div class="stat-value">45</div>
                <div class="stat-label">系统菜单</div>
              </div>
            </div>
          </a-col>

          <a-col :span="6">
            <div class="stat-item">
              <div class="stat-icon" style="background: rgba(255, 77, 79, 0.1)">
                <BankOutlined :size="32" style="color: #ff4d4f" />
              </div>
              <div class="stat-info">
                <div class="stat-value">12</div>
                <div class="stat-label">部门数量</div>
              </div>
            </div>
          </a-col>
        </a-row>
      </a-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  TeamOutlined,
  MenuOutlined,
  BankOutlined,
  BookOutlined,
  FileTextOutlined,
  SettingOutlined
} from '@ant-design/icons-vue'

onMounted(() => {
  message.success('系统管理模块加载完成')
})
</script>

<style scoped lang="scss">
.system-container {
  .quick-card {
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .quick-content {
      display: flex;
      align-items: center;
      gap: 16px;

      .quick-info {
        flex: 1;

        .quick-title {
          font-size: 16px;
          font-weight: 500;
          color: #262626;
          margin-bottom: 4px;
        }

        .quick-desc {
          font-size: 12px;
          color: #8c8c8c;
        }
      }
    }
  }

  .stat-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;

    .stat-icon {
      width: 64px;
      height: 64px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .stat-info {
      flex: 1;

      .stat-value {
        font-size: 28px;
        font-weight: 500;
        color: #262626;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 14px;
        color: #8c8c8c;
      }
    }
  }
}
</style>
