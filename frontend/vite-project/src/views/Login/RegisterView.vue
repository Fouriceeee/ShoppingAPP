<template>
  <div class="login-page-container">
    <div class="login-content">
      <div class="login-form-container">
        <div class="welcome-section">
          <h1>注册账号</h1>
          <p>创建您的账号以开始使用</p>
        </div>

        <el-form class="login-form" :model="registerForm" :rules="registerRules" ref="registerFormRef">
          <el-form-item prop="username">
            <el-input
                class="user-name-box"
              v-model="registerForm.username"
              placeholder="用户名"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="电子邮箱"
              prefix-icon="Message"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="确认密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="agreeTerms" class="remember-me">我同意服务条款和隐私政策</el-checkbox>
          </div>

          <el-button type="primary" class="login-button" @click="handleRegister" :loading="loading">注册</el-button>

          <div class="register-link">
            <span>已有账号？</span>
            <el-link type="primary" @click="goToLogin">立即登录</el-link>
          </div>
        </el-form>
      </div>

      <div class="login-image-container">
        <img src="/src/assets/pictures/LoginImages/Login-image.jpeg" alt="注册页面插图" class="login-image" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register } from '@/utils/userService'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)
const agreeTerms = ref(false)

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应在3到20个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入电子邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的电子邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  if (!agreeTerms.value) {
    ElMessage.warning('请同意服务条款和隐私政策')
    return
  }

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 使用userService进行注册
        await register(registerForm)
        ElMessage.success('注册成功')
        // 注册成功后重定向到登录页面
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '注册失败，请稍后再试')
      } finally {
        loading.value = false
      }
    } else {
      return false
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.login-page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: rgb(254, 240, 240);
  padding: 20px;
}

.login-content {
  display: flex;
  width: 80%;
  max-width: 1200px;
  height: 600px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  background-color: #070b0c;
}

.login-form-container {
  padding: 48px;
  margin: 15px 5px 15px 15px;
  background-color: #1b1d1e;
  flex: 0.35;
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 自定义输入框样式 */
.login-form :deep(.el-input__wrapper) {
  background-color: #191919; /* 设置输入框背景色 */
  border: 1px solid #202022; /* 设置边框颜色 */
  border-radius: 6px;
  box-shadow: none;
}


.welcome-section {
  margin-bottom: 40px;
  text-align: left;
}

.welcome-section h1 {
  font-size: 30px;
  font-weight: 600;
  color: #fdfcfc;
  margin-bottom: 20px;
  margin-top: 0;
  text-align: center;
}

.welcome-section p {
  font-size: 16px;
  color: #fdfcfc;
  margin: 0;
  text-align: center;
}

.login-form {
  width: 100%;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.login-button {
  width: 100%;
  height: 44px;
  background-color: #7852f5;
  border: none;
  font-size: 16px;
  font-weight: bold;
  border-radius: 4px;
  margin-bottom: 20px;
}

.register-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #fbfafa;
}

.remember-me {
  color: #aaaaaa;
}

.login-image-container {
  margin: 15px 15px 15px 5px;
  border-radius: 15px;
  flex: 0.65;
  background-color: #ffffff;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 768px) {
  .login-content {
    flex-direction: column;
    width: 95%;
    height: auto;
  }

  .login-image-container {
    display: none;
  }

  .login-form-container {
    padding: 32px 24px;
  }
}
</style>
