<template>
  <top-nav />
  <div class="cart-page-container">
    <el-page-header @back="goBack" class="page-header">
      <template #icon><el-icon><arrow-left /></el-icon></template>
      <template #title>返回</template>
      <template #content>
        <span class="text-large font-600 mr-3">我的购物车</span>
      </template>
    </el-page-header>

    <div v-if="isLoading" class="cart-loading">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="cartItems.length === 0 && !isLoading" class="empty-cart-container">
      <el-empty description="购物车还是空的哦～">
        <el-button type="primary" @click="goShopping">马上去逛逛</el-button>
      </el-empty>
    </div>

    <div v-else class="cart-content">
      <el-table :data="cartItems" style="width: 100%" class="cart-table">
        <el-table-column label="选择" width="70" class="all-select">
          <template #header>
            <el-checkbox v-model="selectAll" /> </template>
          <template #default="scope">
            <el-checkbox v-model="scope.row.selected" @change="handleItemSelectChange(scope.row)" class="select-checkbox" />
          </template>
        </el-table-column>
        <el-table-column label="商品信息" min-width="300">
          <template #default="scope">
            <div class="product-info">
              <el-image
                  :src="getProductImageUrl(scope.row.image)"
                  fit="contain"
                  class="product-image"
                  lazy
                  :alt="scope.row.title"
              >
                <!-- 添加加载失败时的占位图 -->
                <template #error>
                  <div class="image-error">
                    <el-icon><picture-filled /></el-icon>
                  </div>
                </template>
              </el-image>

              <div class="product-details">
                <p class="product-name">{{ scope.row.title }}</p>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="单价" width="120" align="center">
          <template #default="scope">
            <span>¥{{ scope.row.priceInteger }}.{{ scope.row.priceDecimal }}</span>
          </template>
        </el-table-column>

        <el-table-column label="数量" width="180" align="center">
          <template #default="scope">
            <el-input-number
                v-model="scope.row.quantity"
                :min="1"
                :max="scope.row.stock || 99"
                size="small"
                controls-position="right"
                @change="updateCartItemQuantity(scope.row)" />
          </template>
        </el-table-column>

        <el-table-column label="小计" width="120" align="center">
          <template #default="scope">
            <span class="item-subtotal">
              ¥{{ (parseFloat(scope.row.priceInteger + '.' + scope.row.priceDecimal) * scope.row.quantity).toFixed(2) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" align="center">
          <template #default="scope">
            <el-button
                type="danger"
                icon="delete"
                circle
                plain
                size="small"
                @click="deleteCartItemConfirm(scope.row.id)" />
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-summary-bar">
        <div class="summary-left">
          <el-checkbox v-model="selectAll" class="select-all-checkbox-button">
            全选 (已选 {{ selectedItemsCount }} 项)
          </el-checkbox>
          <el-button
              type="danger"
              plain
              :disabled="selectedItemsCount === 0"
              class="delete-selected-button"
              @click="deleteSelectedItems" >
            删除选中
          </el-button>
          <el-button
              type="info"
              plain
              :disabled="cartItems.length === 0"
              class="clean-up-button"
              @click="clearCart" >
            清空购物车
          </el-button>
        </div>
        <div class="summary-right">
          <div class="total-amount">
            合计：<span class="amount-value">¥{{ totalAmount ? totalAmount.toFixed(2) : '0.00' }}</span>
          </div>
          <el-button
              type="danger"
              size="large"
              class="checkout-button"
              :disabled="selectedItemsCount === 0"
              @click="checkout" >
            去结算 ({{ selectedItemsCount }})
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import router from "@/router/index.js";
import TopNav from "@/components/topNav.vue";

// 导入所有购物车相关的 API 函数
import {
  getCartItems,
  addCartItem, // 虽然 CartView 自己不直接用，但可能会通过事件接收 ProductCard 的 add-to-cart 事件
  updateCartItem,
  deleteCartItem,
  batchDeleteCartItems,
  clearAllCartItems,
  selectAllCartItems
} from '@/api/cart';

// --- 响应式数据 ---
const isLoading = ref(true); // 控制加载状态，开始时设置为 true
const cartItems = ref([]); // 购物车商品列表，初始为空数组

// --- 计算属性 ---

// 计算已选商品数量
const selectedItemsCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length;
});

// 计算购物车中所有选中商品的总金额
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    if (item.selected) {
      // 确保将 priceInteger 和 priceDecimal 组合为数字
      const price = parseFloat(item.priceInteger + '.' + item.priceDecimal);
      return sum + price * item.quantity;
    }
    return sum;
  }, 0);
});



