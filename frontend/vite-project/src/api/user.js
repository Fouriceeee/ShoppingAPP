import axiosInstance from './index';

const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api';

/**
 * 用户登录
 * @param {string} email - 用户邮箱
 * @param {string} password - 用户密码
 * @returns {Promise<AxiosResponse>} 登录响应，包含用户信息和token
 */
export const login = (email, password) => {
  return axiosInstance.post(`${API_BASE_URL}/users/login`, {
    email,
    password
  });
};

/**
 * 管理员登录
 * @param {string} email - 管理员邮箱
 * @param {string} password - 管理员密码
 * @returns {Promise<AxiosResponse>} 登录响应，包含管理员信息和token
 */
export const adminLogin = (email, password) => {
  return axiosInstance.post(`${API_BASE_URL}/users/admin/login`, {
    email,
    password
  });
};

/**
 * 用户注册
 * @param {Object} userData - 用户注册数据
 * @param {string} userData.name - 用户名
 * @param {string} userData.email - 用户邮箱
 * @param {string} userData.password - 用户密码
 * @returns {Promise<AxiosResponse>} 注册响应
 */
export const register = (userData) => {
  return axiosInstance.post(`${API_BASE_URL}/users/register`, userData);
};

/**
 * 管理员注册
 * @param {Object} adminData - 管理员注册数据
 * @param {string} adminData.name - 管理员名称
 * @param {string} adminData.email - 管理员邮箱
 * @param {string} adminData.password - 管理员密码
 * @returns {Promise<AxiosResponse>} 注册响应
 */
export const registerAdmin = (adminData) => {
  const adminUserData = {
    ...adminData,
    group: 'ADMIN'
  };
  return axiosInstance.post(`${API_BASE_URL}/users/admin/register`, adminUserData);
};

/**
 * 获取当前登录用户信息
 * @returns {Promise<AxiosResponse>} 用户信息响应
 */
export const getCurrentUser = () => {
  return axiosInstance.get(`${API_BASE_URL}/users/profile`);
};

/**
 * 更新用户信息
 * @param {string} userId - 用户ID
 * @param {Object} userData - 要更新的用户数据
 * @returns {Promise<AxiosResponse>} 更新响应
 */
export const updateUser = (userId, userData) => {
  return axiosInstance.put(`${API_BASE_URL}/users/${userId}`, userData);
};

/**
 * 更新用户密码
 * @param {string} userId - 用户ID
 * @param {string} currentPassword - 当前密码
 * @param {string} newPassword - 新密码
 * @returns {Promise<AxiosResponse>} 更新响应
 */
export const updatePassword = (userId, currentPassword, newPassword) => {
  return axiosInstance.put(`${API_BASE_URL}/users/${userId}/password`, {
    currentPassword,
    newPassword
  });
};

/**
 * 删除用户账号
 * @param {string} userId - 用户ID
 * @returns {Promise<AxiosResponse>} 删除响应
 */
export const deleteAccount = (userId) => {
  return axiosInstance.delete(`${API_BASE_URL}/users/${userId}`);
};

/**
 * 用户退出登录
 * @returns {Promise<AxiosResponse>} 退出登录响应
 */
export const logout = () => {
  return axiosInstance.post(`${API_BASE_URL}/users/logout`);
};

/**
 * 获取用户的购物车
 * @param {string} userId - 用户ID
 * @returns {Promise<AxiosResponse>} 包含购物车信息的响应
 */
export const getUserCart = (userId) => {
  return axiosInstance.get(`${API_BASE_URL}/users/${userId}/cart`);
};

/**
 * 获取所有用户列表 (管理员专用)
 * @returns {Promise<AxiosResponse>} 包含用户列表的响应
 */
export const getAllUsers = () => {
  return axiosInstance.get(`${API_BASE_URL}/users`);
};

/**
 * 根据ID获取用户 (管理员专用)
 * @param {string} userId - 用户ID
 * @returns {Promise<AxiosResponse>} 包含用户信息的响应
 */
export const getUserById = (userId) => {
  return axiosInstance.get(`${API_BASE_URL}/users/${userId}`);
};

/**
 * 检查邮箱是否已注册
 * @param {string} email - 要检查的邮箱
 * @returns {Promise<AxiosResponse>} 检查结果响应
 */
export const checkEmailExists = (email) => {
  return axiosInstance.get(`${API_BASE_URL}/users/check-email`, {
    params: { email }
  });
};
