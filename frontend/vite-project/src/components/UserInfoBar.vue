<template>
  <div class="user-info-bar-wrapper">
    <div class="user-profile" @click="goToUserPage">
      <div class="avatar-container">
        <!-- 用户头像，如果用户没有头像则显示默认头像 -->
        <img
          :src="userInfo.avatar || '/src/assets/pictures/loginImages/default-avatar.png'"
          alt="用户头像"
          class="avatar-image"
        />
      </div>
      <div class="username">{{ userInfo.username }}</div>
    </div>

    <div class="user-benefits">
      <div class="benefit-item" @click="goToCart" style="cursor: pointer">
        <el-badge :value="cartItemCount || 0" :max="999" class="badge-item">
          <div class="benefit-icon cart-icon">
            <el-icon><ShoppingCart /></el-icon>
          </div>
        </el-badge>
        <div class="benefit-name">购物车</div>
      </div>

      <div class="benefit-item" @click="goToCoupons" style="cursor: pointer">
        <el-badge :value="couponsCount || 0" :max="99" class="badge-item">
          <div class="benefit-icon">
            <el-icon><Ticket /></el-icon>
          </div>
        </el-badge>
        <div class="benefit-name">优惠券</div>
      </div>

      <div class="benefit-item" @click="goToFavorites" style="cursor: pointer">
        <el-badge :value="favoritesCount || 0" :max="99" class="badge-item">
          <div class="benefit-icon">
            <el-icon><star /></el-icon>
          </div>
        </el-badge>
        <div class="benefit-name">我的收藏</div>
      </div>
    </div>

    <div class="user-actions">
      <el-button type="primary" class="action-button order-button" @click="goToOrders">
        我的订单
      </el-button>

      <el-button plain class="action-button logout-button" @click="handleLogout">
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {ShoppingCart, Ticket, Present, Star} from '@element-plus/icons-vue'
import { checkAuth, userLogout } from '@/utils/userService'
import {getCartItems} from "@/api/cart.js";

const router = useRouter()
const userInfo = ref({})
const cartItems = ref([])
const couponsCount = ref(3)
const favoritesCount = ref(14) // 示例数据，实际应从API获取

// 计算购物车中的商品总数量
const cartItemCount = computed(() => {
  if (!cartItems.value || cartItems.value.length === 0) return 0;
  return cartItems.value.reduce((total, item) => total + item.quantity, 0)
})

// 加载用户数据
const loadUserData = async () => {
  const user = checkAuth()
  if (user) {
    userInfo.value = user
    await loadCartData() // 使用await确保正确加载购物车数据
  } else {
    // 用户未登录，重定向到登录页面
    ElMessage.warning('请先登录')
    router.push('/login')
  }
}

// 加载购物车数据
const loadCartData = async () => {
  try {
    const response = await getCartItems();
    if (response.data) {
      cartItems.value = response.data;
      console.log('从API获取购物车数据成功:', cartItems.value);
    }
  } catch (error) {
    console.error('获取购物车数据失败:', error);
    cartItems.value = [];
  }
}

// 前往订单页面
const goToOrders = () => {
  router.push('/orders')
}

// 前往购物车页面
const goToCart = () => {
  router.push('/cart')
}

// 前往用户中心页面
const goToUserPage = () => {
  router.push('/user')
}

// 前往优惠券页面
const goToCoupons = () => {
  router.push({
    path: '/user',
    query: { activeTab: 'coupons' }
  })
}

// 前往收藏页面
const goToFavorites = () => {
  router.push({
    path: '/user',
    query: { activeTab: 'favorites' }
  })
}


// 处理退出登录
const handleLogout = () => {
  userLogout()
  ElMessage.success('已成功退出登录')
  router.push('/').then(() => {
    // 导航完成后刷新页面
    window.location.reload()
  })

}

onMounted(async () => {
  await loadUserData()
})
</script>

<style scoped>
.user-info-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  height: 400px;
  padding: 30px 20px;
  border-radius: 12px;
  background-color: #ebecf0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  width: 220px;
  box-sizing: border-box;
}

.user-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  cursor: pointer;
}

.avatar-container {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #7852f5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.username {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-top: 5px;
}

.user-benefits {
  display: flex;
  justify-content: space-around;
  width: 100%;
  margin: 20px 0;
}

.benefit-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.benefit-icon {
  font-size: 24px;
  color: #7852f5;
  padding: 10px;
  border-radius: 8px;
  background-color: rgba(120, 82, 245, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-item :deep(.el-badge__content) {
  background-color: rgba(253, 17, 17, 0.73);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border: none;
}

.benefit-name {
  font-size: 12px;
  color: #666;
}

.user-actions {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-button {
  width: 100%;
  border-radius: 8px;
  font-weight: 500;
  margin: 0;
}

.action-button:first-child {
  background-color: #7852f5;
  border: none;
}

.action-button:last-child {
  margin: 0;
}

</style>