// --- 方法 ---

/**
 * 计算商品图片的完整URL
 * @param {string} imagePath - 图片路径
 * @returns {string} 完整的图片URL
 */
const getProductImageUrl = (imagePath) => {
  if (!imagePath) {
    // 如果没有图片，返回默认图片
    try {
      return new URL('../assets/pictures/products/default-product.jpg', import.meta.url).href;
    } catch (error) {
      return '';
    }
  }

  // 处理后端返回的图片路径
  if (imagePath.startsWith('/images/')) {
    // 静态资源服务器的基础URL
    const baseUrl = 'http://localhost:8080';
    // 将 "/images/xxx.webp" 转换为 "http://localhost:8080/images/xxx.webp"
    return `${baseUrl}/${imagePath.substring(1)}`;
  } else if (imagePath.startsWith('http')) {
    // 如果已经是完整URL，直接返回
    return imagePath;
  } else {
    // 本地资源文件夹中的图片
    try {
      return new URL(`../assets/pictures/products/${imagePath}`, import.meta.url).href;
    } catch (error) {
      console.error('无法加载产品图片:', error);
      return '';
    }
  }
};


// 控制全选框的状态：
const selectAll = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(item => item.selected),
  set: async (val) => {
    // 乐观更新 UI：先将所有商品的选中状态设为新值，给用户即时反馈
    cartItems.value.forEach(item => (item.selected = val));
    try {
      // 调用 API 更新后端所有商品的选中状态
      console.log("CartView:调用selectAllCartItems开始");
      await selectAllCartItems(val);
      console.log("cartView:调用selectAllCartItems成功");
      /*ElMessage.success(val ? '已全选所有商品' : '已取消全选');*/
    } catch (error) {
      console.error('更新全选状态失败:', error);
      /*ElMessage.error('更新全选状态失败，请稍后再试。');*/
      // 如果 API 调用失败，回滚 UI 状态以保持数据一致性
      // 这里需要重新从后端获取数据，以确保最终状态的一致性
      await fetchCartItems(); // 重新拉取数据
    }
  },
});

// 页面头部返回按钮的回调
const goBack = () => {
  console.log('返回上一页');
  router.back();
};

// 购物车为空时“马上去逛逛”按钮的回调
const goShopping = () => {
  console.log('去购物');
  router.push('/');
};

// 获取购物车商品数据 (核心)
const fetchCartItems = async () => {
  isLoading.value = true; // 开始加载动画
  try {
    const response = await getCartItems(); // 调用封装的 API 函数
    cartItems.value = response.data; // 更新本地数据
    console.log('购物车数据已加载:', cartItems.value);
    // 调试：打印每个商品的图片路径
    cartItems.value.forEach(item => {
      console.log(`商品 ${item.title} 的图片路径: ${item.image}`);
      console.log(`处理后的图片URL: ${getProductImageUrl(item.image)}`);
    });

  } catch (error) {
    console.error('获取购物车数据失败:', error);
    ElMessage.error('加载购物车失败，请稍后再试。');
  } finally {
    isLoading.value = false; // 停止加载动画
  }
};

// 处理单个商品数量变化 (触发后端 PATCH 更新)
const updateCartItemQuantity = async (item) => {
  // item.quantity 已经是更新后的值 (v-model 绑定)
  try {
    // 构造只包含需要更新字段的数据对象
    const updateData = { quantity: item.quantity };
    const response = await updateCartItem(item.id, updateData); // 调用 PATCH API
    // 假设后端返回的是更新后的 item，如果需要的话可以更新本地 item，但 v-model 已处理
    // ElMessage.success('商品数量更新成功'); // 频繁更新可能不适合每次都提示
    // 如果后端因为 quantity 为 0 而删除了商品，我们需要更新本地列表
    if (response.data && response.data.message && response.data.message.includes("removed due to quantity 0")) {
      cartItems.value = cartItems.value.filter(cartItem => cartItem.id !== item.id);
      ElMessage.success('商品数量更新为0，已从购物车中移除。');
    }
  } catch (error) {
    console.error(`更新商品 ID: ${item.id} 数量失败:`, error);
    ElMessage.error(`更新数量失败，请稍后再试。`);
    // API 失败时，最好重新从后端拉取数据以确保本地和后端一致
    await fetchCartItems();
  }
};

