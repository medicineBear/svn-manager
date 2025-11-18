import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { login as loginApi, getCurrentUser, resetPassword as resetPasswordApi, type UserInfo } from '../api/auth';
import router from '../router';

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('token'));
  const user = ref<UserInfo | null>(() => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  });

  // 计算属性
  const isAuthenticated = computed(() => !!token.value && !!user.value);
  const isAdmin = computed(() => user.value?.role === 'ADMIN');
  const needsPasswordReset = computed(() => user.value?.passwordResetRequired === true);

  // Actions
  /**
   * 登录
   */
  async function login(username: string, password: string) {
    try {
      const response = await loginApi({ username, password });
      if (response.code === 200 && response.data) {
        token.value = response.data.token;
        user.value = response.data.user;
        
        // 保存到localStorage
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('user', JSON.stringify(response.data.user));
        
        // 如果需要重置密码，跳转到密码重置页
        if (response.data.user.passwordResetRequired) {
          router.push('/reset-password');
        } else {
          // 检查是否有保存的重定向路径
          const redirectPath = sessionStorage.getItem('redirectAfterLogin') || '/';
          sessionStorage.removeItem('redirectAfterLogin');
          router.push(redirectPath);
        }
        
        return { success: true };
      } else {
        return { success: false, message: response.message || '登录失败' };
      }
    } catch (error: any) {
      // 根据错误类型提供更具体的错误提示
      let errorMessage = '登录失败';
      if (error.message) {
        if (error.message.includes('锁定')) {
          errorMessage = error.message;
        } else if (error.message.includes('网络')) {
          errorMessage = '网络连接失败，请检查网络后重试';
        } else if (error.message.includes('超时')) {
          errorMessage = '请求超时，请稍后重试';
        } else {
          errorMessage = error.message;
        }
      }
      return { success: false, message: errorMessage };
    }
  }

  /**
   * 登出
   */
  function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    router.push('/login');
  }

  /**
   * 初始化（从localStorage恢复状态）
   */
  async function init() {
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');
    
    if (savedToken && savedUser) {
      token.value = savedToken;
      user.value = JSON.parse(savedUser);
      
      // 验证token是否有效
      try {
        await getCurrentUser();
      } catch (error) {
        // token无效，清除状态
        logout();
      }
    }
  }

  /**
   * 重置密码
   */
  async function resetPassword(oldPassword: string | undefined, newPassword: string, confirmPassword: string) {
    try {
      const response = await resetPasswordApi({
        oldPassword,
        newPassword,
        confirmPassword,
      });
      
      if (response.code === 200) {
        // 更新用户信息
        if (user.value) {
          user.value.passwordResetRequired = false;
          localStorage.setItem('user', JSON.stringify(user.value));
        }
        
        return { success: true, message: response.message || '密码重置成功' };
      } else {
        return { success: false, message: response.message || '密码重置失败' };
      }
    } catch (error: any) {
      // 根据错误类型提供更具体的错误提示
      let errorMessage = '密码重置失败';
      if (error.message) {
        if (error.message.includes('频繁')) {
          errorMessage = error.message;
        } else if (error.message.includes('不符合要求')) {
          errorMessage = error.message;
        } else if (error.message.includes('网络')) {
          errorMessage = '网络连接失败，请检查网络后重试';
        } else if (error.message.includes('超时')) {
          errorMessage = '请求超时，请稍后重试';
        } else {
          errorMessage = error.message;
        }
      }
      return { success: false, message: errorMessage };
    }
  }

  /**
   * 更新用户信息
   */
  async function refreshUserInfo() {
    try {
      const response = await getCurrentUser();
      if (response.code === 200 && response.data) {
        user.value = response.data;
        localStorage.setItem('user', JSON.stringify(response.data));
      }
    } catch (error) {
      // 开发环境记录错误，生产环境静默处理
      if (import.meta.env.DEV) {
        console.error('获取用户信息失败:', error);
      }
    }
  }

  return {
    // 状态
    token,
    user,
    // 计算属性
    isAuthenticated,
    isAdmin,
    needsPasswordReset,
    // Actions
    login,
    logout,
    init,
    resetPassword,
    refreshUserInfo,
  };
});

