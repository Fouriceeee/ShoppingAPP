<template>
  <div class="edit-product-container">
    <el-card class="edit-product-card" v-loading="pageLoading">
      <template #header>
        <div class="card-header">
          <h2>编辑商品</h2>
          <el-button type="primary" size="small" @click="goBack">
            <el-icon><Back /></el-icon> 返回列表
          </el-button>
        </div>
      </template>

      <el-form 
        v-if="!pageLoading"
        ref="productFormRef"
        :model="productForm" 
        :rules="productRules"
        label-position="top"
        class="product-form"
      >
        <!-- 基本信息 -->
        <h3>基本信息</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="title">
              <el-input v-model="productForm.title" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="category">
              <el-select v-model="productForm.category" placeholder="请选择商品分类" class="full-width">
                <el-option 
                  v-for="item in categoryOptions" 
                  :key="item.value" 
                  :label="item.label" 
                  :value="item.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 价格设置 -->
        <h3>价格设置</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格(整数部分)" prop="priceInteger">
              <el-input-number 
                v-model="productForm.priceInteger" 
                :min="0" 
                :precision="0"
                class="full-width"
                placeholder="请输入价格整数部分"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格(小数部分)" prop="priceDecimal">
              <el-input-number 
                v-model="productForm.priceDecimal" 
                :min="0" 
                :max="99"
                :precision="0"
                class="full-width"
                placeholder="请输入价格小数部分"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 商品图片 -->
        <h3>商品图片</h3>
        <el-form-item label="图片URL" prop="image">
          <el-input v-model="productForm.image" placeholder="请输入商品图片URL" />
          <div class="image-preview-container" v-if="productForm.image">
            <p>图片预览：</p>
            <img :src="productForm.image" alt="商品图片预览" class="image-preview" />
          </div>
        </el-form-item>

        <!-- 商品ID -->
        <el-form-item label="商品ID" prop="id">
          <el-input v-model="productForm.id" disabled />
          <div class="form-helper-text">商品ID不可修改</div>
        </el-form-item>

        <!-- 商品描述 -->
        <h3>商品描述</h3>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="productForm.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入商品详细描述"
          />
        </el-form-item>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="submitForm" :loading="loading">保存修改</el-button>
        </div>
      </el-form>

      <div v-else class="empty-state">
        <el-empty description="加载商品信息中..." />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Back } from '@element-plus/icons-vue';
import { getProductById, updateProduct, getCategories } from '@/api/products';

// 设置页面标题
document.title = '编辑商品 - 管理控制台';

const router = useRouter();
const route = useRoute();
const productId = route.params.id;

const productFormRef = ref(null);
const loading = ref(false);
const pageLoading = ref(true);
const originalProduct = ref(null);

// 商品表单数据
const productForm = reactive({
  id: '',
  title: '',
  image: '',
  priceInteger: 0,
  priceDecimal: 0,
  category: '',
  description: ''
});

// 表单验证规则
const productRules = {
  title: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 100, message: '商品名称长度应在2到100个字符之间', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  priceInteger: [
    { required: true, message: '请输入价格整数部分', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能为负数', trigger: 'blur' }
  ],
  priceDecimal: [
    { required: true, message: '请输入价格小数部分', trigger: 'blur' },
    { type: 'number', min: 0, max: 99, message: '小数部分应在0到99之间', trigger: 'blur' }
  ],
  image: [
    { required: true, message: '请输入商品图片URL', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { min: 10, max: 2000, message: '描述长度应在10到2000个字符之间', trigger: 'blur' }
  ]
};

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
    // 获取分类列表
    const categoriesResponse = await getCategories();
    if (categoriesResponse.data && Array.isArray(categoriesResponse.data)) {
      categoryOptions.value = categoriesResponse.data.map(category => ({
        value: category.code,
        label: category.name
      }));
    }

    // 获取商品详情
    const productResponse = await getProductById(productId);
    console.log('DEBUG:商品详情响应:', productResponse.data);

    // 检查API响应格式，处理不同的响应结构
    let productData;
    if (productResponse.data && productResponse.data.code === 200) {
      // 如果是标准API包装响应 {code: 200, data: {...}, message: '...'}
      productData = productResponse.data.data;
      originalProduct.value = productData;
    } else {
      // 如果直接返回产品数据对象
      productData = productResponse.data;
      originalProduct.value = productData;
    }

    console.log('DEBUG:处理后的商品数据:', productData);

    // 填充表单数据
    Object.assign(productForm, {
      id: productData.id || '',
      title: productData.title || '',
      image: productData.image || '',
      priceInteger: productData.priceInteger || 0,
      priceDecimal: productData.priceDecimal || 0,
      category: productData.category || '',
      description: productData.description || ''
    });
    console.log("DEBUG:获得的商品标题为:" + productForm.title);
  } catch (error) {
    ElMessage.error('获取商品信息失败');
    console.error('获取商品信息失败:', error);
  } finally {
    pageLoading.value = false;
  }
});

// 提交表单
const submitForm = async () => {
  if (!productFormRef.value) return;

  await productFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('请正确填写所有必填项');
      return;
    }

    try {
      loading.value = true;

      // 调用API更新商品
      await updateProduct(productForm.id, productForm);

      ElMessage.success('商品更新成功');

      // 返回商品列表
      await router.push('/admin/products');
    } catch (error) {
      const message = error.response?.data?.message || '更新商品失败，请稍后再试';
      ElMessage.error(message);
    } finally {
      loading.value = false;
    }
  });
};

// 重置表单
const resetForm = () => {
  if (productFormRef.value && originalProduct.value) {
    // 重置为原始值
    Object.assign(productForm, {
      id: originalProduct.value.id,
      title: originalProduct.value.title,
      image: originalProduct.value.image,
      priceInteger: originalProduct.value.priceInteger,
      priceDecimal: originalProduct.value.priceDecimal,
      category: originalProduct.value.category,
      description: originalProduct.value.description
    });
  }
};

// 返回商品列表
const goBack = () => {
  router.push('/admin/products');
};
</script>

<style scoped>
.edit-product-container {
  padding: 20px;
}

.edit-product-card {
  max-width: 1000px;
  margin: 0 auto;
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

.product-form {
  margin-top: 20px;
}

.product-form h3 {
  margin: 20px 0 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  color: #409EFF;
  font-size: 16px;
}

.full-width {
  width: 100%;
}

.form-helper-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.image-preview-container {
  margin-top: 10px;
}

.image-preview {
  max-width: 200px;
  max-height: 200px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 5px;
  margin-top: 5px;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}
</style>
