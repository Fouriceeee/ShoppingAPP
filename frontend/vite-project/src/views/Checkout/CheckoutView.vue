<template>
  <div class="checkout-container">
    <topNav />
    <div v-if="isLoading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>
    <div v-else class="checkout-content">
      <h2>确认订单</h2>

      <div class="checkout-section address-section">
        <h3>选择收货地址</h3>
        <div 
          v-for="address in addresses" 
          :key="address.id" 
          class="address-card" 
          :class="{ selected: selectedAddress && selectedAddress.id === address.id }"
          @click="selectAddress(address)"
        >
          <div class="address-info">
            <span class="name">{{ address.name }}</span>
            <span class="phone">{{ address.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') }}</span>
          </div>
          <div class="address-detail">
            {{ address.address }}
          </div>
          <span v-if="address.isDefault" class="default-tag">默认地址</span>
        </div>
        <a href="#" class="manage-address">管理收货地址</a>
      </div>

      <div class="checkout-section order-section">
        <h3>确认订单信息</h3>
        <table class="order-table">
          <thead>
          <tr>
            <th class="product-info">商品信息</th>
            <th>单价</th>
            <th>数量</th>
            <th>小计</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="item in cartItems" :key="item.productId">
            <td class="product-info">
              <img :src="item.image" :alt="item.name" class="product-image" />
              <span class="product-name">{{ item.name }}</span>
            </td>
            <td>¥{{ parseFloat(item.priceInteger + '.' + item.priceDecimal).toFixed(2) }}</td>
            <td>{{ item.quantity }}</td>
            <td>¥{{ (parseFloat(item.priceInteger + '.' + item.priceDecimal) * item.quantity).toFixed(2) }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <div class="checkout-section payment-section">
        <h3>支付方式</h3>
        <div class="payment-options">
          <div
              class="payment-option"
              :class="{ selected: paymentMethod === 'alipay' }"
              @click="paymentMethod = 'alipay'"
          >
            <img src="https://upload.wikimedia.org/wikipedia/zh/thumb/5/5f/Alipay_logo_2024.png/250px-Alipay_logo_2024.png" alt="Alipay" />
            <span>支付宝</span>
          </div>
          <div
              class="payment-option"
              :class="{ selected: paymentMethod === 'wechat' }"
              @click="paymentMethod = 'wechat'"
          >
            <img src="https://storage.360buyimg.com/payment-assets/sdk/bank/PAY-WEIXIN.png" alt="WeChat Pay" />
            <span>微信支付</span>
          </div>
        </div>
      </div>

      <div class="checkout-summary">
        <div class="summary-details">
          <p>
            <span class="total-quantity">{{ totalItems }}</span> 件商品，总商品金额:
            <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </p>
          <p>运费: <span class="shipping-fee">¥0.00</span></p>
        </div>
        <div class="summary-total">
          <div class="final-price-container">
            <span class="final-price-label">应付总额：</span>
            <span class="final-price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <button @click="placeOrder" class="place-order-btn">提交订单</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import topNav from '@/components/topNav.vue';
import { getCartItems } from '@/api/cart';
import { placeOrder as submitOrder } from '@/api/orders';
import { getProductImageUrl } from '@/utils/productService';
import { checkAuth } from '@/utils/userService';

// Set page title
document.title = '确认订单 - 易猫商城';

// Router for navigation
const router = useRouter();

// Reactive data
const cartItems = ref([]);
const isLoading = ref(true);
const paymentMethod = ref('alipay'); // Default payment method
const selectedAddress = ref(null);
const addresses = ref([
  {
    id: 1,
    name: '张三',
    phone: '13888888888',
    address: '浙江省 杭州市 西湖区 文三路 123 号',
    isDefault: true
  }
]);

// Computed properties
const totalItems = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0);
});

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    const price = parseFloat(item.priceInteger + '.' + item.priceDecimal);
    return sum + price * item.quantity;
  }, 0);
});

