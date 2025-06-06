<template>
  <div class="category-sidebar-wrapper">
    <div class="category-header">
      <h3>商品分类</h3>
    </div>
    <ul class="category-list">
      <li v-for="category in categories" :key="category.id" class="category-item">
        <div @click="navigateToCategory(category.code)" class="category-link">
          {{ category.name }}
          <i class="el-icon-arrow-right arrow-icon"></i>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 使用产品数据中的实际分类
const categories = ref([
  { id: '1', name: '显卡', code: 'VIDEOCARD' },
  { id: '2', name: '处理器', code: 'CPU' },
  { id: '3', name: '主板', code: 'MOTHERBOARD' },
  { id: '4', name: '内存', code: 'RAM' },
  { id: '5', name: '存储设备', code: 'STORAGE' },
  { id: '6', name: '电源', code: 'POWERSUPPLY' },
  { id: '7', name: '机箱', code: 'CASE' },
  { id: '8', name: '散热器', code: 'COOLING' },
  { id: '9', name: '显示器', code: 'MONITOR' },
  { id: '10', name: '外设', code: 'PERIPHERALS' },
]);

// 导航到分类页面的函数
const navigateToCategory = (categoryCode) => {
  router.push({
    path: '/products',
    query: { category: categoryCode }
  });
};
</script>

<style scoped>
.category-sidebar-wrapper {
  width: 220px; /* 分类栏的固定宽度 */
  background-color: rgb(245, 246, 250);
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
  overflow: hidden;
}

.category-header {
  background-color: rgba(201, 210, 228, 0);
  color: #000205;
  padding: 0px 22px;
  font-size: 13px;
  font-weight: bold;
}

.category-list {
  list-style: none; /* 移除列表默认样式 */
  padding-left: 10px;
  margin: 0;
}

.category-item:last-child {
  padding-bottom: 10px;
}

.category-link {
  display: flex; /* 使用 flexbox 布局内容和箭头 */
  justify-content: space-between; /* 内容和箭头分别在两端 */
  align-items: center; /* 垂直居中对齐 */
  padding: 6px 15px;
  color: #333;
  text-decoration: none;
  transition: background-color 0.2s ease, color 0.2s ease;
  font-size: 0.95em;
}

.category-link:hover {
  background-color: rgba(179, 205, 221, 0.3); /* 悬停背景色 */
  color: #05bcff; /* 悬停文字颜色 */
  text-decoration: underline;
}

.arrow-icon {
  font-size: 0.9em;
  color: #999;
}

.category-link:hover .arrow-icon {
  color: #07556a; /* 悬停时箭头颜色也改变 */
}
</style>