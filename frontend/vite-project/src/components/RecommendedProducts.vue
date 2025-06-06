<template>
  <div class="recommended-products-wrapper" ref="moduleRef">
    <div class="header-wrapper">
      <h3>为你推荐</h3>
      <div @click="navigateToProducts" class="more-link">查看更多 ></div>
    </div>

    <div v-if="isLoading" class="loading-container">
      <el-skeleton :rows="1" animated />
    </div>

    <div v-else-if="loadError" class="error-container">
      <span class="error-text">{{ loadError }}</span>
      <el-button size="small" @click="fetchRecommendedProducts">重试</el-button>
    </div>

    <div v-else class="products-container">
      <div 
        v-for="product in recommendedProducts" 
        :key="product.id" 
        class="recommended-product-item"
        @click="navigateToProductDetail(product.id)"
      >
        <img :src="getProductImageUrl(product.image)
" :alt="product.title" class="product-image">
        <div class="product-title">{{ truncateTitle(product.title) }}</div>
        <div class="product-price">¥{{ formatPrice(product) }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getProducts } from '@/api/products';
import { ElMessage } from 'element-plus';

const router = useRouter();
const isLoading = ref(true);
const loadError = ref(null);
const allProducts = ref([]);
const recommendedProducts = ref([]);


// 获取产品数据
const fetchRecommendedProducts = async () => {
  try {
    isLoading.value = true;
    loadError.value = null;

    const response = await getProducts();

    if (response.data && response.data.code === 200) {
      allProducts.value = response.data.data;
      // 从所有产品中随机选择4个作为推荐产品
      recommendedProducts.value = getRandomProducts(allProducts.value, 4);
      console.log('成功获取推荐产品:', recommendedProducts.value.length, '个产品');
      console.log('DEBUG:推荐产品数据详情:', JSON.stringify(recommendedProducts.value));
    } else {
      throw new Error(response.data.message || '获取推荐产品失败');
    }
  } catch (error) {
    console.error('加载推荐产品失败:', error);
    loadError.value = '加载推荐失败';
  } finally {
    isLoading.value = false;
  }
};

//计算图片路径
const getProductImageUrl = (imagePath) => {
  console.log('DEBUG:RecommendedProducts处理图片路径:', imagePath);

  if (!imagePath) {
    // 如果没有图片，返回默认图片
    try {
      const defaultUrl = new URL('../assets/pictures/products/default-product.jpg', import.meta.url).href;
      console.log('DEBUG:使用默认图片:', defaultUrl);
      return defaultUrl;
    } catch (error) {
      console.error('加载默认图片失败:', error);
      return '';
    }
  }

  // 处理后端返回的图片路径
  if (imagePath.startsWith('/images/')) {
    const baseUrl = 'http://localhost:8080';
    const imageUrl = `${baseUrl}${imagePath}`;
    console.log('DEBUG:RecommendedProducts生成的图片URL:', imageUrl);
    return imageUrl;
  } else if (imagePath.startsWith('http')) {
    // 如果已经是完整URL，直接返回
    console.log('DEBUG:使用完整URL:', imagePath);
    return imagePath;
  } else {
    // 本地资源文件夹中的图片
    try {
      const localUrl = new URL(`../assets/pictures/products/${imagePath}`, import.meta.url).href;
      console.log('DEBUG:使用本地图片:', localUrl);
      return localUrl;
    } catch (error) {
      console.error('无法加载产品图片:', error);
      return '';
    }
  }
};


// 从数组中随机选择n个元素
const getRandomProducts = (array, n) => {
  // 如果数组长度小于等于n，直接返回整个数组的浅拷贝
  if (array.length <= n) return [...array];

  // 创建原数组的拷贝，避免修改原数组
  const arrayCopy = [...array];
  const result = [];

  // 随机选择n个元素
  for (let i = 0; i < n; i++) {
    // 生成0到当前arrayCopy长度之间的随机索引
    const randomIndex = Math.floor(Math.random() * arrayCopy.length);
    // 从arrayCopy中移除该元素并添加到结果数组
    result.push(arrayCopy.splice(randomIndex, 1)[0]);
  }

  return result;
};

// 格式化价格显示
const formatPrice = (product) => {
  // 处理整数部分和小数部分
  const priceInteger = typeof product.priceInteger === 'string' 
    ? parseInt(product.priceInteger, 10) 
    : product.priceInteger;

  const priceDecimal = product.priceDecimal;

  // 如果有小数部分，则显示小数；否则只显示整数
  if (priceDecimal && priceDecimal !== '00') {
    return `${priceInteger}.${priceDecimal}`;
  }

  return priceInteger.toString();
};

// 截断过长的标题
const truncateTitle = (title) => {
  return title.length > 10 ? title.substring(0, 10) + '...' : title;
};

// 导航到产品详情页
const navigateToProductDetail = (productId) => {
  router.push(`/products/${productId}`);
};

// 导航到所有商品页面
const navigateToProducts = () => {
  router.push('/products');
};

onMounted(() => {
  fetchRecommendedProducts();
});
</script>

<style scoped>
.recommended-products-wrapper {
  padding: 15px;
  width: 100%; /* 默认填充父容器宽度 */
  height: 200px;
  min-width: 300px; /* 模块最小宽度 */
  max-width: 512px; /* 模块最大宽度 */
  background-color: #edeef2; /* 模块背景色 */
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
  overflow: hidden; /* 确保内容超出时被裁剪 */
  display: flex; /* 使用 flex 垂直布局头部和内容区域 */
  flex-direction: column;
}

.header-wrapper {
  display: flex;
  height: 20px;
  justify-content: space-between;
  align-items: center;
  margin: 0 10px;   /* 与下方元素的间距 */
  padding-bottom: 0px;   /* 模块内边距 */
  flex-shrink: 0;   /* 头部不压缩 */
}

.header-wrapper h3 {
  margin: 0;
  font-size: 1.1em;
  color: #333;
}

.more-link {
  font-size: 0.9em;
  color: #ed115d;
  text-decoration: none;
  transition: color 0.2s ease;
  cursor: pointer;
}

.more-link:hover {
  color: #b5174d;
}

.loading-container,
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 150px;
  width: 100%;
  flex-direction: column;
  gap: 10px;
}

.error-text {
  color: #f56c6c;
  font-size: 0.9em;
}

.products-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  row-gap: 50px;    /* 上下间距 */
  column-gap: 0; /* 左右间距 */
  transition: transform 0.3s ease; /* 如果将来需要滑动效果可以添加 */
}

.recommended-product-item {
  padding-top: 10px;
  flex-shrink: 0; /* 确保商品项在空间不足时不会收缩 */
  height: 140px;
  width: 100px;
  text-align: center;
  cursor: pointer;
  border-radius: 4px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.recommended-product-item:hover {
  transform: translateY(-3px); /* 悬停微动 */
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); /* 悬停阴影 */
  background-color: rgba(255, 255, 255, 0.5);
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: contain; /* 确保图片完整显示，不裁剪 */
  display: block;
  border-radius: 4px;
  background-color: #f5f5f5;
}

.product-title {
  margin-top: 5px;
  font-size: 0.85em;
  color: #333;
  height: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

.product-price {
  margin-top: 5px;
  font-size: 1.1em;
  color: #ed115d; /* 价格颜色 */
  font-weight: bold;
}
</style>