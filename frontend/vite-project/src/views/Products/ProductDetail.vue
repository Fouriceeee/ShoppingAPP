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

      <div v-else class="product-detail-wrapper">
        <!-- 导航路径 -->
        <div class="breadcrumb-container">
          <el-breadcrumb separator=">">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: '/products' }">商品列表</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: `/products?category=${product.category}` }">{{ formatCategory }}</el-breadcrumb-item>
            <el-breadcrumb-item>{{ product.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 商品详情内容 -->
        <div class="product-detail-container">
          <!-- 左侧：大图片展示区 -->
          <div class="product-gallery-section">
            <div class="main-image-container">
              <img :src="productImage" :alt="product.title" class="main-product-image">
            </div>
          </div>

          <!-- 右侧：商品信息区 -->
          <div class="product-info-section">
            <h1 class="product-title">{{ product.title }}</h1>

            <div class="product-rating">
              <el-rate v-model="rating" disabled show-score text-color="#ff9900" score-template="{value}" />
              <span class="review-count">({{ reviewCount }}条评价)</span>
            </div>

            <div class="product-price-section">
              <div class="price-value">
                <span class="price-symbol">¥</span>
                <span class="price-integer">{{ product.priceInteger }}</span>
                <span class="price-decimal">.{{ product.priceDecimal }}</span>
              </div>
              <div class="price-original" v-if="originalPrice">
                <span>原价: ¥{{ originalPrice }}</span>
              </div>
            </div>

<!--            <div class="product-availability">
              <el-tag type="success" effect="plain" v-if="product.stock > 0">有库存</el-tag>
              <el-tag type="danger" effect="plain" v-else>缺货</el-tag>
            </div>-->

            <div class="product-actions">
              <div class="quantity-selector">
                <span class="quantity-label">数量:</span>
                <el-input-number v-model="quantity" :min="1" :max="99" size="large" :disabled="product.stock <= 0" />
              </div>

              <div class="action-buttons">
                <el-button type="primary" size="large" class="add-to-cart-button" @click="addToCart" :disabled="product.stock <= 0">
                  <el-icon><ShoppingCart /></el-icon>
                  加入购物车
                </el-button>
                <el-button size="large" class="buy-now-button" type="danger" @click="buyNow" :disabled="product.stock <= 0">
                  领券购买
                </el-button>
              </div>
            </div>

            <div class="product-specs">
              <h3>规格参数</h3>
              <el-descriptions :column="1" border>
                <el-descriptions-item v-for="(spec, index) in productSpecs" :key="index" :label="spec.name">
                  {{ spec.value }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </div>

        <!-- 商品详情和描述 -->
        <div class="product-details-tabs">
          <el-tabs>
            <el-tab-pane label="商品详情">
              <div class="product-description">
                <h3>商品描述</h3>
                <p>{{ product.description }}</p>
                <div class="description-images" v-if="descriptionImages.length > 0">
                  <img v-for="(img, index) in descriptionImages" :key="index" :src="img" :alt="`${product.title} 详情图 ${index+1}`" class="description-image">
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="用户评价">
              <div class="product-reviews">
                <h3>用户评价</h3>
                <p v-if="!hasReviews">暂无评价</p>
                <div v-else class="reviews-list">
                  <!-- 这里将来可以添加评价列表 -->
                  <p>暂无实际评价数据</p>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- 推荐商品 -->
        <div class="recommended-products">
          <h3>相关推荐</h3>
          <div class="recommendations-container">
            <p>暂无相关推荐数据</p>
            <!-- 这里将来可以添加推荐商品列表 -->
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
const rating = ref(4.5); // 示例评分
const reviewCount = ref(24); // 示例评价数量
const hasReviews = ref(false); // 是否有评价
const originalPrice = ref(null); // 原价

// 示例描述图片数组
const descriptionImages = ref([]);

// 产品图片地址
const productImage = computed(() => {
  if (!product.value.image) {
    // 如果没有图片，返回默认图片
    return new URL('../../assets/pictures/products/default-product.jpg', import.meta.url).href;
  }

  // 处理后端返回的图片路径
  if (product.value.image.startsWith('/images/')) {
    const baseUrl = 'http://localhost:8080';
    return `${baseUrl}${product.value.image}`;
  }

  return product.value.image;
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

// 商品规格参数
const productSpecs = computed(() => {
  if (!product.value.id) return [];

  // 根据产品类别返回不同的规格参数
  switch(product.value.category) {
    case 'CPU':
      return [
        { name: '品牌', value: product.value.brand || '未知' },
        { name: '型号', value: product.value.model || '未知' },
        { name: '核心数', value: product.value.cores || '未知' },
        { name: '线程数', value: product.value.threads || '未知' },
        { name: '基础频率', value: product.value.baseFrequency ? `${product.value.baseFrequency} GHz` : '未知' },
        { name: '最大加速频率', value: product.value.boostFrequency ? `${product.value.boostFrequency} GHz` : '未知' },
        { name: '缓存', value: product.value.cache ? `${product.value.cache} MB` : '未知' },
        { name: '接口', value: product.value.socket || '未知' },
      ];
    case 'VIDEOCARD':
      return [
        { name: '品牌', value: product.value.brand || '未知' },
        { name: '型号', value: product.value.model || '未知' },
        { name: '显存', value: product.value.vram ? `${product.value.vram} GB` : '未知' },
        { name: '接口类型', value: product.value.interface || '未知' },
        { name: '核心频率', value: product.value.coreFrequency ? `${product.value.coreFrequency} MHz` : '未知' },
      ];
    default:
      // 默认规格参数
      return [
        { name: '品牌', value: product.value.brand || '未知' },
        { name: '型号', value: product.value.model || '未知' },
        { name: '分类', value: formatCategory.value },
        { name: '库存状态', value: product.value.stock > 0 ? '有库存' : '缺货' },
      ];
  }
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

      // 动态设置页面标题为商品名称
      document.title = `${product.value.title} - 商品详情 - 易猫商城`


      // 设置示例原价（比实际价格高10-30%）
      const actualPrice = parseFloat(`${product.value.priceInteger}.${product.value.priceDecimal || '00'}`);
      const markup = Math.random() * 0.2 + 0.1; // 10-30%的加价
      originalPrice.value = (actualPrice * (1 + markup)).toFixed(2);

      // 随机设置是否有评价
      hasReviews.value = Math.random() > 0.5;

      // 设置详情图片 - 使用相同图片但可以在实际项目中替换
      if (productImage.value) {
        // 添加2-4张详情图片（实际项目中这些会来自后端）
        const numImages = Math.floor(Math.random() * 3) + 2;
        descriptionImages.value = Array(numImages).fill(productImage.value);
      }
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

// 立即购买
const buyNow = async () => {
  try {
    // 先添加到购物车
    const itemData = {
      id: product.value.id,
      quantity: quantity.value,
    };

    const response = await addCartItem(itemData);

    if (response.data && response.data.code === 200) {
      // 添加成功后直接跳转到购物车页面
      router.push('/cart');
    } else {
      throw new Error(response.data.message || '添加商品到购物车失败');
    }
  } catch (error) {
    console.error('立即购买失败:', error);
    ElMessage.error(`操作失败，请重试！`);
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
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.loading-container,
.error-container {
  margin: 40px 0;
  display: flex;
  justify-content: center;
}

/* 面包屑导航 */
.breadcrumb-container {
  margin-bottom: 20px;
  padding: 10px 0;
}

/* 产品详情容器 */
.product-detail-wrapper {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 30px;
  margin-bottom: 30px;
}

/* 产品详情主体部分 */
.product-detail-container {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
}

/* 产品图片展示区 */
.product-gallery-section {
  flex: 1;
  max-width: 60%;
}

.main-image-container {
  width: 100%;
  height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(120, 82, 245, 0.1);
}

.main-product-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

/* 产品信息区 */
.product-info-section {
  flex: 1;
  max-width: 40%;
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.product-title {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.3;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 10px;
}

.review-count {
  color: #666;
  font-size: 14px;
}

.product-price-section {
  margin: 20px 0;
  padding: 15px;
  background-color: rgba(120, 82, 245, 0.05);
  border-radius: 8px;
}

.price-value {
  display: flex;
  align-items: baseline;
  font-weight: bold;
}

.price-symbol {
  font-size: 20px;
  color: #ed115d;
}

.price-integer {
  font-size: 36px;
  color: #ed115d;
}

.price-decimal {
  font-size: 20px;
  color: #ed115d;
}

.price-original {
  margin-top: 5px;
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.product-availability {
  margin: 10px 0;
}

/* 产品操作区 */
.product-actions {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin: 20px 0;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.quantity-label {
  font-size: 16px;
  color: #333;
}

.action-buttons {
  display: flex;
  gap: 15px;
  width: 100%;
}

.add-to-cart-button {
  flex: 1;
  background-color: #7852f5;
  border: none;
  border-radius: 10px;
  height: 50px;
  font-size: 16px;
}

.add-to-cart-button:hover {
  background-color: #4d36a5;
}

.add-to-cart-button:active {
  background-color: #4d36a5; /* 稍微深一点的紫色，表示点击反馈 */
  transform: scale(95%);
}

.buy-now-button {
  border-radius: 10px;
  flex: 1;
  height: 50px;
  font-size: 16px;
}

/* 产品规格 */
.product-specs {
  margin-top: 20px;
}

.product-specs h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
}

/* 详情选项卡 */
.product-details-tabs {
  margin: 40px 0;
}

/* 商品描述 */
.product-description h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 15px;
}

.product-description p {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 20px;
}

.description-images {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.description-image {
  max-width: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 用户评价 */
.product-reviews h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 15px;
}

/* 推荐商品 */
.recommended-products {
  margin-top: 40px;
}

.recommended-products h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
}

.recommendations-container {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding: 10px 0;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .product-detail-container {
    flex-direction: column;
  }

  .product-gallery-section,
  .product-info-section {
    max-width: 100%;
  }

  .main-image-container {
    height: 400px;
  }
}

@media (max-width: 768px) {
  .main-image-container {
    height: 300px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>
