import { createRouter, createWebHistory } from 'vue-router'
import App from '../App.vue'
import WriteBlog from '../view/WriteBlog.vue'  // 引入写博客页面

// 配置路由
const routes = [
  {
    path: '/',
    name: 'Home',
    component: App  // 默认的主页面
  },
  {
    path: '/write-blog',
    name: 'WriteBlog',
    component: WriteBlog  // 写博客页面
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
