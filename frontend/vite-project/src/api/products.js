import axiosInstance from './index';

const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api';

/**
 * 获取所有产品列表。
 * 后端根目录下的 data/products.json 文件将通过后端暴露为 API。
 * @returns {Promise<AxiosResponse>} 包含产品列表的响应。
 */
export const getProducts = () => {
    return axiosInstance.get(`${API_BASE_URL}/products`);
};

/**
 * 搜索产品。
 * 根据关键词搜索产品名称、描述等字段。
 * @param {string} query - 搜索关键词。
 * @returns {Promise<AxiosResponse>} 包含搜索结果的响应。
 */
export const searchProducts = (query) => {
    return axiosInstance.get(`${API_BASE_URL}/products/search`, {
        params: { query }
    });
};

/**
 * 根据产品ID获取单个产品详情。
 * @param {number} productId - 产品ID。
 * @returns {Promise<AxiosResponse>} 包含单个产品详情的响应。
 */
export const getProductById = (productId) => {
    console.log("DEBUG：getProductById：开始调用，商品ID为", productId);
    // 使用相对路径，axiosInstance已经配置了baseURL
    return axiosInstance.get(`${API_BASE_URL}/products/${productId}`)
        .then(response => {
            console.log('DEBUG: 获取商品详情响应:', response.data);
            return response;
        })
        .catch(error => {
            console.error('获取商品详情失败:', error);
            throw error;
        });
};

/**
 * 根据分类获取产品列表。
 * @param {string} category - 产品分类代码，例如 VIDEOCARD, CPU 等。
 * @returns {Promise<AxiosResponse>} 包含特定分类产品列表的响应。
 */
export const getProductsByCategory = (category) => {
    return axiosInstance.get(`${API_BASE_URL}/products`, {
        params: { category }
    });
};

/**
 * 添加新商品（管理员功能）
 * @param {Object} productData - 商品数据
 * @param {string} productData.id - 商品ID
 * @param {string} productData.image - 商品图片URL
 * @param {string} productData.title - 商品标题
 * @param {number} productData.priceInteger - 价格整数部分
 * @param {number} productData.priceDecimal - 价格小数部分
 * @param {string} productData.category - 商品分类
 * @param {string} productData.description - 商品描述
 * @returns {Promise<AxiosResponse>} 操作结果响应
 */
export const addProduct = (productData) => {
    return axiosInstance.post(`${API_BASE_URL}/admin/products`, productData);
};

/**
 * 更新商品信息（管理员功能）
 * @param {string} productId - 商品ID
 * @param {Object} productData - 要更新的商品数据
 * @returns {Promise<AxiosResponse>} 操作结果响应
 */
export const updateProduct = (productId, productData) => {
    console.log("DEBUG：updateProduct：开始调用，商品ID为", productId);
    return axiosInstance.patch(`${API_BASE_URL}/admin/products/${productId}`, productData);
};

/**
 * 删除商品（管理员功能）
 * @param {string} productId - 要删除的商品ID
 * @returns {Promise<AxiosResponse>} 操作结果响应
 */
export const deleteProduct = (productId) => {
    return axiosInstance.delete(`${API_BASE_URL}/admin/products/${productId}`);
};

/**
 * 获取商品类别列表
 * @returns {Promise<AxiosResponse>} 包含类别列表的响应
 */
export const getCategories = () => {
    return axiosInstance.get(`${API_BASE_URL}/categories`);
};
