import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import * as AntDesignIconsVue from '@ant-design/icons-vue'

import App from './App.vue'
import router from './router'
import './styles/index.scss'
import './styles/common.css'

const app = createApp(App)

// 注册所有 Ant Design 图标
for (const [key, component] of Object.entries(AntDesignIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(Antd)

app.mount('#app')
