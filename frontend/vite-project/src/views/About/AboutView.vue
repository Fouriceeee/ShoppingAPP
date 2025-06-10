<template>
  <div class="about-page">
    <el-container>
      <el-main class="page-container">
        <h1 class="page-title">关于我们</h1>
        <el-divider />

        <el-card class="box-card project-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>关于本项目</span>
            </div>
          </template>
          <p class="project-description">
            本项目是一个基于 Vue 和 Java 技术栈开发的网上购物商城。前端使用 Vue 3 和 Element Plus 构建现代化的用户界面，通过 Axios 与后端进行数据交互。后端采用 Java 和 Spark 技术，旨在打造一个稳定、可扩展的电商平台。
          </p>
          <el-button
              tag="a"
              :href="projectRepoUrl"
              target="_blank"
              type="primary"
              :icon="Link"
              plain
          >
            项目 GitHub 仓库
          </el-button>
        </el-card>

        <h2 class="authors-title">开发团队</h2>

        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <el-card class="box-card author-card" shadow="hover">
              <div class="author-content">
                <el-avatar :size="80" :src="author1.avatarUrl" />
                <div class="author-details">
                  <h3>{{ author1.name }}</h3>
                  <p class="author-bio">
                    项目后端开发者，负责系统架构和核心功能实现。
                  </p>
                  <a :href="author1.githubUrl">
                    <img src="https://img.icons8.com/?size=100&id=62856&format=png&color=000000" alt="GitHub" class="button-img"/>
                  </a>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :xs="24" :sm="12">
            <el-card class="box-card author-card" shadow="hover">
              <div class="author-content">
                <el-avatar :size="80" :src="author2.avatarUrl" />
                <div class="author-details">
                  <h3>{{ author2.name }}</h3>
                  <p class="author-bio">
                    {{ author2.bio }}
                  </p>
                  <a :href="author2.githubUrl">
                    <img src="https://img.icons8.com/?size=100&id=62856&format=png&color=000000" alt="GitHub" class="button-img"/>
                  </a>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Link, Pointer } from '@element-plus/icons-vue';

// 页面标题
document.title = '关于我们 - 易猫商城';

// --- 数据定义 ---

// 项目仓库信息
const projectRepoUrl = ref('https://github.com/Fouriceeee/ShoppingAPP.git');

// 作者一信息
const author1 = ref({
  name: 'Fouriceeee',
  avatarUrl: 'https://avatars.githubusercontent.com/u/97792220?v=4', // 从 GitHub 自动获取头像
  bio: '项目后端开发者，负责系统架构和核心功能实现。',
  githubUrl: 'https://github.com/Fouriceeee'
});

// 作者二信息 (*** 请在这里填写真实信息 ***)
const author2 = ref({
  name: 'miaoning',
  avatarUrl: 'https://avatars.githubusercontent.com/u/186403365?v=4', // 这是一个 Element Plus 默认头像，可以替换为 GitHub 头像链接
  bio: '前端和api的构建',
  githubUrl: 'https://github.com/Miaoning-exe'
});
</script>

<style scoped>
.about-page {
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-title {
  text-align: center;
  font-size: 2.5rem;
  color: #303133;
  font-weight: 500;
}

.authors-title {
  text-align: center;
  font-size: 2rem;
  color: #606266;
  margin-top: 40px;
  margin-bottom: 20px;
  font-weight: 400;
}

.box-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.project-card {
  margin-bottom: 40px;
  text-align: center;
}

.card-header {
  font-size: 1.2rem;
  font-weight: bold;
}

.project-description {
  font-size: 1rem;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 20px;
  padding: 0 10px;
}

.author-card {
  margin-bottom: 20px;
}

.author-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.author-details {
  flex-grow: 1;
}

.author-details h3 {
  margin: 0 0 8px 0;
  font-size: 1.5rem;
  color: #303133;
}

.author-bio {
  font-size: 0.95rem;
  color: #909399;
  line-height: 1.6;
  min-height: 50px; /* 保证两张卡片在 bio 长短不一时高度基本一致 */
  margin-bottom: 15px;
}

.button-img {
  height: 40px;
  width: 40px;
}
</style>