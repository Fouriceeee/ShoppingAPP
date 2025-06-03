<template>
  <div class="home-page">
    <top-nav /> <div class="page-container"> <div class="home-top-wrapper"> <div class="home-top-1-wrapper"> <CategorySidebar class="home-category-sidebar" /> </div>

    <div class="home-top-2-wrapper"> <ImageCarousel class="home-image-carousel" /> </div>

    <div class="home-top-3-wrapper"> <div class="home-top-3-top-section"> <RecommendedProducts class="home-recommended-products" /> </div>
      <div class="home-top-3-bottom-section"> </div>
    </div>

    <div class="home-top-4-wrapper"> <login-bar class="home-login-bar" /> </div>
  </div>

    <main class="home-main-content-wrapper"> <h1>首页</h1>
      <div class="product-wrapper"> <ProductCard
          v-for="product in products"
          :key="product.id"
          :product="product"
          @add-to-cart="handleAddToCart"
      />
      </div>

      <div style="height: 1000px; padding: 20px;"> <p>滚动内容测试...</p>
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

// 导入你的 JSON 数据
import productsData from "@/data/products.json"; // 确保路径正确

// 产品列表数据
const products = ref([]);
// 购物车数据
const cartItems = ref([]);

// 在组件挂载后加载产品数据
onMounted(() => {
  products.value = productsData;

  const savedCart = localStorage.getItem('shoppingCart');
  if (savedCart) {
    try {
      cartItems.value = JSON.parse(savedCart);
      console.log('Home: 从 localStorage 加载购物车成功:', cartItems.value);
    } catch (e) {
      console.error("Home: 解析购物车数据失败:", e);
      localStorage.removeItem('shoppingCart');
      cartItems.value = [];
    }
  }
});

// 处理 ProductCard 触发的 'add-to-cart' 事件
const handleAddToCart = (productToAdd) => {
  // 查找购物车中是否已有该产品
  const existingItem = cartItems.value.find(item => item.id === productToAdd.id);

  if (existingItem) {
    // 如果已存在，则增加数量
    existingItem.quantity++;
  } else {
    // 如果不存在，则添加到购物车，数量为 1
    cartItems.value.push({ ...productToAdd, quantity: 1 });
  }

  // 将购物车数据保存到 localStorage (模拟持久化)
  localStorage.setItem('shoppingCart', JSON.stringify(cartItems.value));

  // 弹窗提示
  ElMessage({
    message: `${productToAdd.title} 已成功加入购物车！`,
    type: 'success', // 使用 Element Plus 提供的 success 类型
    duration: 2000, // 2秒后自动关闭
    customClass: 'blue-message', // 自定义 CSS 类名，用于修改颜色
  });

  console.log('当前购物车内容:', cartItems.value);
};
</script>

<style scoped>
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