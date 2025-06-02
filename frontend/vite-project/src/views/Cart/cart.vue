<template>
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
          <el-checkbox v-model="selectAll" class="select-all-checkbox">
            全选 (已选 {{ selectedItemsCount }} 项)
          </el-checkbox>
          <el-button type="danger" plain :disabled="selectedItemsCount === 0">
            删除选中
          </el-button>
          <el-button type="info" plain :disabled="cartItems.length === 0">
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
import { ref } from 'vue';
import {ArrowLeft, ShoppingCart} from '@element-plus/icons-vue';

// 模拟数据，用于展示页面布局，实际开发中会从后端获取
const isLoading = ref(false); // 模拟加载状态
const cartItems = ref([
  {
    id: 1,
    name: '商品A',
    image: 'https://via.placeholder.com/80x80/cccccc/ffffff?text=ProductA',
    specs: '颜色：红色，尺码：M',
    price: 99.00,
    quantity: 1,
    stock: 10,
    selected: false,
  },
  {
    id: 2,
    name: '商品B',
    image: 'https://via.placeholder.com/80x80/999999/ffffff?text=ProductB',
    specs: '颜色：蓝色，尺码：L',
    price: 199.50,
    quantity: 2,
    stock: 5,
    selected: true,
  },
  {
    id: 3,
    name: '商品C',
    image: 'https://via.placeholder.com/80x80/666666/ffffff?text=ProductC',
    specs: null,
    price: 50.00,
    quantity: 1,
    stock: 20,
    selected: false,
  },
]);

const selectAll = ref(false); // 全选状态
const selectedItemsCount = ref(cartItems.value.filter(item => item.selected).length); // 已选商品数量
const totalAmount = ref(cartItems.value.reduce((sum, item) => item.selected ? sum + item.price * item.quantity : sum, 0)); // 合计金额

// 页面头部返回按钮的回调
const goBack = () => {
  console.log('返回上一页');
  // 实际项目中可以实现 router.back() 或其他返回逻辑
};

// 购物车为空时“马上去逛逛”按钮的回调
const goShopping = () => {
  console.log('去购物');
  // 实际项目中可以实现 router.push('/shop') 或其他跳转逻辑
};
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
  color: #f56c6c;
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

.select-all-checkbox {
  margin-right: 10px;
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
  color: #f56c6c;
}

.checkout-button {
  padding: 10px 30px;
  font-size: 1.1em;
}
</style>