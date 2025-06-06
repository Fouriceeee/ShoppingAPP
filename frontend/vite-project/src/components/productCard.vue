<template>
  <el-card class="product-card" >
    <div class="product-image">
      <img :src="productImage" :alt="product.title">
    </div>
    <div class="product-info">
      <h3 class="product-title">{{ product.title }}</h3>
      <div class="product-price">
        <span>¥</span>
        <span class="price-integer">{{ product.priceInteger }}</span>
        <span class="price-decimal">.{{ product.priceDecimal }}</span>
      </div>
      <el-button
          type="primary"
          class="add-to-cart-button"
          @click="addToCart"
          :disabled="isAdding"
      >
        <img class="cart-for-productCard-icon" src="../assets/icons/cart-for-product-card.png" alt="">
        {{ isAdding ? '添加中...' : '加入购物车' }}
      </el-button>
    </div>
  </el-card>
</template>

<script setup>
import { defineProps, defineEmits, computed, ref } from 'vue';

// 定义 ProductCard 组件接受的 props
const props = defineProps({
  product: {
    type: Object,
    required: true,
    default: () => ({
      id: '',
      image: '',
      title: '',
      priceInteger: '',
      priceDecimal: ''
    })
  }
});

// 定义组件可以触发的事件
const emit = defineEmits(['add-to-cart']);

// 计算图片路径 - 修改为处理两种图片来源
const productImage = computed(() => {
  if (!props.product.image) {
    // 如果没有图片，返回默认图片
    return new URL('../assets/pictures/products/default-product.jpg', import.meta.url).href;
  }

  // 处理后端返回的图片路径
  if (props.product.image.startsWith('/images/')) {
    const baseUrl = 'http://localhost:8080';
    // 去掉开头的 "./"
    return `${baseUrl}/${props.product.image.substring(1)}`;
  }
});


// 加入购物车处理函数
const addToCart = () => {
  console.log('1. ProductCard: "加入购物车" 按钮被点击了！');
  emit('add-to-cart', props.product);
};
</script>

<style scoped>
.product-card {
  padding: 0px;
  --el-card-padding: 15px;
  margin: 0px;
  background-color: #ffffff;
  border: none;
  border-radius: 30px;
  width: 100%;
  max-width: 300px;
  min-width: 200px;
  aspect-ratio: 6 / 7.8; /*控制横纵比为6:7*/
  box-sizing: border-box;
  display: flex;

  /* Apply the custom box-shadows */
  box-shadow:
      10px 10px 30px 0px rgba(0, 0, 0, 0.7),   /* Dark shadow */
      -5px -5px 30px 0px rgba(255, 255, 255, 0.1); /* Light shadow */

  /* Optional: Add transition for hover effects */
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow:
      30px 30px 80px 0px rgba(0, 0, 0, 1),
      -30px -30px 80px 0px rgba(255, 255, 255, 0.18);
}

:deep(.el-card__body) { /*并非从未使用。IDE解析错了*/
  width: 100%;
}

.product-image {
  width: 100%;
  height: 65%;
  display: flex;
  align-items: center; /* 垂直居中子元素 */
  justify-content: center;
}

.product-image img {
  width: auto;
  height: auto;
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 8px; /* Slightly rounded corners for the image */
  margin-bottom: 15px;
  display: block; /* Remove extra space below image */
}

.product-info {
  margin: 0;
  background-color: #ffffff;
  border-radius: 10px;
  padding: 15px;
}

.product-title {
  font-size: 1.2em;
  margin-top: 0;
  margin-bottom: 2px;
  color: #000205; /* Slightly brighter for title */
}

.product-price {
  font-size: 2.0em;
  font-weight: bold;
  margin-bottom: 15px;
  color: #000205; /* Element Plus primary color for price */

  display: flex; /* 使用 Flexbox 进行内部布局 */
  align-items: baseline; /* 关键：使不同字号的文本基于基线对齐 */
  line-height: 1; /* 减少价格行高，使垂直对齐更紧凑 */
}

/* 货币符号样式 */
.product-price span:first-child { /* 针对 '¥' 符号 */
  font-size: 0.7em; /* 比整数部分小一些 */
  margin-right: 2px; /* 与整数部分之间的间距 */
}

/* 整数部分样式 */
.product-price .price-integer {
  font-size: 1em; /* 继承 .product-price 的 2.0em，所以是最大的 */
  /* 可以选择不设置 color，继承父级 */
  color: #ed115d; /* 整数部分使用较深的颜色，如果与父级颜色不同的话 */
}

/* 小数部分样式 */
.product-price .price-decimal {
  font-size: 0.6em; /* 关键：相对于父级 .product-price 的字号缩小，例如 0.6 * 2.0em = 1.2em */
  margin-left: 0px; /* 与整数部分紧密连接 */
  color: #ed115d; /* 小数部分可以颜色稍浅 */
}

.add-to-cart-button {
  background-color: #7852f5;
  border: none;
  width: 80%;
  padding-left: 0px;
  padding-right: 5px;
  margin: 0 15px;
}

.add-to-cart-button:active {
  background-color: #4d36a5; /* 稍微深一点的紫色，表示点击反馈 */
  transform: scale(95%);
}
.add-to-cart-button:hover {
  background-color: #4d36a5;
}

.cart-for-productCard-icon {
  width: 20px;
  height: 20px;
  margin-right: 15px;
}
</style>