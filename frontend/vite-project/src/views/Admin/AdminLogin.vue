<template>
  <div class="admin-login-container">
    <top-nav />
    <div class="admin-login-content">
      <div class="admin-login-card">
        <div class="admin-login-header">
          <h2>管理员登录</h2>
          <p>请输入您的管理员账号和密码</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          label-position="top"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username" label="管理员账号">
            <el-input 
              v-model="loginForm.username"
              placeholder="请输入管理员账号"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password" label="密码">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <div class="admin-login-buttons">
            <el-button 
              type="primary" 
              native-type="submit" 
              :loading="isLoading"
              class="admin-login-button"
            >
              登录
            </el-button>
          </div>
        </el-form>

        <div class="admin-login-footer">
          <el-alert v-if="loginError" :title="loginError" type="error" show-icon style="margin-bottom: 15px;" />

          <div class="register-link">
            <span>需要创建管理员账号？</span>
            <el-link type="primary" @click="goToRegister">管理员注册</el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import topNav from '@/components/topNav.vue';
import { adminLogin } from '@/utils/adminService';

// 设置页面标题
document.title = '管理员登录 - 易猫商城';

const router = useRouter();
const loginFormRef = ref(null);
const isLoading = ref(false);
const loginError = ref('');

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
});

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
  ]
};

// 跳转到管理员注册页面
const goToRegister = () => {
  router.push('/admin/register');
};

// 管理员登录处理
const handleLogin = async () => {
  // 先清除之前的错误信息
  loginError.value = '';

  // 表单验证
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) {
      return false;
    }

    try {
      isLoading.value = true;

      // 调用管理员登录API
      const result = await adminLogin(loginForm.username, loginForm.password);

      ElMessage.success('管理员登录成功');

      // 登录成功后跳转到管理后台首页
      router.push('/admin/dashboard');
    } catch (error) {
      console.error('管理员登录失败:', error);
      loginError.value = error.message || '登录失败，请检查您的账号和密码';
    } finally {
      isLoading.value = false;
    }
  });
};
</script>

<style scoped>
.admin-login-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.admin-login-content {
  display: flex;
  justify-content: center;
  padding-top: 80px;
  padding-bottom: 80px;
}

.admin-login-card {
  width: 420px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 40px;
}

.admin-login-header {
  text-align: center;
  margin-bottom: 30px;
}

.admin-login-header h2 {
  color: #333;
  font-size: 28px;
  margin-bottom: 10px;
}

.admin-login-header p {
  color: #666;
  font-size: 15px;
}

.admin-login-buttons {
  margin-top: 25px;
}

.admin-login-button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  background-color: #7852f5;
  border: none;
}

.admin-login-button:hover {
  background-color: #6a47d8;
}

.admin-login-footer {
  margin-top: 20px;
}

.register-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #666;
}

:deep(.el-input__wrapper) {
  padding: 4px 11px;
}

:deep(.el-input__inner) {
  height: 38px;
}
</style>
