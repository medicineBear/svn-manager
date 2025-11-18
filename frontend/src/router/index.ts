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
      // 未登录，保存当前路径并跳转到登录页
      if (to.path !== '/login') {
        sessionStorage.setItem('redirectAfterLogin', to.fullPath)
      }
      next({ name: 'Login' })
    } else if (authStore.needsPasswordReset && to.name !== 'ResetPassword') {
      // 需要重置密码，跳转到密码重置页
      next({ name: 'ResetPassword' })
    } else {
      next()
    }
  } else {
    // 公开路由
    if (to.name === 'Login' && authStore.isAuthenticated) {
      // 已登录用户访问登录页，重定向到保存的路径或首页
      const redirectPath = sessionStorage.getItem('redirectAfterLogin') || '/'
      sessionStorage.removeItem('redirectAfterLogin')
      if (authStore.needsPasswordReset) {
        next({ name: 'ResetPassword' })
      } else {
        next(redirectPath)
      }
    } else {
      next()
    }
  }
})

export default router

