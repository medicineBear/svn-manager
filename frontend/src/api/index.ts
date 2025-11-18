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
      
      // 401未授权，清除token并跳转到登录页
      if (status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        router.push('/login');
      }
      
      // 从响应中提取错误信息
      const responseData = error.response.data as any;
      const errorMessage = responseData?.message || error.message || '请求失败';
      
      return Promise.reject(new Error(errorMessage));
    }
    
    console.error('API Error:', error);
    return Promise.reject(error);
  },
);

export default api;
