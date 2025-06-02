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
            <el-checkbox v-model="selectAll" @change="handleSelectAllChange" />
          </template>
          <template #default="scope">
            <el-checkbox v-model="scope.row.selected" @change="handleItemSelectChange" class="select-checkbox" />
          </template>
        </el-table-column>
        <el-table-column label="商品信息" min-width="300">
          <template #default="scope">
            <div class="product-info">
              <el-image :src="scope.row.image" fit="contain" class="product-image" lazy />
              <div class="product-details">
                <p class="product-name">{{ scope.row.name }}</p>
                <p class="product-specs">{{ scope.row.specs || '默认规格' }}</p>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="单价" width="120" align="center">
          <template #default="scope">
            <span>¥{{ scope.row.price ? scope.row.price.toFixed(2) : '0.00' }}</span>
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
                @change="updateCartSummary"
            />
          </template>
        </el-table-column>

        <el-table-column label="小计" width="120" align="center">
          <template #default="scope">
            <span class="item-subtotal">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span>
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
            />
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-summary-bar">
        <div class="summary-left">
          <el-checkbox v-model="selectAll" @change="handleSelectAllChange" class="select-all-checkbox-button">
            全选 (已选 {{ selectedItemsCount }} 项)
          </el-checkbox>
          <el-button type="danger" plain :disabled="selectedItemsCount === 0" class="delete-selected-button">
            删除选中
          </el-button>
          <el-button type="info" plain :disabled="cartItems.length === 0" class="clean-up-button">
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
          >
            去结算 ({{ selectedItemsCount }})
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Cart'
}
</script>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus'; // 引入消息提示和确认框
import router from "@/router/index.js";
import TopNav from "@/components/topNav.vue";

import {
  getCartItems,
  addCartItem,
  updateCartItem,
  deleteCartItem,
  batchDeleteCartItems,
  clearAllCartItems,
  updateAllCartItemsSelection
} from '@/api/cart'; // 注意：@ 是 webpack/vite 的 alias，通常指向 src 目录


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
  return cartItems.value.reduce((sum, item) => item.selected ? sum + item.price * item.quantity : sum, 0);
});

// 控制全选框的状态：
// get: 如果所有商品都被选中，则为 true；否则为 false。
// set: 当全选框被点击时，更新所有商品的 selected 状态。
const selectAll = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(item => item.selected),
  set: async (val) => {
    // 乐观更新 UI：先将所有商品的选中状态设为新值，给用户即时反馈
    cartItems.value.forEach(item => (item.selected = val));
    try {
      // 调用 API 更新后端所有商品的选中状态
      await updateAllCartItemsSelection(val);
      ElMessage.success(val ? '已全选所有商品' : '已取消全选');
    } catch (error) {
      console.error('更新全选状态失败:', error);
      ElMessage.error('更新全选状态失败，请稍后再试。');
      // 如果 API 调用失败，回滚 UI 状态以保持数据一致性
      cartItems.value.forEach(item => (item.selected = !val));
    }
  },
});

// --- 方法 ---

// 页面头部返回按钮的回调
const goBack = () => {
  console.log('返回上一页');
  router.back()
};

// 购物车为空时“马上去逛逛”按钮的回调
const goShopping = () => {
  console.log('去购物');
  router.push('/')
};

// 获取购物车商品数据
const fetchCartItems = async () => {
  isLoading.value = true; // 开始加载动画
  try {
    const response = await getCartItems(); // 调用封装的 API 函数
    cartItems.value = response.data; // 更新本地数据
    console.log('购物车数据已加载:', cartItems.value);
  } catch (error) {
    console.error('获取购物车数据失败:', error);
    // 错误处理已在 `src/api/index.js` 的响应拦截器中统一处理，这里可以根据需要添加额外提示
    // ElMessage.error('加载购物车失败，请稍后再试。');
  } finally {
    isLoading.value = false; // 停止加载动画
  }
};

// 处理单个商品数量变化
const updateCartItemQuantity = async (item) => {
  // 优化：只有当数量实际改变时才发送请求
  // 假设 item.originalQuantity 存储了改变前的值，或这里简单地认为 v-model 绑定后会触发更新
  try {
    // 构造只包含需要更新字段的数据对象
    const updateData = { quantity: item.quantity };
    await updateCartItem(item.id, updateData); // 调用封装的 API 函数进行 PATCH 更新
    // ElMessage.success('商品数量更新成功'); // 频繁更新可能不适合每次都提示
  } catch (error) {
    console.error(`更新商品 ${item.name} 数量失败:`, error);
    ElMessage.error(`更新数量失败，请稍后再试。`);
    // 如果后端更新失败，可能需要回滚本地的 quantity 状态
    // fetchCartItems(); // 或者重新拉取数据以确保一致性
  }
};

// 处理单个商品选择框变化
const handleItemSelectChange = async (item) => {
  // 乐观更新 UI：因为 v-model 已经双向绑定，本地状态 item.selected 已经改变
  try {
    const updateData = { selected: item.selected };
    await updateCartItem(item.id, updateData); // 调用封装的 API 函数进行 PATCH 更新
    // ElMessage.success('商品选中状态更新成功'); // 频繁更新可能不适合每次都提示
  } catch (error) {
    console.error(`更新商品 ${item.name} 选中状态失败:`, error);
    ElMessage.error(`更新选中状态失败，请稍后再试。`);
    // 如果后端更新失败，回滚 UI 状态
    item.selected = !item.selected;
  }
};

// 删除购物车中的单个商品
const deleteCartItemConfirm = async (itemId) => {
  try {
    // 弹出 Element Plus 确认框
    await ElMessageBox.confirm('确定要从购物车删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    await deleteCartItem(itemId); // 调用封装的 API 函数
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

    await batchDeleteCartItems(selectedIds); // 调用封装的 API 函数进行批量删除
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

    await clearAllCartItems(); // 调用封装的 API 函数
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

.product-specs {
  font-size: 0.9em;
  color: #909399;
}

.item-subtotal {
  font-weight: bold;
  color: #4aa6e3;
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
  background-color: #fa4343;
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
  color: #4aa6e3;
}

.checkout-button {
  background-color: #0b78ea;
  border: none;
  padding: 10px 30px;
  font-size: 1.1em;
}

.checkout-button:hover {
  background-color: #91c1ff;
}
</style>