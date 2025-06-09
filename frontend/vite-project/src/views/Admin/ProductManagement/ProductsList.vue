<template>
  <div class="products-list-container">
    <el-card class="products-list-card">
      <template #header>
        <div class="card-header">
          <h2>商品管理</h2>
          <el-button type="primary" @click="goToAddProduct">
            <el-icon><Plus /></el-icon> 添加商品
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="filter-container">
        <el-input
          v-model="searchQuery"
          placeholder="搜索商品名称或ID"
          class="search-input"
          clearable
          @clear="fetchProducts"
        >
          <template #append>
            <el-button @click="fetchProducts">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>

        <el-select v-model="categoryFilter" placeholder="商品分类" clearable @change="fetchProducts" class="filter-select">
          <el-option 
            v-for="item in categoryOptions" 
            :key="item.value" 
            :label="item.label" 
            :value="item.value" 
          />
        </el-select>
      </div>

      <!-- 商品列表表格 -->
      <el-table
        v-loading="loading"
        :data="products"
        style="width: 100%"
        border
        @row-click="handleRowClick"
        class="clickable-table"
      >
        <el-table-column label="ID" prop="id" width="220" sortable />

        <el-table-column label="商品图片" width="100">
          <template #default="scope">
            <el-image 
              :src="getProductImageUrl(scope.row.image)"
              :preview-src-list="[scope.row.image]"
              fit="contain"
              style="width: 50px; height: 50px"
            />
          </template>
        </el-table-column>

        <el-table-column label="商品名称" prop="title" min-width="200" show-overflow-tooltip />

        <el-table-column label="价格" width="120">
          <template #default="scope">
            {{ formatPrice(scope.row.priceInteger, scope.row.priceDecimal) }}
          </template>
        </el-table-column>

        <el-table-column label="分类" prop="category" width="120" sortable>
          <template #default="scope">
            <el-tag size="small" type="info" effect="plain">
              {{ scope.row.category }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button-group>
              <el-button size="small" type="primary" @click="editProduct(scope.row)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button size="small" type="danger" @click="confirmDelete(scope.row)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalProducts"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue';
import { getProducts, deleteProduct, getCategories } from '@/api/products';
import { getProductImageUrl, formatPrice } from "@/utils/productService.js";

// 设置页面标题
document.title = '商品管理 - 管理控制台';

const router = useRouter();
const loading = ref(false);
const products = ref([]);
const totalProducts = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');
const categoryFilter = ref('');
// 加载状态
const isLoading = ref(true);
// 加载错误信息
const loadError = ref(null);

// 分类选项
const categoryOptions = ref([
  { value: 'CPU', label: 'CPU处理器' },
  { value: 'GPU', label: '显卡' },
  { value: 'MOTHERBOARD', label: '主板' },
  { value: 'RAM', label: '内存' },
  { value: 'STORAGE', label: '存储设备' },
  { value: 'POWER', label: '电源' },
  { value: 'CASE', label: '机箱' },
  { value: 'COOLING', label: '散热器' },
  { value: 'PERIPHERAL', label: '外设' }
]);

// 在组件挂载时获取分类列表和商品数据
onMounted(async () => {
  try {
    const response = await getCategories();
    if (response.data && Array.isArray(response.data)) {
      categoryOptions.value = response.data.map(category => ({
        value: category.code,
        label: category.name
      }));
    }
  } catch (error) {
    console.warn('获取分类列表失败，使用默认分类', error);
  }

  // 获取商品数据
  await fetchProducts();
});

// 获取商品列表
const fetchProducts = async () => {
  try {
    isLoading.value = true;
    loadError.value = null;

    const response = await getProducts();

    if (response.data && response.data.code === 200) {
      products.value = response.data.data;
      console.log('从后端成功获取产品数据:', products.value.length, '个产品');
    } else {
      throw new Error(response.data.message || '获取产品数据失败');
    }
  } catch (error) {
    console.error('加载产品数据失败:', error);
    loadError.value = '无法加载产品数据，请刷新页面重试';
    ElMessage.error('加载产品列表失败');
  } finally {
    isLoading.value = false;
  }
};

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchProducts();
};

// 处理每页数量变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1; // 重置到第一页
  fetchProducts();
};

// 跳转到添加商品页面
const goToAddProduct = () => {
  router.push('/admin/products/add');
};

// 编辑商品
const editProduct = (product) => {
  router.push(`/admin/products/edit/${product.id}`);
};

// 确认删除商品
const confirmDelete = (product) => {
  ElMessageBox.confirm(
    `确定要删除商品 "${product.title}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteProduct(product.id);
      ElMessage.success('商品删除成功');
      await fetchProducts(); // 刷新列表
    } catch (error) {
      ElMessage.error('删除商品失败：' + (error.response?.data?.message || '未知错误'));
      console.error('删除商品失败:', error);
    }
  }).catch(() => {
    // 用户取消删除，不需要做任何事
  });
};

// 导航到产品详情页
const navigateToProductDetail = (productId) => {
  router.push(`/products/${productId}`);
};

// 处理表格行点击事件
const handleRowClick = (row) => {
  navigateToProductDetail(row.id);
};

</script>

<style scoped>
.products-list-container {
  padding: 20px;
}

.products-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.filter-select {
  width: 200px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .filter-container {
    flex-direction: column;
  }

  .search-input,
  .filter-select {
    width: 100%;
  }

  .pagination-container {
    justify-content: center;
  }
}

/* 价格标签样式 */
.price-tag {
  color: #f56c6c;
  font-weight: bold;
}

/* 可点击表格样式 */
.clickable-table :deep(.el-table__row) {
  cursor: pointer;
  transition: background-color 0.3s;
}
</style>


