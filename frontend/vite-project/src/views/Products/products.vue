<template>
  <div class="products-page">
    <top-nav />
    <div class="page-container">
      <h1>商品列表 <span v-if="categoryName">({{ categoryName }})</span></h1>

      <div v-if="isLoading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="loadError" class="error-container">
        <el-empty :description="loadError">
          <template #extra>
            <el-button type="primary" @click="fetchProducts">重试</el-button>
          </template>
        </el-empty>
      </div>

      <div v-else-if="filteredProducts.length === 0" class="empty-container">
        <el-empty description="没有找到相关商品">
          <template #extra>
            <el-button type="primary" @click="clearFilter">查看所有商品</el-button>
          </template>
        </el-empty>
      </div>

      <div v-else class="product-wrapper">
        <ProductCard
          v-for="product in filteredProducts"
          :key="product.id"
          :product="product"
          @add-to-cart="handleAddToCart"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
//页面导航栏标题信息
document.title = '商品列表 - 易猫商城';

import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import topNav from '../../components/topNav.vue';
import ProductCard from "@/components/productCard.vue";
import { getProducts } from "@/api/products.js";
import { addCartItem } from "@/api/cart.js";

const route = useRoute();
const router = useRouter();

// 产品列表数据
const products = ref([]);
// 加载状态
const isLoading = ref(true);
// 加载错误信息
const loadError = ref(null);
// 当前分类代码
const categoryCode = ref('');

// 监听路由参数变化
watch(() => route.query.category, (newCategory) => {
  categoryCode.value = newCategory || '';
});

// 计算属性：根据分类筛选商品
const filteredProducts = computed(() => {
  if (!categoryCode.value) {
    return products.value;
  }
  return products.value.filter(product => product.category === categoryCode.value);
});

// 计算属性：获取当前分类名称
const categoryName = computed(() => {
  if (!categoryCode.value) return '';

  const categoryMap = {
    'VIDEOCARD': '显卡',
    'CPU': '处理器',
    'MOTHERBOARD': '主板',
    'RAM': '内存',
    'STORAGE': '存储设备',
    'POWERSUPPLY & COOLING': '电源/散热器',
    'CASE & PERIPHERALS': '机箱/外设',
    'MONITOR': '显示器',
    'PAD': '平板电脑',
    'LAPTOP': '笔记本电脑',
  };

  return categoryMap[categoryCode.value] || categoryCode.value;
});

// 清除筛选器
const clearFilter = () => {
  router.push('/products');
};

// 在组件挂载后从后端加载产品数据
const fetchProducts = async () => {
  try {
    isLoading.value = true;
    loadError.value = null;

    const response = await getProducts();

    if (response.data && response.data.code === 200) {
      products.value = response.data.data;
      console.log('从后端成功获取产品数据:', products.value.length, '个产品');
    } else {
      throw new Error(response.data.message || '获取产品数据失败');
    }
  } catch (error) {
    console.error('加载产品数据失败:', error);
    loadError.value = '无法加载产品数据，请刷新页面重试';
    ElMessage.error('加载产品列表失败');
  } finally {
    isLoading.value = false;
  }
};

// 添加商品到购物车
const handleAddToCart = async (productToAdd) => {
  try {
    const itemData = {
      id: productToAdd.id,
      quantity: 1,
    };

    const response = await addCartItem(itemData);

    if (response.data && response.data.code === 200) {
      ElMessage({
        message: `${productToAdd.title} 已成功加入购物车！`,
        type: 'success',
        duration: 2000
      });
    } else {
      throw new Error(response.data.message || '添加商品到购物车失败');
    }
  } catch (error) {
    console.error('添加到购物车失败:', error);
    ElMessage.error(`添加 ${productToAdd.title} 到购物车失败，请重试！`);
  }
};

onMounted(() => {
  // 从路由中获取分类参数
  categoryCode.value = route.query.category || '';
  fetchProducts();
});
</script>

<style scoped>
.products-page {
  background-color: #f0f2f5;
  min-height: 100vh;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.loading-container,
.error-container,
.empty-container {
  margin: 40px 0;
  display: flex;
  justify-content: center;
}

.product-wrapper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 30px;
  margin-top: 20px;
}
</style>