// 处理单个商品选择框变化 (触发后端 PATCH 更新)
const handleItemSelectChange = async (item) => {
  // item.selected 已经是更新后的值 (v-model 绑定)
  try {
    const updateData = { selected: item.selected };
    await updateCartItem(item.id, updateData); // 调用 PATCH API
    // ElMessage.success('商品选中状态更新成功'); // 频繁更新可能不适合每次都提示
  } catch (error) {
    console.error(`更新商品 ID: ${item.id} 选中状态失败:`, error);
    ElMessage.error(`更新选中状态失败，请稍后再试。`);
    // 如果后端更新失败，回滚 UI 状态
    item.selected = !item.selected;
  }
};

// 删除购物车中的单个商品
const deleteCartItemConfirm = async (itemId) => {
  try {
    await ElMessageBox.confirm('确定要从购物车删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    await deleteCartItem(itemId); // 调用 DELETE API
    ElMessage.success('商品已成功删除！');
    // 从本地数据中移除该商品，Vue 会自动更新 UI
    cartItems.value = cartItems.value.filter(item => item.id !== itemId);
  } catch (error) {
    if (error === 'cancel') {
      console.log('删除操作已取消');
    } else {
      console.error(`删除商品 ID: ${itemId} 失败:`, error);
      ElMessage.error('删除商品失败，请稍后再试。');
    }
  }
};

// 删除所有选中的商品
const deleteSelectedItems = async () => {
  if (selectedItemsCount.value === 0) {
    ElMessage.info('请先选择要删除的商品。');
    return;
  }

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedItemsCount.value} 项商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 获取所有选中商品的 ID
    const selectedIds = cartItems.value
        .filter(item => item.selected)
        .map(item => item.id);

    await batchDeleteCartItems(selectedIds); // 调用 POST 批量删除 API
    ElMessage.success(`${selectedItemsCount.value} 件商品已成功删除！`);
    // 过滤掉所有被选中的商品，更新本地数据
    cartItems.value = cartItems.value.filter(item => !item.selected);
  } catch (error) {
    if (error === 'cancel') {
      console.log('删除选中操作已取消');
    } else {
      console.error('删除选中商品失败:', error);
      ElMessage.error('删除选中商品失败，请稍后再试。');
    }
  }
};

// 清空购物车
const clearCart = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.info('购物车已经是空的了。');
    return;
  }

  try {
    await ElMessageBox.confirm('确定要清空购物车吗？此操作不可撤销！', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    await clearAllCartItems(); // 调用 DELETE 清空购物车 API
    ElMessage.success('购物车已成功清空！');
    cartItems.value = []; // 清空本地数据
  } catch (error) {
    if (error === 'cancel') {
      console.log('清空购物车操作已取消');
    } else {
      console.error('清空购物车失败:', error);
      ElMessage.error('清空购物车失败，请稍后再试。');
    }
  }
};

// 结算功能（待实现）
const checkout = () => {
  if (selectedItemsCount.value === 0) {
    ElMessage.warning('请选择要结算的商品。');
    return;
  }
  ElMessage.info('结算功能待实现...');
  // 这里可以跳转到订单确认页，或弹出支付窗口等
};

// --- 生命周期钩子 ---
onMounted(() => {
  fetchCartItems(); // 组件挂载时自动获取购物车数据
});
</script>

<style scoped>
.cart-page-container {
  padding: 20px;
  max-width: 1200px;
  margin: 20px auto;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-header {
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 15px;
}

.cart-loading {
  padding: 50px 0;
  text-align: center;
}

.empty-cart-container {
  padding: 50px 0;
  text-align: center;
}

.cart-table {
  margin-bottom: 20px;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-image {
  width: 80px;
  height: 80px;
  margin-right: 15px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-details {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-name {
  font-weight: bold;
  margin-bottom: 5px;
  color: #303133;
}

.item-subtotal {
  font-weight: bold;
  color: #ed115d;
}

.cart-summary-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}

.summary-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.select-checkbox {
  border-width: 5px;
}

.select-all-checkbox-button {
  margin-right: 10px;
}

.delete-selected-button {
  background-color: #fef0f0;
}

.delete-selected-button:hover {
  background-color: #ed115d;
}

.clean-up-button {
  color:black;
}

.summary-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-amount {
  font-size: 1.1em;
  color: #303133;
}

.amount-value {
  font-size: 1.4em;
  font-weight: bold;
  color: #ed115d;
}

.checkout-button {
  background-color: #7852f5;
  border: none;
  padding: 10px 30px;
  font-size: 1.1em;
}

.checkout-button:hover {
  background-color: #4d36a5;
}

.checkout-button:active {
  background-color: #4d36a5; /* 稍微深一点的紫色，表示点击反馈 */
  transform: scale(95%);
}

.image-error {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 14px;
}

.image-error .el-icon {
  font-size: 32px;
}

</style>