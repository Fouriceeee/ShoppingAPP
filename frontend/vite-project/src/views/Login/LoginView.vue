<template>
  <div class="login-page-container">
    <div class="login-content">
      <div class="login-form-container">
        <div class="welcome-section">
          <h1>Welcome!</h1>
          <p>请登录您的账号以继续</p>
        </div>

        <el-form class="login-form" :model="loginForm" :rules="loginRules" ref="loginFormRef">
          <el-form-item prop="username">
            <el-input
                class="user-name-box"
              v-model="loginForm.username"
              placeholder="用户名"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe" class="remember-me">记住我</el-checkbox>
            <el-link type="primary">忘记密码？</el-link>
          </div>

          <el-button type="primary" class="login-button" @click="handleLogin" :loading="loading">登录</el-button>

          <div class="register-link">
            <span>还没有账号？</span>
            <el-link type="primary" @click="goToRegister">立即注册</el-link>
          </div>

          <div class="register-link admin-link">
            <span>管理员？</span>
            <el-link type="primary" @click="goToAdminLogin">管理员登录</el-link>
          </div>
        </el-form>
      </div>

      <div class="login-image-container">
        <img src="/src/assets/pictures/LoginImages/Login-image.jpeg" alt="登录页面插图" class="login-image" />
      </div>
    </div>
  </div>
</template>

<script setup>
//页面导航栏标题信息
document.title = '登录 - 易猫商城';

import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { login } from '@/utils/userService'

const router = useRouter()
const route = useRoute()
const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应在3到20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 使用userService进行登录
        const result = await login(loginForm.username, loginForm.password)
        ElMessage.success(result.message || '登录成功')

        // 登录成功后的重定向逻辑
        // 如果有重定向参数，则跳转到原来要访问的页面
        const redirectPath = route.query.redirect || '/'
        router.push(redirectPath)
      } catch (error) {
        ElMessage.error(error.message || '登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    } else {
      return false
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}

const goToAdminLogin = () => {
  router.push('/admin/login')
}
</script>

<style scoped>
.login-page-container {
/*  background-image: url('/src/assets/pictures/LoginImages/Login-background.svg');*/
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

.admin-link {
  margin-top: 8px;
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