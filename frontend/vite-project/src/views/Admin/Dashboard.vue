<template>
  <div class="admin-dashboard">
    <!-- 管理后台顶部导航 -->
    <div class="admin-header">
      <div class="admin-logo">
        <img src="../../assets/logo.png" alt="Logo" />
        <h1>易猫商城后台管理系统</h1>
      </div>
      <div class="admin-user-info">
        <span>欢迎，{{ adminName }}</span>
        <el-dropdown @command="handleCommand">
          <span class="admin-dropdown-link">
            <el-avatar :size="32" :src="adminAvatar" />
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="settings">系统设置</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="admin-container">
      <!-- 侧边栏菜单 -->
      <div class="admin-sidebar">
        <el-menu
          :default-active="activeMenu"
          class="admin-menu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>控制台</span>
          </el-menu-item>

          <el-sub-menu index="1">
            <template #title>
              <el-icon><ShoppingBag /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="/admin/products">商品列表</el-menu-item>
            <el-menu-item index="/admin/products/add">添加商品</el-menu-item>
            <el-menu-item index="/admin/categories">分类管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><ShoppingCart /></el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item index="/admin/orders">所有订单</el-menu-item>
            <el-menu-item index="/admin/orders/pending">待处理订单</el-menu-item>
            <el-menu-item index="/admin/orders/shipped">已发货订单</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/users">用户列表</el-menu-item>
            <el-menu-item index="/admin/users/add">添加用户</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/admin/settings">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 主内容区域 -->
      <div class="admin-main">
        <div class="admin-content">
          <!-- 面包屑导航 -->
          <el-breadcrumb separator="/" class="admin-breadcrumb">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>控制台</el-breadcrumb-item>
          </el-breadcrumb>

          <!-- 控制台内容 -->
          <div class="dashboard-content">
            <h2>控制台概览</h2>

            <!-- 数据卡片 -->
            <div class="data-cards">
              <el-card class="data-card">
                <template #header>
                  <div class="card-header">
                    <el-icon><ShoppingBag /></el-icon>
                    <span>商品总数</span>
                  </div>
                </template>
                <div class="card-content">
                  <h3>{{ statsData.products }}</h3>
                  <p>总商品数量</p>
                </div>
              </el-card>

              <el-card class="data-card">
                <template #header>
                  <div class="card-header">
                    <el-icon><ShoppingCart /></el-icon>
                    <span>订单总数</span>
                  </div>
                </template>
                <div class="card-content">
                  <h3>{{ statsData.orders }}</h3>
                  <p>总订单数量</p>
                </div>
              </el-card>

              <el-card class="data-card">
                <template #header>
                  <div class="card-header">
                    <el-icon><User /></el-icon>
                    <span>用户总数</span>
                  </div>
                </template>
                <div class="card-content">
                  <h3>{{ statsData.users }}</h3>
                  <p>注册用户数</p>
                </div>
              </el-card>

              <el-card class="data-card">
                <template #header>
                  <div class="card-header">
                    <el-icon><Money /></el-icon>
                    <span>本月营收</span>
                  </div>
                </template>
                <div class="card-content">
                  <h3>￥{{ statsData.revenue }}</h3>
                  <p>本月销售额</p>
                </div>
              </el-card>
            </div>

            <!-- 最近订单 -->
            <div class="recent-orders">
              <h3>最近订单</h3>
              <el-table :data="recentOrders" stripe style="width: 100%">
                <el-table-column prop="id" label="订单ID" width="180" />
                <el-table-column prop="customer" label="客户名称" width="180" />
                <el-table-column prop="date" label="下单日期" />
                <el-table-column prop="amount" label="金额" />
                <el-table-column prop="status" label="状态">
                  <template #default="scope">
                    <el-tag :type="getOrderStatusType(scope.row.status)">
                      {{ scope.row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="180">
                  <template #default>
                    <el-button size="small" type="primary">查看</el-button>
                    <el-button size="small">处理</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Odometer, ShoppingBag, ShoppingCart, User, Setting, 
  Money, ArrowDown 
} from '@element-plus/icons-vue';
import { checkAdminAuth, adminLogout } from '@/utils/adminService';

// 设置页面标题
document.title = '管理控制台 - 易猫商城';

const router = useRouter();
const activeMenu = ref('/admin/dashboard');
const adminName = ref('管理员');
const adminAvatar = ref(''); // 默认头像

// 模拟统计数据
const statsData = ref({
  products: 368,
  orders: 156,
  users: 2547,
  revenue: 86435.76
});

// 模拟最近订单数据
const recentOrders = ref([
  { id: '202506080001', customer: '张三', date: '2025-06-07', amount: '￥1299.00', status: '待发货' },
  { id: '202506070023', customer: '李四', date: '2025-06-07', amount: '￥899.50', status: '已完成' },
  { id: '202506070015', customer: '王五', date: '2025-06-07', amount: '￥2499.00', status: '已发货' },
  { id: '202506060089', customer: '赵六', date: '2025-06-06', amount: '￥599.00', status: '已取消' },
  { id: '202506060074', customer: '钱七', date: '2025-06-06', amount: '￥3699.00', status: '已完成' }
]);

// 获取订单状态对应的标签类型
const getOrderStatusType = (status) => {
  const statusMap = {
    '待发货': 'warning',
    '已发货': 'info',
    '已完成': 'success',
    '已取消': 'danger'
  };
  return statusMap[status] || 'info';
};

// 下拉菜单命令处理
const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm(
      '确定要退出管理系统吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 调用退出登录
      adminLogout();
      ElMessage.success('已安全退出系统');
      router.push('/admin/login');
    }).catch(() => {
      // 取消退出
    });
  } else if (command === 'profile') {
    router.push('/admin/profile');
  } else if (command === 'settings') {
    router.push('/admin/settings');
  }
};

