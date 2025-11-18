import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '../store/auth'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('../views/ResetPassword.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 初始化认证状态
  if (!authStore.isAuthenticated) {
    authStore.init()
  }
  
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      // 未登录，跳转到登录页
      next({ name: 'Login', query: { redirect: to.fullPath } })
    } else if (authStore.needsPasswordReset && to.name !== 'ResetPassword') {
      // 需要重置密码，跳转到密码重置页
      next({ name: 'ResetPassword' })
    } else {
      next()
    }
  } else {
    // 公开路由
    if (to.name === 'Login' && authStore.isAuthenticated) {
      // 已登录用户访问登录页，跳转到首页
      if (authStore.needsPasswordReset) {
        next({ name: 'ResetPassword' })
      } else {
        next({ name: 'Home' })
      }
    } else {
      next()
    }
  }
})

export default router

