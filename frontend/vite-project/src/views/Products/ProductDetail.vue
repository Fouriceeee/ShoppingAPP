<template>
  <div class="product-detail-page">
    <top-nav />
    <div class="page-container">
      <div v-if="isLoading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>

      <div v-else-if="loadError" class="error-container">
        <el-empty :description="loadError">
          <template #extra>
            <el-button type="primary" @click="fetchProductDetail">重试</el-button>
            <el-button @click="goBack">返回</el-button>
          </template>
        </el-empty>
      </div>

      <div v-else class="product-detail-container">
        <div class="product-image-section">
          <img :src="product.image" :alt="product.title" class="product-image">
        </div>

        <div class="product-info-section">
          <h1 class="product-title">{{ product.title }}</h1>

          <div class="product-price-section">
            <span class="price-label">价格：</span>
            <span class="price-value">¥{{ formatPrice }}</span>
          </div>

          <div class="product-description">
            <h3>商品描述</h3>
            <p>{{ product.description }}</p>
          </div>

          <div class="product-category">
            <span class="category-label">分类：</span>
            <span class="category-value">{{ formatCategory }}</span>
          </div>

          <div class="product-actions">
            <el-input-number v-model="quantity" :min="1" :max="99" size="large" />
            <el-button type="primary" size="large" @click="addToCart">
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ShoppingCart } from '@element-plus/icons-vue';
import topNav from '@/components/topNav.vue';
import { getProductById } from '@/api/products';
import { addCartItem } from '@/api/cart';

const route = useRoute();
const router = useRouter();
const product = ref({});
const isLoading = ref(true);
const loadError = ref(null);
const quantity = ref(1);

// 格式化价格显示
const formatPrice = computed(() => {
  if (!product.value.priceInteger) return '0';

  const priceInteger = typeof product.value.priceInteger === 'string' 
    ? parseInt(product.value.priceInteger, 10) 
    : product.value.priceInteger;

  const priceDecimal = product.value.priceDecimal;

  if (priceDecimal && priceDecimal !== '00') {
    return `${priceInteger}.${priceDecimal}`;
  }

  return priceInteger.toString();
});

// 格式化分类显示
const formatCategory = computed(() => {
  if (!product.value.category) return '';

  const categoryMap = {
    'VIDEOCARD': '显卡',
    'CPU': '处理器',
    'MOTHERBOARD': '主板',
    'RAM': '内存',
    'STORAGE': '存储设备',
    'POWERSUPPLY': '电源',
    'CASE': '机箱',
    'COOLER': '散热器',
    'MONITOR': '显示器',
    'PERIPHERAL': '外设'
  };

  return categoryMap[product.value.category] || product.value.category;
});

// 获取产品详情
const fetchProductDetail = async () => {
  const productId = route.params.id;
  if (!productId) {
    loadError.value = '产品ID无效';
    return;
  }

  try {
    isLoading.value = true;
    loadError.value = null;

    const response = await getProductById(productId);

    if (response.data && response.data.code === 200) {
      product.value = response.data.data;
    } else {
      throw new Error(response.data.message || '获取产品详情失败');
    }
  } catch (error) {
    console.error('加载产品详情失败:', error);
    loadError.value = '无法加载产品详情，请刷新页面重试';
  } finally {
    isLoading.value = false;
  }
};

// 添加到购物车
const addToCart = async () => {
  try {
    const itemData = {
      id: product.value.id,
      quantity: quantity.value,
    };

    const response = await addCartItem(itemData);

    if (response.data && response.data.code === 200) {
      ElMessage({
        message: `${product.value.title} 已成功加入购物车！`,
        type: 'success',
        duration: 2000
      });
    } else {
      throw new Error(response.data.message || '添加商品到购物车失败');
    }
  } catch (error) {
    console.error('添加到购物车失败:', error);
    ElMessage.error(`添加 ${product.value.title} 到购物车失败，请重试！`);
  }
};

// 返回上一页
const goBack = () => {
  router.back();
};

onMounted(() => {
  fetchProductDetail();
});
</script>

<style scoped>
.product-detail-page {
  background-color: #f0f2f5;
  min-height: 100vh;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.loading-container,
.error-container {
  margin: 40px 0;
  display: flex;
  justify-content: center;
}

.product-detail-container {
  display: flex;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.product-image-section {
  flex: 1;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f9f9f9;
}

.product-image {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
}

.product-info-section {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-title {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.product-price-section {
  margin-top: 10px;
}

.price-label {
  font-size: 16px;
  color: #666;
}

.price-value {
  font-size: 28px;
  color: #ed115d;
  font-weight: bold;
}

.product-description {
  margin-top: 20px;
}

.product-description h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}

.product-description p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.product-category {
  margin-top: 10px;
}

.category-label {
  font-size: 14px;
  color: #666;
}

.category-value {
  font-size: 14px;
  color: #333;
  font-weight: bold;
}

.product-actions {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .product-detail-container {
    flex-direction: column;
  }

  .product-image-section,
  .product-info-section {
    width: 100%;
  }
}
</style>
