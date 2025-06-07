<template>
  <div class="user-page">
    <top-nav />

    <div class="user-page-container">
      <!-- 侧边栏导航 -->
      <div class="sidebar">
        <div class="user-profile">
          <div class="avatar-container">
            <img
              :src="userInfo.avatar || '/src/assets/pictures/loginImages/default-avatar.png'"
              alt="用户头像"
              class="avatar-image"
            />
            <div class="update-avatar">
              <el-icon><Camera /></el-icon>
            </div>
          </div>
          <div class="username">{{ userInfo.username }}</div>
          <div class="user-level">
            <el-tag size="small" type="warning" effect="plain">普通会员</el-tag>
          </div>
        </div>

        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="account">
            <el-icon><User /></el-icon>
            <span>账号信息</span>
          </el-menu-item>
          <el-menu-item index="orders">
            <el-icon><Tickets /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="cart">
            <el-icon><ShoppingCart /></el-icon>
            <span>我的购物车</span>
            <el-badge :value="cartItemCount || 0" :max="999" class="menu-badge" />
          </el-menu-item>
          <el-menu-item index="coupons">
            <el-icon><Ticket /></el-icon>
            <span>优惠券</span>
            <el-badge :value="couponsCount || 0" :max="99" class="menu-badge" />
          </el-menu-item>
          <el-menu-item index="favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="addresses">
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
          </el-menu-item>
          <el-menu-item index="settings">
            <el-icon><Setting /></el-icon>
            <span>账号设置</span>
          </el-menu-item>
        </el-menu>

        <div class="logout-action">
          <el-button type="danger" plain @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </div>

      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 账号信息 -->
        <div v-if="activeMenu === 'account'" class="content-section">
          <div class="section-header">
            <h2>账号信息</h2>
          </div>

          <el-card class="account-info-card">
            <el-form :model="userInfo" label-width="100px">
              <el-form-item label="用户名">
                <el-input v-model="userInfo.username" disabled />
              </el-form-item>
              <el-form-item label="电子邮箱">
                <el-input v-model="userInfo.email" />
              </el-form-item>
              <el-form-item label="手机号码">
                <el-input v-model="userInfo.phone" placeholder="请添加手机号码" />
              </el-form-item>
              <el-form-item label="注册时间">
                <el-input :value="formatDate(userInfo.createdAt)" disabled />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="saveUserInfo">保存修改</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>

        <!-- 我的订单 -->
        <div v-if="activeMenu === 'orders'" class="content-section">
          <div class="section-header">
            <h2>我的订单</h2>
            <div class="order-tabs">
              <el-tabs v-model="orderTabActive">
                <el-tab-pane label="全部订单" name="all" />
                <el-tab-pane label="待付款" name="unpaid" />
                <el-tab-pane label="待发货" name="unshipped" />
                <el-tab-pane label="待收货" name="shipped" />
                <el-tab-pane label="已完成" name="completed" />
              </el-tabs>
            </div>
          </div>

          <div v-if="orders.length === 0" class="empty-data">
            <el-empty description="暂无订单数据" />
            <el-button type="primary" @click="goShopping">去购物</el-button>
          </div>

          <div v-else class="orders-list">
            <!-- 这里将来显示订单列表 -->
            <el-card v-for="i in 3" :key="i" class="order-card">
              <div class="order-header">
                <div class="order-id">订单号: 202506070{{ i }}</div>
                <div class="order-date">2025-06-0{{ i }}</div>
                <div class="order-status">待付款</div>
              </div>
              <div class="order-products">
                <div class="product-item">
                  <img src="/src/assets/pictures/products/default-product.png" class="product-img" />
                  <div class="product-info">
                    <div class="product-title">高性能主板 X570</div>
                    <div class="product-price">¥1299.00 × 1</div>
                  </div>
                </div>
              </div>
              <div class="order-footer">
                <div class="order-total">总计: ¥1299.00</div>
                <div class="order-actions">
                  <el-button type="primary" size="small">付款</el-button>
                  <el-button plain size="small">取消订单</el-button>
                </div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 我的优惠券 -->
        <div v-if="activeMenu === 'coupons'" class="content-section">
          <div class="section-header">
            <h2>我的优惠券</h2>
          </div>

          <div class="coupons-container">
            <el-tabs v-model="couponTabActive">
              <el-tab-pane label="未使用" name="unused">
                <div class="coupons-list">
                  <div v-for="i in 3" :key="i" class="coupon-item">
                    <div class="coupon-value">¥{{ 10 * i }}</div>
                    <div class="coupon-info">
                      <div class="coupon-title">新人专享券</div>
                      <div class="coupon-validity">有效期至: 2025-12-31</div>
                      <div class="coupon-condition">满{{ 100 * i }}可用</div>
                    </div>
                    <div class="coupon-use">
                      <el-button type="primary" size="small" round>去使用</el-button>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="已使用" name="used">
                <el-empty description="暂无已使用的优惠券" />
              </el-tab-pane>
              <el-tab-pane label="已过期" name="expired">
                <el-empty description="暂无已过期的优惠券" />
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>

        <!-- 我的收藏 -->
        <div v-if="activeMenu === 'favorites'" class="content-section">
          <div class="section-header">
            <h2>我的收藏</h2>
          </div>

          <div v-if="favorites.length === 0" class="empty-data">
            <el-empty description="暂无收藏商品" />
            <el-button type="primary" @click="goShopping">去购物</el-button>
          </div>

          <div v-else class="favorites-grid">
            <el-card v-for="i in 4" :key="i" class="favorite-item">
              <img src="/src/assets/pictures/products/default-product.png" class="favorite-img" />
              <div class="favorite-info">
                <div class="favorite-title">高性能CPU {{ i }}</div>
                <div class="favorite-price">¥{{ 899 + i * 100 }}</div>
              </div>
              <div class="favorite-actions">
                <el-button type="primary" size="small" @click="addToCart">加入购物车</el-button>
                <el-button type="danger" icon="Delete" circle plain size="small" @click="removeFromFavorites" />
              </div>
            </el-card>
          </div>
        </div>

        <!-- 收货地址 -->
        <div v-if="activeMenu === 'addresses'" class="content-section">
          <div class="section-header">
            <h2>收货地址</h2>
            <el-button type="primary" @click="showAddressDialog = true">新增地址</el-button>
          </div>

          <div v-if="addresses.length === 0" class="empty-data">
            <el-empty description="暂无收货地址" />
          </div>

          <div v-else class="addresses-list">
            <el-card v-for="(address, index) in addresses" :key="index" class="address-card">
              <div class="address-info">
                <div class="address-name-phone">
                  <span class="address-name">{{ address.name }}</span>
                  <span class="address-phone">{{ address.phone }}</span>
                  <el-tag v-if="address.isDefault" size="small" type="success">默认</el-tag>
                </div>
                <div class="address-detail">
                  {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}
                </div>
              </div>
              <div class="address-actions">
                <el-button type="text" @click="editAddress(index)">编辑</el-button>
                <el-button type="text" @click="deleteAddressConfirm(index)">删除</el-button>
                <el-button v-if="!address.isDefault" type="text" @click="setDefaultAddress(index)">设为默认</el-button>
              </div>
            </el-card>
          </div>

          <!-- 新增/编辑地址对话框 -->
          <el-dialog
            v-model="showAddressDialog"
            :title="isEditingAddress ? '编辑地址' : '新增地址'"
            width="500px"
          >
            <el-form :model="currentAddress" label-width="80px">
              <el-form-item label="收货人">
                <el-input v-model="currentAddress.name" placeholder="请输入收货人姓名" />
              </el-form-item>
              <el-form-item label="手机号码">
                <el-input v-model="currentAddress.phone" placeholder="请输入手机号码" />
              </el-form-item>
              <el-form-item label="所在地区">
                <div class="region-select">
                  <el-select v-model="currentAddress.province" placeholder="省">
                    <el-option label="广东省" value="广东省" />
                    <el-option label="北京市" value="北京市" />
                    <!-- 更多选项 -->
                  </el-select>
                  <el-select v-model="currentAddress.city" placeholder="市">
                    <el-option label="深圳市" value="深圳市" />
                    <el-option label="广州市" value="广州市" />
                    <!-- 更多选项 -->
                  </el-select>
                  <el-select v-model="currentAddress.district" placeholder="区/县">
                    <el-option label="南山区" value="南山区" />
                    <el-option label="福田区" value="福田区" />
                    <!-- 更多选项 -->
                  </el-select>
                </div>
              </el-form-item>
              <el-form-item label="详细地址">
                <el-input
                  v-model="currentAddress.detailAddress"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入详细地址"
                />
              </el-form-item>
              <el-form-item>
                <el-checkbox v-model="currentAddress.isDefault">设为默认地址</el-checkbox>
              </el-form-item>
            </el-form>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="showAddressDialog = false">取消</el-button>
                <el-button type="primary" @click="saveAddress">保存</el-button>
              </span>
            </template>
          </el-dialog>
        </div>

        <!-- 账号设置 -->
        <div v-if="activeMenu === 'settings'" class="content-section">
          <div class="section-header">
            <h2>账号设置</h2>
          </div>

          <el-tabs v-model="settingsTabActive" class="settings-tabs">
            <el-tab-pane label="密码修改" name="password">
              <el-card class="settings-card">
                <el-form :model="passwordForm" label-width="100px">
                  <el-form-item label="当前密码">
                    <el-input
                      v-model="passwordForm.currentPassword"
                      type="password"
                      placeholder="请输入当前密码"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item label="新密码">
                    <el-input
                      v-model="passwordForm.newPassword"
                      type="password"
                      placeholder="请输入新密码"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item label="确认新密码">
                    <el-input
                      v-model="passwordForm.confirmPassword"
                      type="password"
                      placeholder="请再次输入新密码"
                      show-password
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="changePassword">修改密码</el-button>
                  </el-form-item>
                </el-form>
              </el-card>
            </el-tab-pane>
            <el-tab-pane label="隐私设置" name="privacy">
              <el-card class="settings-card">
                <el-form label-width="200px">
                  <el-form-item label="接收促销信息">
                    <el-switch v-model="privacySettings.receivePromotions" />
                  </el-form-item>
                  <el-form-item label="允许推荐商品">
                    <el-switch v-model="privacySettings.allowRecommendations" />
                  </el-form-item>
                  <el-form-item label="记住浏览历史">
                    <el-switch v-model="privacySettings.saveHistory" />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="savePrivacySettings">保存设置</el-button>
                  </el-form-item>
                </el-form>
              </el-card>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 页面标题