// Fetch cart items
const fetchCartItems = async () => {
  isLoading.value = true;
  try {
    const response = await getCartItems();
    // Filter only selected items for checkout
    cartItems.value = response.data.filter(item => item.selected);

    if (cartItems.value.length === 0) {
      ElMessage.warning('没有选择要结算的商品，请返回购物车选择商品');
      router.push('/cart');
    }
  } catch (error) {
    console.error('获取购物车数据失败:', error);
    ElMessage.error('加载购物车失败，请稍后再试');
  } finally {
    isLoading.value = false;
  }
};

// Select address
const selectAddress = (address) => {
  selectedAddress.value = address;
};

// Place order
const placeOrder = async () => {
  // Check if user is logged in
  const currentUser = checkAuth();
  if (!currentUser) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }

  // Check if address is selected
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址');
    return;
  }

  try {
    // Prepare order data
    const orderData = {
      items: cartItems.value.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        price: parseFloat(item.priceInteger + '.' + item.priceDecimal)
      })),
      shippingAddress: selectedAddress.value,
      paymentMethod: paymentMethod.value,
      totalAmount: totalPrice.value
    };

    // Show confirmation dialog
    await ElMessageBox.confirm(
      `确认提交订单？总金额：¥${totalPrice.value.toFixed(2)}`,
      '提交订单',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    );

    // Call API to place order
    const response = await submitOrder(orderData);

    ElMessage.success('订单提交成功！');

    // Navigate to order success page or orders list
    router.push({
      path: '/order/success',
      query: { 
        orderId: response.data.orderId,
        amount: totalPrice.value.toFixed(2)
      }
    });
  } catch (error) {
    if (error === 'cancel') {
      console.log('用户取消了订单提交');
    } else {
      console.error('提交订单失败:', error);
      ElMessage.error('提交订单失败，请稍后再试');
    }
  }
};

// Lifecycle hooks
onMounted(() => {
  // Set default selected address
  selectedAddress.value = addresses.value.find(addr => addr.isDefault) || addresses.value[0];

  // Fetch cart items
  fetchCartItems();
});
</script>

<style scoped>
.checkout-container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.loading-container {
  width: 80%;
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}

.checkout-content {
  width: 80%;
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}

h2, h3 {
  color: #333;
}

.checkout-section {
  margin-bottom: 30px;
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 20px;
}

/* 地址部分 */
.address-section .address-card {
  border: 1px solid #e0e0e0;
  padding: 15px;
  border-radius: 4px;
  position: relative;
  cursor: pointer;
  width: 300px;
}
.address-section .address-card.selected {
  border-color: #ed115d;
  border-width: 2px;
}
.address-info .name {
  font-weight: bold;
  margin-right: 15px;
}
.address-detail {
  color: #666;
  margin-top: 8px;
}
.default-tag {
  background-color: #ed115d;
  color: white;
  padding: 2px 6px;
  font-size: 12px;
  border-radius: 3px;
  position: absolute;
  top: 15px;
  right: 15px;
}
.manage-address {
  color: #007bff;
  text-decoration: none;
  font-size: 14px;
  display: inline-block;
  margin-top: 10px;
}

/* 订单表格 */
.order-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
}
.order-table th, .order-table td {
  padding: 12px;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
}
.order-table th {
  background-color: #f9f9f9;
  font-weight: normal;
}
.order-table .product-info {
  text-align: left;
  display: flex;
  align-items: center;
}
.product-image {
  width: 60px;
  height: 60px;
  margin-right: 10px;
  border-radius: 4px;
}

/* 支付方式 */
.payment-options {
  display: flex;
  gap: 20px;
  margin-top: 15px;
}
.payment-option {
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
}
.payment-option.selected {
  border-color: #ed115d;
  border-width: 2px;
}
.payment-option img {
  height: 24px;
}

/* 结算总结 */
.checkout-summary {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-top: 20px;
  text-align: right;
}

.summary-details {
  color: #666;
  font-size: 14px;
}

.summary-total {
  margin-top: 15px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.final-price-container .final-price-label {
  font-size: 14px;
}

.final-price-container .final-price {
  font-size: 24px;
  font-weight: bold;
  color: #ed115d;
}

.place-order-btn {
  background-color: #7852f5;
  color: white;
  border: none;
  padding: 12px 30px;
  font-size: 18px;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.place-order-btn:hover {
  background-color: #4d36a5;
}
</style>
