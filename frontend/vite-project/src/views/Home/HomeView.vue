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
import productsData from "@/data/products.json";
import {addCartItem} from "@/api/cart.js"; // 确保路径正确

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

const handleAddToCart = async (productToAdd) => {

  console.log('2. HomePage: 接收到 ProductCard 的添加购物车事件，商品ID:', productToAdd.id);

  try {
    // 后端会根据 productId 从其 products.json 中获取完整信息来创建 CartItem
    // 所以前端只需传递 productId 和 quantity (默认为1)
    const itemData = {
      id: productToAdd.id, // 传递产品ID
      quantity: 1,         // 默认添加数量为 1
    };

    // 调用后端 API 添加商品到购物车
    const response = await addCartItem(itemData);

    // 检查后端响应
    if (response.data && response.data.code === 200) {
      console.log('后端添加购物车成功响应:', response.data.message, response.data.data);
      // 后端返回的 data 可能是更新后的 CartItem 或整个购物车列表
      // 这里可以根据需要更新前端的 cartItems 状态，
      // 但对于主页，通常只需要显示成功消息
      ElMessage({
        message: `${productToAdd.title} 已成功加入购物车！`,
        type: 'success',
        duration: 2000,
        customClass: 'blue-message',
      });
    } else {
      // 如果后端返回的 code 不是 200，表示操作失败
      throw new Error(response.data.message || '添加商品到购物车失败');
    }

  } catch (error) {
    console.error('添加到购物车失败:', error);
    ElMessage.error(`添加 ${productToAdd.title} 到购物车失败，请重试！`);
  }
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