<template>
    <!-- 顶部蓝色导航栏 -->
    <nav class="navbar">
      <ul class="nav-list">
        <li v-for="(item, index) in navItems" :key="index">
          <a href="#">{{ item }}</a>
        </li>
      </ul>
    </nav>

    <!-- 第二栏工具区 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <!-- Logo -->
        <a href="#" class="logo">
          <img src="https://via.placeholder.com/120x50?text=LOGO" alt="Logo" />
        </a>

        <!-- 菜单按钮 -->
        <div class="menu">
          <el-dropdown @command="handleCommand" trigger="click">
            <el-button type="text" class="menu-btn" @click="toggleMenu">
              <svg class="menu-icon"
                  xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                  width="20"
                  height="20"
                  viewBox="0 0 50 50"
              >
                <path d="M 5 8 A 2.0002 2.0002 0 1 0 5 12 L 45 12 A 2.0002 2.0002 0 1 0 45 8 L 5 8 z
                 M 5 23 A 2.0002 2.0002 0 1 0 5 27 L 45 27 A 2.0002 2.0002 0 1 0 45 23 L 5 23 z
                 M 5 38 A 2.0002 2.0002 0 1 0 5 42 L 45 42 A 2.0002 2.0002 0 1 0 45 38 L 5 38 z"></path>
              </svg>
              菜单
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu class="dropdown">
                <el-dropdown-item>Action 1</el-dropdown-item>
                <el-dropdown-item>Action 2</el-dropdown-item>
                <el-dropdown-item>Action 3</el-dropdown-item>
                <el-dropdown-item disabled>Action 4</el-dropdown-item>
                <el-dropdown-item divided>Action 5</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 中间搜索框 -->
      <div class="search-box">
        <input type="text" placeholder="搜索商品..." class="search-input" />
        <el-button class="search-button" type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <!-- 购物车 -->
      <div class="cart">
        <a href="/cart">
        <el-button type="text" class="cart-btn">
          <img class="shopping-cart-icon" src="@/assets/icons/cart.png" alt="shopping-cart" />
          购物车
        </el-button>
        </a>
      </div>
    </div>


<!--    <main class="content">-->
<!--      <p style="height: 2000px;">滚动内容测试...</p>-->
<!--    </main>-->
</template>

<script setup lang="js">
import { ref } from 'vue'
import { } from "@element-plus/icons-vue";

const navItems = ['首页', '产品', '服务', '关于我们']
const showMenu = ref(false)

function toggleMenu() {
  showMenu.value = !showMenu.value
}
</script>

<style scoped>
/* 顶部蓝色导航栏 */
.navbar {
  background-color: #05284e;
  padding: 10px 20px;
  height: 20px;
}

.nav-list {
  display: flex;
  gap: 50px;
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-list li a {
  color: white;
  text-decoration: none;
  font-size: 15px;
  position: relative;
  top: -3px;
}

.nav-list li a:hover {
  text-decoration: underline;
}

/* 第二栏工具区整体布局 */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky; /* 固定在顶部 */
  height: 80px;
  background: #064273;
  padding: 0 20px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  top: 0;
  left: 0;
  right: 0;

  z-index: 1000;/* 叠放在最上面 */
}


/* 左侧 Logo + 菜单 */
.toolbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo img {
  height: 50px;
}

/* 菜单按钮 */
.menu-btn {
  color: white;
  position: relative;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
  margin-left: 30px;
}

.menu-icon {
  margin-right: 7px;
  fill: white;
}

.dropdown {
  cursor: pointer;
  color: white;
}

.dropdown li a {
  text-decoration: none;
  color: #202020;
}

.dropdown li a:hover {
  text-decoration: underline;
}

/* 搜索框 */
.search-box {
  display: flex;
  align-items: stretch;
  flex-wrap: nowrap;
  max-width: 500px;
  width: 100%;
  margin: 0 80px;
}

/* 输入框样式 */
.search-input {
  flex: 1;
  padding: 10px 14px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-right: none;              /* 去掉右边框，与按钮无缝连接 */
  border-radius: 4px 0 0 4px;
  outline: none;
  box-sizing: border-box;
}

/* 按钮样式 */
.search-button {
  height: 38px;
  padding: 0 10px;
  font-size: 14px;
  background-color: #cddded;
  border: 1px solid #ccc;
  color: #000205;
  border-left: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  white-space: nowrap;
}


/* 购物车按钮 */
.cart a {
  text-decoration: none;
  margin-right: 80px;
}

.cart-btn {
  font-size: 20px;
  color: white;
  font-weight: bold;
}

.shopping-cart-icon {
  width: 30px;
  height: 30px;
  margin-right: 7px
}





</style>
