<template>
  <div class="recommended-products-wrapper" ref="moduleRef">
    <div class="header-wrapper">
      <h3>为你推荐</h3>
      <a href="#" class="more-link">查看更多 ></a>
    </div>
    <div class="products-container">
      <div v-for="product in displayedProducts" :key="product.id" class="recommended-product-item">
        <img :src="product.imageUrl" :alt="product.name" class="product-image">
        <div class="product-price">¥{{ product.price.toFixed(2) }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue';

// 示例推荐商品数据
const displayedProducts = ref([
  { id: 'rec1', name: '高性能固态硬盘', imageUrl: 'https://via.placeholder.com/120x120/FFDDC1/000000?text=SSD', price: 399.99 },
  { id: 'rec2', name: '电竞机械键盘', imageUrl: 'https://via.placeholder.com/120x120/C1FFDD/000000?text=Keyboard', price: 288.00 },
  { id: 'rec3', name: '27寸高刷显示器', imageUrl: 'https://via.placeholder.com/120x120/DDC1FF/000000?text=Monitor', price: 999.00 },
  { id: 'rec4', name: '无线游戏鼠标', imageUrl: 'https://via.placeholder.com/120x120/FFC1DD/000000?text=Mouse', price: 159.50 },
  { id: 'rec5', name: '内存条 16GB', imageUrl: 'https://via.placeholder.com/120x120/C1DDFF/000000?text=RAM', price: 189.00 },
  { id: 'rec6', name: '电脑散热器', imageUrl: 'https://via.placeholder.com/120x120/DDFFC1/000000?text=Cooler', price: 120.00 },
  { id: 'rec7', name: '机械硬盘 2TB', imageUrl: 'https://via.placeholder.com/120x120/FFDDFF/000000?text=HDD', price: 450.00 },
  { id: 'rec8', name: '游戏耳机', imageUrl: 'https://via.placeholder.com/120x120/DDF1FF/000000?text=Headset', price: 300.00 },
]);

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

.module-header h3 {
  margin: 0;
  font-size: 1.1em;
  color: #333;
}

.more-link {
  font-size: 0.9em;
  color: #0b78ea;
  text-decoration: none;
  transition: color 0.2s ease;
}

.more-link:hover {
  color: #0a407e;
}

.products-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(110px, 1fr));
  row-gap: 50px;    /* 上下间距 */
  column-gap: 0px; /* 左右间距 */
  transition: transform 0.3s ease; /* 如果将来需要滑动效果可以添加 */
}

.recommended-product-item {
  padding-top: 10px;
  flex-shrink: 0; /* 确保商品项在空间不足时不会收缩 */
  height: 140px;
  width: 110px; /* 单个商品的固定宽度 */
  text-align: center;
  cursor: pointer;
  border-radius: 4px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
}

.recommended-product-item:hover {
  transform: translateY(-3px); /* 悬停微动 */
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); /* 悬停阴影 */
}

.product-image {
  margin: 0 11px;
  width: 90px;
  height: 90px;
  object-fit: contain; /* 确保图片完整显示，不裁剪 */
  display: block;
  border-radius: 4px;
  background-color: #f5f5f5; /* 简单的背景色，防止图片是透明的 */
}

.product-price {
  margin-top: 10px;
  font-size: 1.1em;
  color: #0b78ea; /* 价格颜色 */
  font-weight: bold;
}
</style>