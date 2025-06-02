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
// 导入所有需要的组件
import topNav from '../../components/topNav.vue' // 假设路径正确
import ProductCard from "@/components/productCard.vue";
import CategorySidebar from "@/components/CategorySidebar.vue";
import ImageCarousel from "@/components/ImageCarousel.vue";
import RecommendedProducts from "@/components/RecommendedProducts.vue";
import LoginBar from "@/components/loginBar.vue";

// 导入 Vue 核心 API
import { ref, onMounted } from 'vue';

// 导入你的 JSON 数据
import productsData from "@/data/products.json"; // 确保路径正确

// 定义响应式产品数据
const products = ref([]);

// 在组件挂载后加载数据
onMounted(() => {
  products.value = productsData;
});

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