document.title = '个人中心 - 易猫商城'

import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import topNav from '@/components/topNav.vue'
import {
  User, Setting, ShoppingCart, Tickets, Ticket, Star, 
  Location, Camera, SwitchButton, Delete
} from '@element-plus/icons-vue'
import { checkAuth, userLogout } from '@/utils/userService'
import { getCartItems } from '@/api/cart'

const router = useRouter()
const route = useRoute()
const activeMenu = ref('account')
const userInfo = ref({})
const cartItems = ref([])
const couponsCount = ref(3) // 示例数据
const favorites = ref([]) // 示例数据
const orders = ref([]) // 示例数据

// 订单标签页
const orderTabActive = ref('all')

// 优惠券标签页
const couponTabActive = ref('unused')

// 设置标签页
const settingsTabActive = ref('password')

// 地址管理
const addresses = ref([
  {
    name: '张三',
    phone: '13812345678',
    province: '广东省',
    city: '深圳市',
    district: '南山区',
    detailAddress: '科技园路123号',
    isDefault: true
  },
  {
    name: '李四',
    phone: '13987654321',
    province: '北京市',
    city: '北京市',
    district: '海淀区',
    detailAddress: '中关村大街456号',
    isDefault: false
  }
])
const showAddressDialog = ref(false)
const isEditingAddress = ref(false)
const currentAddress = ref({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})
const editingAddressIndex = ref(-1)

