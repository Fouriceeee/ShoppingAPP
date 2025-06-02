<template>
  <div class="login-bar-wrapper">
    <div class="profile">
      <svg xmlns="http://www.w3.org/2000/svg" width="60" height="60" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-user"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
    </div>
    <div class="greeting">欢迎回来！</div>
    <div class="tips">登录以继续您的精彩旅程</div>
    <div class="button-container login-button">
      <el-button type="primary" size="large" class="action-el-button">登录</el-button>
    </div>
    <div class="button-container signup-button">
      <el-button plain size="large" class="action-el-button">注册新账号</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginModule'

}
</script>

<style scoped>
.login-bar-wrapper {
  display: flex;
  flex-direction: column; /* 子元素自上而下垂直排列 */
  align-items: center;   /* 子元素在交叉轴（本例中是水平方向）上居中 */
  justify-content: center; /* 子元素在主轴（本例中是垂直方向）上居中 */
  height: 400px;
  padding: 30px 20px;
  border-radius: 12px;
  background-color: #ebecf0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  width: 220px; /* 设定一个宽度 */
  gap: 18px; /* 设置子元素之间的垂直间距，比分别设置 margin 更简洁 */
  box-sizing: border-box;
}

.profile {
  color: #555;
}

.profile svg {
  display: block;
}

.greeting {
  font-size: 20px;
  font-weight: bold;
  color: #333333;
  text-align: center;
}

.tips {
  font-size: 14px;
  color: #777777;
  text-align: center;
  max-width: 90%;
}

.button-container {
  width: 100%; /* 让按钮容器撑满父级内容宽度 */
  max-width: 240px; /* 限制按钮容器的最大宽度，可以根据设计调整 */
  display: flex; /* 使用 flex 来控制内部 el-button 的行为 */
}

.action-el-button {
  width: 100%;
  font-weight: 500;
  border: none;
}

</style>