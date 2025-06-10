<template>
  <div class="search-page">
    <top-nav />
    <div class="page-container">
      <h1>搜索商品</h1>

      <div v-if="isLoading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="loadError" class="error-container">
        <el-empty :description="loadError">
          <template #extra>
            <el-button type="primary" @click="handleSearch">重试</el-button>
          </template>
        </el-empty>
      </div>

      <div v-else-if="hasSearched && searchResults.length === 0" class="empty-container">
        <el-empty description="没有找到相关商品">
          <template #extra>
            <el-button type="primary" @click="clearSearch">查看所有商品</el-button>
          </template>
        </el-empty>
      </div>

      <div v-else-if="hasSearched" class="search-results">
        <h2>搜索结果 ({{ searchResults.length }})</h2>
        <div class="product-wrapper">
          <ProductCard
            v-for="product in searchResults"
            :key="product.id"
            :product="product"
            @add-to-cart="handleAddToCart"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 页面导航栏标题信息
document.title = '搜索商品 - 易猫商城';

import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import topNav from '../../components/topNav.vue';
import ProductCard from "@/components/productCard.vue";
import { searchProducts } from "@/api/products.js";
import { addCartItem } from "@/api/cart.js";

const router = useRouter();

// 搜索相关状态
const searchQuery = ref('');
const searchResults = ref([]);
const hasSearched = ref(false);
const isLoading = ref(false);
const loadError = ref(null);

// 执行搜索
const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }

  try {
    isLoading.value = true;
    loadError.value = null;
    
    const response = await searchProducts(searchQuery.value);
    
    if (response.data && response.data.code === 200) {
      searchResults.value = response.data.data;
      hasSearched.value = true;
      console.log('搜索结果:', searchResults.value.length, '个商品');
    } else {
      throw new Error(response.data.message || '搜索失败');
    }
  } catch (error) {
    console.error('搜索失败:', error);
    loadError.value = '搜索失败，请重试';
    ElMessage.error('搜索商品失败');
  } finally {
    isLoading.value = false;
  }
};

// 清除搜索
const clearSearch = () => {
  searchQuery.value = '';
  searchResults.value = [];
  hasSearched.value = false;
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
  // 如果URL中有查询参数，自动执行搜索
  const urlParams = new URLSearchParams(window.location.search);
  const queryParam = urlParams.get('q');
  if (queryParam) {
    searchQuery.value = queryParam;
    handleSearch();
  }
});
</script>

<style scoped>
.search-page {
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
  margin-bottom: 20px;
  color: #333;
}

h2 {
  margin-top: 30px;
  margin-bottom: 20px;
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