// 密码修改表单
const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 隐私设置
const privacySettings = ref({
  receivePromotions: true,
  allowRecommendations: true,
  saveHistory: true
})

// 计算购物车中的商品总数量
const cartItemCount = computed(() => {
  if (!cartItems.value || cartItems.value.length === 0) return 0
  return cartItems.value.reduce((total, item) => total + item.quantity, 0)
})

// 菜单选择处理
const handleMenuSelect = (key) => {
  activeMenu.value = key

  // 如果选择了购物车，直接跳转到购物车页面
  if (key === 'cart') {
    router.push('/cart')
  }
}

// 加载用户数据
const loadUserData = async () => {
  const user = checkAuth()
  if (user) {
    userInfo.value = user
    await loadCartData() // 加载购物车数据
    // 此处可添加加载其他数据的方法
  } else {
    ElMessage.warning('请先登录')
    router.push('/login')
  }
}

// 加载购物车数据
const loadCartData = async () => {
  try {
    const response = await getCartItems()
    if (response.data) {
      cartItems.value = response.data
    }
  } catch (error) {
    console.error('获取购物车数据失败:', error)
    cartItems.value = []
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 保存用户信息
const saveUserInfo = () => {
  // 此处应调用API更新用户信息
  ElMessage.success('用户信息已保存')
}

// 前往购物页面
const goShopping = () => {
  router.push('/products')
}

// 添加到购物车
const addToCart = () => {
  ElMessage.success('已添加到购物车')
}

// 从收藏中移除
const removeFromFavorites = () => {
  ElMessage.success('已从收藏中移除')
}

// 编辑地址
const editAddress = (index) => {
  isEditingAddress.value = true
  editingAddressIndex.value = index
  currentAddress.value = { ...addresses.value[index] }
  showAddressDialog.value = true
}

// 删除地址确认
const deleteAddressConfirm = (index) => {
  ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    addresses.value.splice(index, 1)
    ElMessage.success('地址已删除')
  }).catch(() => {
    // 取消删除
  })
}

