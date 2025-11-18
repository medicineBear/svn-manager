import api from './index';

/**
 * 登录请求
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * 登录响应
 */
export interface LoginResponse {
  token: string;
  user: {
    id: number;
    username: string;
    role: 'ADMIN' | 'USER';
    passwordResetRequired: boolean;
  };
}

/**
 * 密码重置请求
 */
export interface ResetPasswordRequest {
  oldPassword?: string;
  newPassword: string;
  confirmPassword: string;
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number;
  username: string;
  role: 'ADMIN' | 'USER';
  passwordResetRequired: boolean;
}

/**
 * API响应格式
 */
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

/**
 * 登录
 */
export function login(data: LoginRequest): Promise<ApiResponse<LoginResponse>> {
  return api.post('/auth/login', data);
}

/**
 * 密码重置
 */
export function resetPassword(data: ResetPasswordRequest): Promise<ApiResponse<null>> {
  return api.post('/auth/reset-password', data);
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser(): Promise<ApiResponse<UserInfo>> {
  return api.get('/auth/me');
}

/**
 * 登出
 */
export function logout(): Promise<ApiResponse<null>> {
  return api.post('/auth/logout');
}

/**
 * 检查是否需要初始化
 */
export function checkInitialization(): Promise<ApiResponse<boolean>> {
  return api.get('/auth/check-initialization');
}

