<template>
  <div class="category-sidebar-wrapper">
    <div class="category-header">
      <h3>商品分类</h3>
    </div>
    <ul class="category-list">
      <li v-for="category in categories" :key="category.id" class="category-item">
        <div class="category-link-group">
          <template v-if="Array.isArray(category.codes)">
            <span v-for="(sub, index) in category.codes" :key="sub.code" class="sub-category-item">
              <a @click="navigateToCategory(sub.code)" class="inner-category-link">
                {{ sub.name }}
              </a>
              <span v-if="index < category.codes.length - 1" class="separator">/</span>
            </span>
          </template>
          <template v-else>
            <a @click="navigateToCategory(category.code)" class="category-link single-link">
              {{ category.name }}
              <i class="el-icon-arrow-right arrow-icon"></i>
            </a>
          </template>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 修改 categories 数据结构以支持同一行内的多个链接
const categories = ref([
  { id: '1', name: '显卡', code: 'VIDEOCARD' },
  { id: '2', name: '处理器', code: 'CPU' },
  { id: '3', name: '主板', code: 'MOTHERBOARD' },
  { id: '4', name: '内存', code: 'RAM' },
  { id: '5', name: '硬盘', code: 'STORAGE' },
  {
    id: '6',
    name: '电源/散热器', // 这里的 name 用于显示，实际点击通过 codes
    codes: [
      { name: '电源', code: 'POWERSUPPLY' },
      { name: '散热器', code: 'COOLING' }
    ]
  },
  {
    id: '7',
    name: '机箱/外设/数位板', // 这里的 name 用于显示，实际点击通过 codes
    codes: [
      { name: '机箱', code: 'CASE' },
      { name: '外设', code: 'PERIPHERALS' },
      { name: '数位板', code: 'GRAPHIC_TABLET' }
    ]
  },
  { id: '8', name: '平板电脑', code: 'PAD' },
  { id: '9', name: '显示器', code: 'MONITOR' },
  { id: '10', name: '笔记本', code: 'LAPTOP' },
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

.category-link-group {
  display: flex; /* 保持 flex 布局，确保内部元素在同一行 */
  align-items: center; /* 垂直居中对齐 */
  flex-wrap: wrap; /* 允许换行，以防内容过长 */
  /* 将 padding 从 .inner-category-link 移动到 .category-link-group */
  padding: 6px 15px; /* 将父元素的 padding 设为链接的高度 */
  font-size: 0.95em;
  color: #333;
  /* 确保背景色和过渡效果在这里 */
  transition: background-color 0.2s ease;
}

.category-link, .inner-category-link {
  justify-content: space-between; /* 内容和箭头分别在两端 */
  align-items: center; /* 垂直居中对齐 */
  color: #333;
  text-decoration: none;
  transition: background-color 0.2s ease, color 0.2s ease;
  font-size: 0.95em;
}

.category-link {
  display: flex; /* 使用 flexbox 布局内容和箭头 */
}

.inner-category-link {
  justify-content: space-between; /* 内容和箭头分别在两端 */
  align-items: center; /* 垂直居中对齐 */
  color: #333;
  text-decoration: none;
  transition: background-color 0.2s ease, color 0.2s ease;
  font-size: 0.95em;
}

.category-link:hover {
  background-color: rgba(179, 205, 221, 0.3); /* 悬停背景色 */
  color: #05bcff; /* 悬停文字颜色 */
  text-decoration: underline;
  cursor: pointer;
}

.inner-category-link:hover {
  color: #05bcff; /* 悬停文字颜色 */
  text-decoration: underline;
  cursor: pointer;
}

.category-link-group:hover {
  background-color: rgba(179, 205, 221, 0.3); /* 悬停背景色 */
}
</style>