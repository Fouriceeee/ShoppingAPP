<template>
  <div class="image-carousel-container"
       @mouseenter="showArrows = true"
       @mouseleave="showArrows = false"
  >
    <div class="carousel-wrapper">
      <div class="carousel-track" :style="trackStyle">
        <div v-for="(image, index) in images" :key="index" class="carousel-slide">
          <img :src="image.url" :alt="image.alt" class="carousel-image">
        </div>
      </div>

      <transition name="fade">
        <el-button v-if="showArrows" class="carousel-arrow left-arrow" @click="prevImage">
          <el-icon><arrow-left /></el-icon>
        </el-button>
      </transition>
      <transition name="fade">
        <el-button v-if="showArrows" class="carousel-arrow right-arrow" @click="nextImage">
          <el-icon><arrow-right /></el-icon>
        </el-button>
      </transition>
    </div>

    <div class="carousel-dots">
      <span
          v-for="(image, index) in images"
          :key="index"
          class="dot"
          :class="{ 'active': index === currentIndex }"
          @click="goToImage(index)"
      ></span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import cpuImage from '@/assets/pictures/imageCarousel/CPU.png';
import gpuImage from '@/assets/pictures/imageCarousel/GPU.png';
import memoryImage from '@/assets/pictures/imageCarousel/memory.png';
import motherboardImage from '@/assets/pictures/imageCarousel/motherboard.png';
import {ArrowLeft, ArrowRight} from "@element-plus/icons-vue";

const images = ref([
  { url: cpuImage, alt: 'Image 1' },
  { url: gpuImage, alt: 'Image 2' },
  { url: memoryImage, alt: 'Image 3' },
  { url: motherboardImage, alt: 'Image 4' },
]);

const currentIndex = ref(0);
const showArrows = ref(false); // 控制箭头的显示隐藏
let intervalId = null; // 用于自动轮播的定时器ID

// 计算 track 的 transform 样式，实现图片滑动效果
const trackStyle = computed(() => ({
  transform: `translateX(-${currentIndex.value * 100}%)`,
  transition: 'transform 0.5s ease-in-out',
}));

// 切换到上一张图片
const prevImage = () => {
  currentIndex.value = (currentIndex.value - 1 + images.value.length) % images.value.length;
  resetAutoPlay();
};

// 切换到下一张图片
const nextImage = () => {
  currentIndex.value = (currentIndex.value + 1) % images.value.length;
  resetAutoPlay();
};

// 切换到指定图片
const goToImage = (index) => {
  currentIndex.value = index;
  resetAutoPlay();
};

// 启动自动轮播
const startAutoPlay = () => {
  intervalId = setInterval(() => {
    nextImage();
  }, 3000); // 每 3 秒切换一次
};

// 重置自动轮播（当手动切换或点击圆点时）
const resetAutoPlay = () => {
  clearInterval(intervalId);
  startAutoPlay();
};

// 组件挂载时启动自动轮播
onMounted(() => {
  startAutoPlay();
});

// 组件卸载时清除定时器
onUnmounted(() => {
  clearInterval(intervalId);
});
</script>

<style scoped>
.image-carousel-container {
  width: 100%; /* 轮播图容器宽度自适应父容器 */
  min-width: 200px;
  max-width: 512px;
  margin: 0px auto;
  position: relative;
  overflow: hidden;
  border-radius: 8px; /* 圆角 */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.carousel-wrapper {
  display: flex;
  width: 100%;
  position: relative;
  height: 200px; /* 固定轮播图高度 */
  overflow: hidden; /* 确保滑动时只显示一张图片 */
}

.carousel-track {
  display: flex;
  height: 100%;
  width: 400%; /* 四张图片，每张 100%，总宽 400% */
}

.carousel-slide {
  flex-shrink: 0; /* 防止图片收缩 */
  width: 100%; /* 每张图片占据 carousel-wrapper 的 100% 宽度 */
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片覆盖整个区域，可能裁剪 */
  display: block;
}

/* 箭头按钮样式 */
.carousel-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.3); /* 半透明背景 */
  color: #fff;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
  font-size: 24px;
  border-radius: 4px;
  z-index: 10; /* 确保在图片上方 */
  opacity: 1; /* 初始显示 */
  transition: background-color 0.3s ease, opacity 0.3s ease;
}

.carousel-arrow:hover {
  background-color: rgba(0, 0, 0, 0.5);
}

.left-arrow {
  left: 10px;
}

.right-arrow {
  right: 10px;
}

/* 箭头显示/隐藏的过渡效果 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}


/* 圆点指示器样式 */
.carousel-dots {
  position: absolute;
  bottom: 15px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px; /* 圆点之间的间距 */
}

.dot {
  width: 10px;
  height: 10px;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  cursor: pointer;
  transition: width 0.3s ease, background-color 0.3s ease;
}

.dot.active {
  width: 25px; /* 当前圆点变长 */
  border-radius: 5px; /* 变长后变成椭圆 */
  background-color: #0c4781; /* 活跃圆点的颜色 */
}
</style>