<template>
  <div class="reset-password-container">
    <div class="reset-password-card">
      <div class="reset-password-header">
        <h1 class="reset-password-title">重置密码</h1>
        <p class="reset-password-subtitle">请设置您的新密码</p>
      </div>
      
      <el-form
        ref="resetFormRef"
        :model="resetForm"
        :rules="resetRules"
        class="reset-password-form"
        @submit.prevent="handleResetPassword"
      >
        <el-form-item
          v-if="showOldPassword"
          prop="oldPassword"
        >
          <el-input
            v-model="resetForm.oldPassword"
            type="password"
            placeholder="旧密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="newPassword">
          <el-input
            v-model="resetForm.newPassword"
            type="password"
            placeholder="新密码（至少8位）"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="resetForm.confirmPassword"
            type="password"
            placeholder="确认新密码"
            size="large"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleResetPassword"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="reset-password-button"
            :loading="loading"
            @click="handleResetPassword"
          >
            {{ loading ? '重置中...' : '重置密码' }}
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
      
      <div v-if="successMessage" class="success-message">
        <el-alert
          :title="successMessage"
          type="success"
          :closable="false"
          show-icon
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';
import type { FormInstance, FormRules } from 'element-plus';

const router = useRouter();
const authStore = useAuthStore();

const resetFormRef = ref<FormInstance>();
const loading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

// 如果是首次登录（passwordResetRequired为true），不需要旧密码
const showOldPassword = computed(() => !authStore.needsPasswordReset);

const resetForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
});

// 验证确认密码是否一致
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== resetForm.newPassword) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const resetRules: FormRules = {
  oldPassword: showOldPassword.value
    ? [{ required: true, message: '请输入旧密码', trigger: 'blur' }]
    : [],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 100, message: '密码长度必须在8-100之间', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
};

const handleResetPassword = async () => {
  if (!resetFormRef.value) return;
  
  await resetFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      errorMessage.value = '';
      successMessage.value = '';
      
      try {
        const result = await authStore.resetPassword(
          showOldPassword.value ? resetForm.oldPassword : undefined,
          resetForm.newPassword,
          resetForm.confirmPassword
        );
        
        if (result.success) {
          successMessage.value = result.message || '密码重置成功';
          // 延迟跳转到首页
          setTimeout(() => {
            router.push('/');
          }, 1500);
        } else {
          errorMessage.value = result.message || '密码重置失败';
        }
      } catch (error: any) {
        errorMessage.value = error.message || '密码重置失败，请稍后重试';
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.reset-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.reset-password-card {
  width: 100%;
  max-width: 420px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.reset-password-header {
  text-align: center;
  margin-bottom: 32px;
}

.reset-password-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.reset-password-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.reset-password-form {
  margin-top: 32px;
}

.reset-password-button {
  width: 100%;
  margin-top: 8px;
}

.error-message,
.success-message {
  margin-top: 16px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
}
</style>

