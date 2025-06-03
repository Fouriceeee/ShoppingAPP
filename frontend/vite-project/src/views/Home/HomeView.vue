<template>
  <div class="home-page">
    <top-nav />
    <div class="page-container">
      <div class="home-top-wrapper">
        <div class="home-top-1-wrapper">
          <CategorySidebar class="home-category-sidebar" />
        </div>

        <div class="home-top-2-wrapper">
          <ImageCarousel class="home-image-carousel" />
        </div>

        <div class="home-top-3-wrapper">
          <div class="home-top-3-top-section">
            <RecommendedProducts class="home-recommended-products" />
          </div>
          <div class="home-top-3-bottom-section"></div>
        </div>

        <div class="home-top-4-wrapper">
          <login-bar class="home-login-bar" />
        </div>
      </div>

      <main class="home-main-content-wrapper">
        <h1>首页</h1>
        <div v-if="isLoadingProducts" style="text-align: center; padding: 20px;">
          加载产品中...
        </div>
        <div v-else-if="productLoadError" style="text-align: center; padding: 20px; color: red;">
          加载产品失败：{{ productLoadError.message }}
        </div>
        <div v-else class="product-wrapper">
          <ProductCard
              v-for="product in products"
              :key="product.id"
              :product="product"
              @add-to-cart="handleAddToCart"
              :is-adding="productAddingStates[product.id]" />
        </div>

        <div style="height: 1000px; padding: 20px;">
          <p>滚动内容测试...</p>
          <p>更多内容...</p>
          <p>更多内容...</p>
          <p>更多内容...</p>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus'; // 导入 Element Plus 的 Message 组件

// 导入你的组件
import topNav from '../../components/topNav.vue'
import ProductCard from "@/components/productCard.vue";
import CategorySidebar from "@/components/CategorySidebar.vue";
import ImageCarousel from "@/components/ImageCarousel.vue";
import RecommendedProducts from "@/components/RecommendedProducts.vue";
import LoginBar from "@/components/loginBar.vue";

// 导入 API
import { getProducts } from '@/api/products'; //  products.js 导入
import { addCartItem, getCartItems } from '@/api/cart'; // 从 cart.js 导入 addCartItem 和 getCartItems

// 产品列表数据
const products = ref([]);
const isLoadingProducts = ref(true);
const productLoadError = ref(null);

// 购物车数据 (这里保留一份本地同步，但主要以后端为准)
const cartItems = ref([]); // 可以在需要时，通过 getCartItems 从后端获取最新购物车数据

// 用于管理每个商品“添加到购物车”按钮的加载状态
const productAddingStates = ref({}); // { productId: true/false }

// 在组件挂载后加载产品数据
onMounted(async () => {
  // 加载产品数据
  try {
    isLoadingProducts.value = true;
    const response = await getProducts();
    products.value = response.data; // 假设后端直接返回产品数组
    console.log('Home: 从后端加载产品成功:', products.value);
  } catch (error) {
    productLoadError.value = error;
    console.error("Home: 加载产品失败:", error);
    ElMessage.error('加载产品列表失败，请稍后重试！');
  } finally {
    isLoadingProducts.value = false;
  }
});

// 处理 ProductCard 触发的 'add-to-cart' 事件
const handleAddToCart = async (productToAdd) => {
  // 检查是否正在添加，防止重复点击
  if (productAddingStates.value[productToAdd.id]) {
    return;
  }

  // 设置当前商品的添加状态为 true
  productAddingStates.value = {
    ...productAddingStates.value,
    [productToAdd.id]: true
  };

  console.log('2. HomePage: 接收到 ProductCard 的添加购物车事件，商品ID:', productToAdd.id);

  try {
    // 调用后端 API 添加商品到购物车
    // 假设后端 addCartItem 期望的参数是 { productId: ..., quantity: 1 }
    const itemData = {
      productId: productToAdd.id,
      quantity: 1, // 默认添加数量为 1
      // 如果后端需要其他信息（如价格、规格等），可以根据后端 API 契约添加
      // 例如：price: parseFloat(`${productToAdd.priceInteger}.${productToAdd.priceDecimal}`)
    };
    const response = await addCartItem(itemData);
    console.log('后端添加购物车成功响应:', response.data);

    // 弹窗提示
    ElMessage({
      message: `${productToAdd.title} 已成功加入购物车！`,
      type: 'success',
      duration: 2000,
      customClass: 'blue-message',
    });

    // 这里可以根据需要刷新购物车数据，例如在购物车页面或顶部购物车图标处更新数量
    // 如果 HomePage 不直接展示购物车内容，则不需要在这里刷新 cartItems.value

  } catch (error) {
    console.error('添加到购物车失败:', error);
    ElMessage.error(`添加 ${productToAdd.title} 到购物车失败，请重试！`);
  } finally {
    // 无论成功失败，都解除当前商品的添加状态
    productAddingStates.value = {
      ...productAddingStates.value,
      [productToAdd.id]: false
    };
  }
};
</script>

<style scoped>
/* 样式保持不变 */
.home-page {
  background-color: #f0f2f5;
  padding: 0;
}

/* 页面主容器 */
.page-container {
  padding: 20px 50px;
  margin: 0 auto;
  max-width: 1500px; /* 设置最大宽度 */
  min-width: 1140px;
  box-sizing: border-box;
}

/* 主页顶部区域 */
.home-top-wrapper {
  padding: 20px;
  margin: 0;
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  gap: 0px
}

/*主页顶部1区域*/
.home-top-1-wrapper {
  margin-right: 10px;
}

/*主页顶部2区域*/
.home-top-2-wrapper {
  width: 100%;
  margin-right: 10px;
  margin-left: 10px;
}

/*主页顶部3区域*/
.home-top-3-wrapper {
  width: 100%;
  margin-right: 10px;
  margin-left: 10px;
}

/*3区域的上半部分*/
.home-top-3-top-section {
  margin-bottom: 10px;
}

/*3区域的下半部分*/
.home-top-3-bottom-section {
  margin-top: 10px;
}

/*主页顶部4区域*/
.home-top-4-wrapper {
  padding-left: 10px;
}

/*页面主要部分*/
.home-main-content-wrapper {
  background-color: #fff;
  border-radius: 10px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.product-wrapper {
  padding-left: 30px;
  padding-right: 20px;
  display: grid;
  /* 将这里从 repeat(4, 300px) 或 repeat(auto-fit, minmax(200px, 1fr)) 修改为以下 */
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  row-gap: 50px;    /* 上下间距 */
  column-gap: 30px; /* 左右间距 */
  justify-content: center;
}
</style>