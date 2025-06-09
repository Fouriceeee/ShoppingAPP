
/**
 * 商品相关工具函数
 */

// 分类映射配置
const CATEGORY_CONFIG = {
  'CPU': { label: 'CPU处理器', type: 'danger' },
  'GPU': { label: '显卡', type: 'success' },
  'MOTHERBOARD': { label: '主板', type: 'warning' },
  'RAM': { label: '内存', type: 'info' },
  'STORAGE': { label: '存储设备', type: 'primary' },
  'POWER': { label: '电源', type: '' },
  'CASE': { label: '机箱', type: '' },
  'COOLING': { label: '散热器', type: '' },
  'PERIPHERAL': { label: '外设', type: '' }
};

/**
 * 格式化商品价格
 * @param {number} integer - 价格整数部分
 * @param {number} decimal - 价格小数部分
 * @returns {string} 格式化后的价格字符串
 */
export const formatPrice = (integer, decimal) => {
  return `¥${integer}.${decimal.toString().padStart(2, '0')}`;
};

/**
 * 获取商品图片完整URL
 * @param {string} imagePath - 图片路径
 * @returns {string} 完整的图片URL
 */
export const getProductImageUrl = (imagePath) => {
  if (!imagePath) {
    // 如果没有图片，返回默认图片
    try {
      return new URL('../assets/pictures/products/default-product.jpg', import.meta.url).href;
    } catch (error) {
      return '';
    }
  }

  // 处理后端返回的图片路径
  if (imagePath.startsWith('/images/')) {
    // 静态资源服务器的基础URL
    const baseUrl = 'http://localhost:8080';
    // 将 "/images/xxx.webp" 转换为 "http://localhost:8080/images/xxx.webp"
    return `${baseUrl}/${imagePath.substring(1)}`;
  } else if (imagePath.startsWith('http')) {
    // 如果已经是完整URL，直接返回
    return imagePath;
  } else {
    // 本地资源文件夹中的图片
    try {
      return new URL(`../assets/pictures/products/${imagePath}`, import.meta.url).href;
    } catch (error) {
      console.error('无法加载产品图片:', error);
      return '';
    }
  }
};

/**
 * 获取分类显示标签
 * @param {string} categoryCode - 分类代码
 * @param {Array} customCategories - 自定义分类列表（可选）
 * @returns {string} 分类显示名称
 */
export const getCategoryLabel = (categoryCode, customCategories = []) => {
  // 优先使用自定义分类
  if (customCategories.length > 0) {
    const category = customCategories.find(cat => cat.value === categoryCode);
    if (category) return category.label;
  }

  // 使用默认分类配置
  return CATEGORY_CONFIG[categoryCode]?.label || categoryCode;
};

/**
 * 获取分类标签样式类型
 * @param {string} categoryCode - 分类代码
 * @returns {string} Element Plus 标签类型
 */
export const getCategoryType = (categoryCode) => {
  return CATEGORY_CONFIG[categoryCode]?.type || '';
};

/**
 * 获取默认分类选项
 * @returns {Array} 分类选项数组
 */
export const getDefaultCategoryOptions = () => {
  return Object.entries(CATEGORY_CONFIG).map(([value, config]) => ({
    value,
    label: config.label
  }));
};

/**
 * 验证商品数据
 * @param {Object} product - 商品数据
 * @returns {Object} 验证结果 { isValid: boolean, errors: string[] }
 */
export const validateProduct = (product) => {
  const errors = [];

  if (!product.title || product.title.trim() === '') {
    errors.push('商品名称不能为空');
  }

  if (!product.priceInteger || product.priceInteger < 0) {
    errors.push('商品价格不能为空且必须大于等于0');
  }

  if (product.priceDecimal < 0 || product.priceDecimal > 99) {
    errors.push('价格小数部分必须在0-99之间');
  }

  if (!product.category) {
    errors.push('商品分类不能为空');
  }

  return {
    isValid: errors.length === 0,
    errors
  };
};

/**
 * 搜索过滤商品
 * @param {Array} products - 商品列表
 * @param {string} searchQuery - 搜索关键词
 * @param {string} categoryFilter - 分类筛选
 * @returns {Array} 过滤后的商品列表
 */
export const filterProducts = (products, searchQuery = '', categoryFilter = '') => {
  return products.filter(product => {
    // 搜索过滤
    const matchesSearch = !searchQuery ||
      product.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      product.id.toLowerCase().includes(searchQuery.toLowerCase());

    // 分类过滤
    const matchesCategory = !categoryFilter || product.category === categoryFilter;

    return matchesSearch && matchesCategory;
  });
};