import axiosInstance from './index';

// Define API base URL
const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api';

/**
 * Place a new order with the selected cart items
 * @param {Object} orderData - Order data including shipping address, payment method, etc.
 * @returns {Promise<AxiosResponse>} Response after placing the order
 */
export const placeOrder = (orderData) => {
    return axiosInstance.post(`${API_BASE_URL}/orders`, orderData);
};

/**
 * Get all orders for the current user
 * @returns {Promise<AxiosResponse>} Response containing user's orders
 */
export const getUserOrders = () => {
    return axiosInstance.get(`${API_BASE_URL}/orders`);
};

/**
 * Get details of a specific order
 * @param {string} orderId - ID of the order to retrieve
 * @returns {Promise<AxiosResponse>} Response containing order details
 */
export const getOrderDetails = (orderId) => {
    return axiosInstance.get(`${API_BASE_URL}/orders/${orderId}`);
};

/**
 * Cancel an order
 * @param {string} orderId - ID of the order to cancel
 * @returns {Promise<AxiosResponse>} Response after canceling the order
 */
export const cancelOrder = (orderId) => {
    return axiosInstance.post(`${API_BASE_URL}/orders/${orderId}/cancel`);
};