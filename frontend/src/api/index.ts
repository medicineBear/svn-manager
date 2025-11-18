import axios, { AxiosError } from 'axios';
import router from '../router';

// 创建 axios 实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
});

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage获取token并添加到请求头
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  },
);

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data;
  },
  (error: AxiosError) => {
    // 统一错误处理
    if (error.response) {
      const status = error.response.status;
      const responseData = error.response.data as any;
      
      // 401未授权，清除token并跳转到登录页
      if (status === 401) {
        const errorMessage = responseData?.message || '登录已过期，请重新登录';
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        // 保存当前路径，登录后返回
        const currentPath = router.currentRoute.value.fullPath;
        if (currentPath !== '/login') {
          sessionStorage.setItem('redirectAfterLogin', currentPath);
        }
        router.push('/login');
        return Promise.reject(new Error(errorMessage));
      }
      
      // 403禁止访问
      if (status === 403) {
        const errorMessage = responseData?.message || '权限不足，无法访问';
        return Promise.reject(new Error(errorMessage));
      }
      
      // 429请求过于频繁
      if (status === 429) {
        const errorMessage = responseData?.message || '请求过于频繁，请稍后再试';
        return Promise.reject(new Error(errorMessage));
      }
      
      // 500服务器错误
      if (status >= 500) {
        const errorMessage = responseData?.message || '服务器错误，请稍后重试';
        return Promise.reject(new Error(errorMessage));
      }
      
      // 从响应中提取错误信息
      const errorMessage = responseData?.message || error.message || '请求失败';
      
      return Promise.reject(new Error(errorMessage));
    }
    
    // 网络错误或超时
    if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      return Promise.reject(new Error('请求超时，请检查网络连接后重试'));
    }
    
    if (error.code === 'ERR_NETWORK' || !error.response) {
      return Promise.reject(new Error('网络错误，请检查网络连接'));
    }
    
    // 开发环境记录详细错误，生产环境只记录简要信息
    if (import.meta.env.DEV) {
      console.error('API Error:', error);
    }
    
    return Promise.reject(new Error(error.message || '请求失败，请稍后重试'));
  },
);

export default api;