// 设为默认地址
const setDefaultAddress = (index) => {
  addresses.value.forEach(address => address.isDefault = false)
  addresses.value[index].isDefault = true
  ElMessage.success('已设为默认地址')
}

// 保存地址
const saveAddress = () => {
  if (!currentAddress.value.name || !currentAddress.value.phone || !currentAddress.value.detailAddress) {
    ElMessage.warning('请填写完整的地址信息')
    return
  }

  if (isEditingAddress.value) {
    // 编辑现有地址
    addresses.value[editingAddressIndex.value] = { ...currentAddress.value }
  } else {
    // 添加新地址
    if (currentAddress.value.isDefault) {
      // 如果新地址设为默认，需要将其他地址设为非默认
      addresses.value.forEach(address => address.isDefault = false)
    }
    addresses.value.push({ ...currentAddress.value })
  }

  showAddressDialog.value = false
  ElMessage.success(isEditingAddress.value ? '地址已更新' : '地址已添加')

  // 重置表单
  resetAddressForm()
}

// 重置地址表单
const resetAddressForm = () => {
  currentAddress.value = {
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  }
  isEditingAddress.value = false
  editingAddressIndex.value = -1
}

// 修改密码
const changePassword = () => {
  // 这里应该有密码验证逻辑和API调用
  if (!passwordForm.value.currentPassword || 
      !passwordForm.value.newPassword || 
      !passwordForm.value.confirmPassword) {
    ElMessage.warning('请填写完整的密码信息')
    return
  }

  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }

  // 模拟API调用
  ElMessage.success('密码修改成功')

  // 重置表单
  passwordForm.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

// 保存隐私设置
const savePrivacySettings = () => {
  // 这里应该有API调用来保存设置
  ElMessage.success('隐私设置已保存')
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userLogout()
    ElMessage.success('已成功退出登录')
    router.push('/')
  }).catch(() => {
    // 用户取消操作
  })
}

onMounted(async () => {
  await loadUserData()

  // 检查URL中是否有activeTab参数，如果有则切换到对应的标签页
  if (route.query.activeTab) {
    activeMenu.value = route.query.activeTab
  }
})
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.user-page-container {
  display: flex;
  max-width: 1200px;
  margin: 20px auto;
  gap: 20px;
  padding: 0 20px;
}

