<template>
  <div class="admin-register-container">
    <top-nav />
    <div class="admin-register-content">
      <div class="admin-register-card">
        <div class="admin-register-header">
          <h2>管理员注册</h2>
          <p>创建新的管理员账号</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          label-position="top"
          @submit.prevent="handleRegister"
        >
          <el-form-item prop="username" label="管理员账号">
            <el-input 
              v-model="registerForm.username"
              placeholder="请输入管理员账号"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password" label="密码">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword" label="确认密码">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="name" label="管理员姓名">
            <el-input 
              v-model="registerForm.name"
              placeholder="请输入管理员姓名"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="role" label="管理员角色">
            <el-select v-model="registerForm.role" placeholder="请选择管理员角色" style="width: 100%">
              <el-option label="超级管理员" value="superadmin" />
              <el-option label="普通管理员" value="admin" />
              <el-option label="内容管理员" value="content" />
            </el-select>
          </el-form-item>

          <div class="admin-register-buttons">
            <el-button 
              type="primary" 
              native-type="submit" 
              :loading="isLoading"
              class="admin-register-button"
            >
              注册
            </el-button>
          </div>
        </el-form>

        <div class="admin-register-footer">
          <el-alert 
            v-if="registerError" 
            :title="registerError" 
            type="error" 
            show-icon 
            style="margin-bottom: 15px;"
          />

          <div class="login-link">
            <span>已有账号？</span>
            <el-link type="primary" @click="goToAdminLogin">返回登录</el-link>
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
import { addAdmin } from '@/utils/adminService';

// 设置页面标题
document.title = '管理员注册 - 易猫商城';

const router = useRouter();
const registerFormRef = ref(null);
const isLoading = ref(false);
const registerError = ref('');

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  role: 'admin',
  permissions: []
});

// 根据角色设置权限
const setPermissionsByRole = (role) => {
  switch(role) {
    case 'superadmin':
      return ['all'];
    case 'admin':
      return ['products', 'orders', 'users'];
    case 'content':
      return ['products'];
    default:
      return [];
  }
};

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  name: [
    { required: true, message: '请输入管理员姓名', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择管理员角色', trigger: 'change' }
  ]
};

// 管理员注册处理
const handleRegister = async () => {
  // 先清除之前的错误信息
  registerError.value = '';

  // 表单验证
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) {
      return false;
    }

    try {
      isLoading.value = true;

      // 设置权限
      registerForm.permissions = setPermissionsByRole(registerForm.role);

      // 提交注册
      const adminData = { ...registerForm };
      delete adminData.confirmPassword; // 删除确认密码字段

      // 调用添加管理员API
      const result = await addAdmin(adminData);

      ElMessage.success('管理员注册成功');

      // 注册成功后跳转到管理员登录页面
      router.push('/admin/login');
    } catch (error) {
      console.error('管理员注册失败:', error);
      registerError.value = error.message || '注册失败，请检查输入信息';
    } finally {
      isLoading.value = false;
    }
  });
};

const goToAdminLogin = () => {
  router.push('/admin/login');
};
</script>

<style scoped>
.admin-register-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.admin-register-content {
  display: flex;
  justify-content: center;
  padding-top: 60px;
  padding-bottom: 60px;
}

.admin-register-card {
  width: 420px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 40px;
}

.admin-register-header {
  text-align: center;
  margin-bottom: 30px;
}

.admin-register-header h2 {
  color: #333;
  font-size: 28px;
  margin-bottom: 10px;
}

.admin-register-header p {
  color: #666;
  font-size: 15px;
}

.admin-register-buttons {
  margin-top: 25px;
}

.admin-register-button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  background-color: #7852f5;
  border: none;
}

.admin-register-button:hover {
  background-color: #6a47d8;
}

.admin-register-footer {
  margin-top: 20px;
}

.login-link {
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