onMounted(() => {
  // 检查管理员是否已登录
  const adminInfo = checkAdminAuth();
  if (!adminInfo) {
    ElMessage.error('请先登录');
    router.push('/admin/login');
    return;
  }

  // 设置管理员信息
  adminName.value = adminInfo.username || '管理员';
  adminAvatar.value = adminInfo.avatar || '/src/assets/pictures/loginImages/default-avatar.png';
});
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.admin-header {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
}

.admin-logo {
  display: flex;
  align-items: center;
}

.admin-logo img {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.admin-logo h1 {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  margin: 0;
}

.admin-user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.admin-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #333;
}

.admin-container {
  display: flex;
  min-height: calc(100vh - 60px);
  margin-top: 60px;
}

.admin-sidebar {
  width: 220px;
  background-color: #304156;
  position: fixed;
  top: 60px;
  bottom: 0;
  left: 0;
  overflow-y: auto;
}

.admin-menu {
  border-right: none;
  height: 100%;
}

.admin-main {
  flex: 1;
  margin-left: 220px;
  padding: 20px;
}

.admin-content {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  min-height: calc(100vh - 100px);
}

.admin-breadcrumb {
  margin-bottom: 20px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.dashboard-content h2 {
  margin-bottom: 20px;
  color: #333;
}

.data-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.data-card {
  border-radius: 4px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
}

.card-content {
  text-align: center;
  padding: 15px 0;
}

.card-content h3 {
  font-size: 28px;
  margin: 0;
  color: #333;
}

.card-content p {
  margin: 5px 0 0;
  color: #999;
  font-size: 14px;
}

.recent-orders {
  margin-top: 30px;
}

.recent-orders h3 {
  margin-bottom: 15px;
  color: #333;
}

@media (max-width: 1200px) {
  .data-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .admin-sidebar {
    width: 64px;
  }

  .admin-main {
    margin-left: 64px;
  }

  .data-cards {
    grid-template-columns: 1fr;
  }
}
</style>