/* 侧边栏样式 */
.sidebar {
  width: 240px;
  margin-top: 30px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.user-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0;
  background: linear-gradient(to top, rgba(157, 105, 255, 0.63), rgba(127, 184, 255, 0.6));
  color: white;
}

.avatar-container {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  position: relative;
  cursor: pointer;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.update-avatar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  text-align: center;
  padding: 3px 0;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-container:hover .update-avatar {
  opacity: 1;
}

.username {
  font-size: 20px;
  font-weight: bold;
  margin-top: 15px;
  color: white;
}

.user-level {
  margin-top: 5px;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
}

.sidebar-menu :deep(.el-menu-item) {
  display: flex;
  align-items: center;
  font-size: 15px;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: rgba(120, 82, 245, 0.1);
  color: #7852f5;
  font-weight: bold;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: rgba(120, 82, 245, 0.05);
}


.menu-badge {
  display: grid;
  place-items: center;
  margin-left: 10px;
}

.menu-badge :deep(.el-badge__content) {
  background-color: #ed115d;
  right: -15px;
}

.logout-action {
  padding: 15px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}

/* 主内容区样式 */
.main-content {
  margin-top: 30px;
  flex: 1;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  min-height: 700px;
  padding: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.section-header h2 {
  font-size: 20px;
  color: #303133;
  margin: 0;
}

/* 账号信息卡片 */
.account-info-card {
  margin-top: 20px;
}

/* 订单标签页 */
.order-tabs {
  width: 100%;
  margin-bottom: 20px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-card {
  margin-bottom: 15px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.order-status {
  color: #ed115d;
  font-weight: bold;
}

.order-products {
  padding: 10px 0;
}

.product-item {
  display: flex;
  margin-bottom: 10px;
}

.product-img {
  width: 80px;
  height: 80px;
  object-fit: contain;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.product-info {
  margin-left: 15px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.product-price {
  color: #606266;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
}

.order-total {
  font-weight: bold;
  color: #ed115d;
}

.order-actions {
  display: flex;
  gap: 10px;
}

/* 优惠券样式 */
.coupons-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.coupon-item {
  display: flex;
  background: linear-gradient(to right, rgba(237, 73, 58, 0.23), rgba(120, 82, 245, 0.11));
  border-radius: 15px;
  overflow: hidden;
  height: 100px;
  position: relative;
}

.coupon-item::before {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: 100px;
  width: 1px;
  background: radial-gradient(circle at 0 50%, transparent 8px, #fff 9px, #fff 16px) repeat-y;
  background-size: 1px 32px;
}

.coupon-value {
  width: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 35px;
  font-weight: bold;
  color: #ed115d;
  background-color: rgb(223, 177, 116);
  padding: 0 10px;
}

.coupon-info {
  flex: 1;
  padding: 15px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.coupon-title {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 5px;
}

.coupon-validity,
.coupon-condition {
  font-size: 12px;
  color: #606266;
}

.coupon-use {
  width: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 收藏商品样式 */
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.favorite-item {
  transition: transform 0.3s;
}

.favorite-item:hover {
  transform: translateY(-5px);
}

.favorite-img {
  width: 100%;
  height: 180px;
  object-fit: contain;
}

.favorite-info {
  padding: 10px 0;
}

.favorite-title {
  font-size: 16px;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.favorite-price {
  color: #ed115d;
  font-weight: bold;
}

.favorite-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

/* 地址管理样式 */
.addresses-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-radius: 10px;
}

.address-info {
  flex: 1;
}

.address-name-phone {
  margin-bottom: 10px;
}

.address-name {
  font-weight: bold;
  margin-right: 10px;
}

.address-detail {
  color: #606266;
}

.address-actions {
  display: flex;
  gap: 10px;
}

.region-select {
  display: flex;
  gap: 10px;
}

.region-select .el-select {
  flex: 1;
}

/* 空数据样式 */
.empty-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
}

/* 设置标签页样式 */
.settings-tabs {
  margin-top: 20px;
}

.settings-card {
  margin-top: 20px;
  padding: 10px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .user-page-container {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .favorites-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }
}
</style>
