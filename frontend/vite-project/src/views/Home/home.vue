<template>
  <div class="home-page">
    <top-nav /> <!--顶部导航栏-->

    <div class="page-container"> <!--页面主容器-->
      <div class="home-top-wrapper"> <!--主页顶部区域-->
        <div class="home-top-1-wrapper"> <!--主页顶部(从左向右数）1区域-->
          <CategorySidebar class="home-category-sidebar" /> <!--放商品分类-->
        </div>

        <div class="home-top-2-wrapper"> <!--主页顶部2区域-->
          <ImageCarousel class="home-image-carousel" /> <!--放轮播图片-->
        </div>

        <div class="home-top-3-wrapper"> <!--主页顶部3区域-->
          <div class="home-top-3-top-section"> <!--3区域的上半部分-->
            <RecommendedProducts class="home-recommended-products" /> <!--放为你推荐-->
          </div>
          <div class="home-top-3-bottom-section"> <!--3区域的下半部分-->

          </div>
        </div>

        <div class="home-top-4-wrapper"> <!--主页面顶部4区域-->
          <login-bar class="home-login-bar" /> <!--放登录模块-->
        </div>
      </div>

      <main class="home-main-content-wrapper"> <!--页面主要区域-->
        <h1>首页</h1>
        <div class="product-wrapper"> <!--商品卡部分-->
          <ProductCard />
          <ProductCard />
          <ProductCard />
          <ProductCard />
          <ProductCard />
          <ProductCard />
          <ProductCard />
          <ProductCard />
        </div>

        <div style="height: 1000px; padding: 20px;"> <!--滚动内容测试-->
          <p>滚动内容测试...</p>
          <p>更多内容...</p>
          <p>更多内容...</p>
          <p>更多内容...</p>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import topNav from './topNav.vue'
import ProductCard from "@/views/Products/productCard.vue";
import CategorySidebar from "@/views/Home/CategorySidebar.vue";
import ImageCarousel from "@/views/Home/ImageCarousel.vue";
import RecommendedProducts from "@/views/Home/RecommendedProducts.vue";
import LoginBar from "@/views/Home/loginBar.vue";

export default {
  name: 'Home',
  components: {
    LoginBar,
    RecommendedProducts,
    ImageCarousel,
    CategorySidebar,
    ProductCard,
    topNav
  }
}
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