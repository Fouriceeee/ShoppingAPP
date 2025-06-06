import axiosInstance from './index';

// 定义 API 的基础路径
const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080';

/**
 * 获取所有产品列表。
 * 后端根目录下的 data/products.json 文件将通过后端暴露为 API。
 * @returns {Promise<AxiosResponse>} 包含产品列表的响应。
 */
export const getProducts = () => {
    // /api/products 接口来读取 data/products.json
    return axiosInstance.get(`${API_BASE_URL}/products`);
};

/**
 * 根据产品ID获取单个产品详情 (如果需要的话)。
 * @param {number} productId - 产品ID。
 * @returns {Promise<AxiosResponse>} 包含单个产品详情的响应。
 */
export const getProductById = (productId) => {
    return axiosInstance.get(`${API_BASE_URL}/products/${productId}`);
};

// 你可以根据需要添加更多产品相关的API，例如搜索、筛选等