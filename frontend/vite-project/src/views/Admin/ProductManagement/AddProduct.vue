<template>
  <div class="add-product-container">
    <el-card class="add-product-card">
      <template #header>
        <div class="card-header">
          <h2>添加新商品</h2>
        </div>
      </template>

      <el-form 
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
          <el-input v-model="productForm.id" placeholder="请输入商品ID，不填将自动生成" />
          <div class="form-helper-text">商品ID应唯一，不填将自动生成</div>
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
          <el-button type="primary" @click="submitForm" :loading="loading">添加商品</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';
import { addProduct, getCategories } from '@/api/products';

// 设置页面标题
document.title = '添加商品 - 管理控制台';

const router = useRouter();
const productFormRef = ref(null);
const loading = ref(false);

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
  { value: 'VIDEOCARD', label: '显卡' },
  { value: 'MOTHERBOARD', label: '主板' },
  { value: 'RAM', label: '内存' },
  { value: 'STORAGE', label: '存储设备' },
  { value: 'POWERSUPPLY', label: '电源' },
  { value: 'CASE', label: '机箱' },
  { value: 'COOLING', label: '散热器' },
  { value: 'PERIPHERAL', label: '外设' },
  { value: 'PAD', label: '平板电脑'},
  { value: 'MONITOR', label: '显示器'},
  { value: 'LAPTOP', label: '笔记本电脑'},
  { value: 'GRAPHIC_TABLET', label: '数位板'}
]);

// 在组件挂载时获取分类列表
onMounted(async () => {
  try {
    const response = await getCategories();
    if (response.data && Array.isArray(response.data)) {
      // 如果后端返回分类列表，则使用后端数据
      categoryOptions.value = response.data.map(category => ({
        value: category.code,
        label: category.name
      }));
    }
  } catch (error) {
    console.warn('获取分类列表失败，使用默认分类', error);
    // 使用默认分类列表，不需要处理错误
  }
});

// 生成唯一ID
const generateUniqueId = () => {
  return 'prod_' + Date.now() + '_' + Math.floor(Math.random() * 1000);
};

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

      // 如果没有输入ID，则生成一个
      if (!productForm.id.trim()) {
        productForm.id = generateUniqueId();
      }

      // 调用API添加商品
      const response = await addProduct(productForm);

      ElMessage.success('商品添加成功');

      // 确认是否继续添加
      ElMessageBox.confirm(
        '商品添加成功，是否继续添加新商品？',
        '操作成功',
        {
          confirmButtonText: '继续添加',
          cancelButtonText: '返回列表',
          type: 'success',
        }
      ).then(() => {
        // 继续添加，重置表单
        resetForm();
      }).catch(() => {
        // 返回商品列表
        router.push('/admin/products');
      });

    } catch (error) {
      const message = error.response?.data?.message || '添加商品失败，请稍后再试';
      ElMessage.error(message);
    } finally {
      loading.value = false;
    }
  });
};

// 重置表单
const resetForm = () => {
  if (productFormRef.value) {
    productFormRef.value.resetFields();
    // 重置为默认值
    Object.assign(productForm, {
      id: '',
      title: '',
      image: '',
      priceInteger: 0,
      priceDecimal: 0,
      category: '',
      description: ''
    });
  }
};
</script>

<style scoped>
.add-product-container {
  padding: 20px;
}

.add-product-card {
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
</style>
