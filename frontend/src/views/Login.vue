<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1 class="login-title">SVN 出入库管理系统</h1>
        <p class="login-subtitle">
          {{ needsInitialization ? '首次使用，请创建管理员账户' : '请登录您的账户' }}
        </p>
        <el-alert
          v-if="needsInitialization"
          title="这是首次登录，您输入的用户名和密码将作为管理员账户创建"
          type="info"
          :closable="false"
          show-icon
          style="margin-top: 16px;"
        />
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            size="large"
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            size="large"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading || checkingInit"
            :disabled="checkingInit"
            @click="handleLogin"
          >
            {{ checkingInit ? '检查中...' : loading ? (needsInitialization ? '创建中...' : '登录中...') : (needsInitialization ? '创建管理员账户' : '登录') }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div v-if="errorMessage" class="error-message">
        <el-alert
          :title="errorMessage"
          type="error"
          :closable="false"
          show-icon
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';
import { checkInitialization } from '../api/auth';
import type { FormInstance, FormRules } from 'element-plus';

const router = useRouter();
const authStore = useAuthStore();

const loginFormRef = ref<FormInstance>();
const loading = ref(false);
const checkingInit = ref(true);
const needsInitialization = ref(false);
const errorMessage = ref('');

const loginForm = reactive({
  username: '',
  password: '',
});

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码长度至少8位', trigger: 'blur' },
  ],
};

// 检查是否需要初始化
onMounted(async () => {
  try {
    const response = await checkInitialization();
    if (response.code === 200) {
      needsInitialization.value = response.data === true;
    }
  } catch (error) {
    console.error('检查初始化状态失败:', error);
  } finally {
    checkingInit.value = false;
  }
});

const handleLogin = async () => {
  if (!loginFormRef.value) return;
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      errorMessage.value = '';
      
      try {
        const result = await authStore.login(loginForm.username, loginForm.password);
        
        if (!result.success) {
          errorMessage.value = result.message || (needsInitialization.value ? '创建管理员账户失败' : '登录失败');
        } else {
          // 登录成功后，更新初始化状态
          if (needsInitialization.value) {
            needsInitialization.value = false;
          }
        }
      } catch (error: any) {
        errorMessage.value = error.message || (needsInitialization.value ? '创建管理员账户失败，请稍后重试' : '登录失败，请稍后重试');
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-form {
  margin-top: 32px;
}

.login-button {
  width: 100%;
  margin-top: 8px;
}

.error-message {
  margin-top: 16px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
}
</style>

