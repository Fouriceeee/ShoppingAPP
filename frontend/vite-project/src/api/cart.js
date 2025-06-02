import axiosInstance from './index';

// 定义 API 的基础路径
// 推荐从环境变量中获取，例如使用 Vite 的 import.meta.env.VITE_APP_API_BASE_URL
// 如果没有设置环境变量，则使用默认值
const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:5173/api';

/**
 * 获取购物车中的所有商品。
 * @returns {Promise<AxiosResponse>} 包含购物车商品列表的响应。
 */
export const getCartItems = () => {
    return axiosInstance.get(`${API_BASE_URL}/cart`);
};

/**
 * 向购物车添加一个新商品。
 * @param {Object} itemData - 要添加的商品数据，例如 { productId: 1, quantity: 1, specs: '...' }。
 * @returns {Promise<AxiosResponse>} 添加商品后的响应。
 */
export const addCartItem = (itemData) => {
    return axiosInstance.post(`${API_BASE_URL}/cart`, itemData);
};

/**
 * 更新购物车中指定商品的属性（如数量、选中状态）。
 * 通常使用 PATCH 请求进行部分更新。
 * @param {number} itemId - 要更新的商品ID。
 * @param {Object} updateData - 包含要更新字段的对象，例如 { quantity: 2 } 或 { selected: true }。
 * @returns {Promise<AxiosResponse>} 更新商品后的响应。
 */
export const updateCartItem = (itemId, updateData) => {
    return axiosInstance.patch(`${API_BASE_URL}/cart/${itemId}`, updateData);
};

/**
 * 从购物车中删除指定商品。
 * @param {number} itemId - 要删除的商品ID。
 * @returns {Promise<AxiosResponse>} 删除商品后的响应。
 */
export const deleteCartItem = (itemId) => {
    return axiosInstance.delete(`${API_BASE_URL}/cart/${itemId}`);
};

/**
 * 批量删除购物车中的商品。
 * @param {number[]} itemIds - 要删除的商品ID数组。
 * @returns {Promise<AxiosResponse>} 批量删除商品后的响应。
 */
export const batchDeleteCartItems = (itemIds) => {
    // 通常 POST 请求更适合发送包含数组的请求体
    return axiosInstance.post(`${API_BASE_URL}/cart/batch-delete`, { ids: itemIds });
};

/**
 * 清空购物车中的所有商品。
 * @returns {Promise<AxiosResponse>} 清空购物车后的响应。
 */
export const clearAllCartItems = () => {
    return axiosInstance.delete(`${API_BASE_URL}/cart/clear`);
};

/**
 * 更新购物车中所有商品的选中状态（全选/全不选）。
 * @param {boolean} selected - 是否选中所有商品。
 * @returns {Promise<AxiosResponse>} 更新所有商品选中状态后的响应。
 */
export const updateAllCartItemsSelection = (selected) => {
    return axiosInstance.patch(`${API_BASE_URL}/cart/select-all`, { selected });
};