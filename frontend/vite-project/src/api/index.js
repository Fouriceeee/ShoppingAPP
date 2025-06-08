import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api';
const instance = axios.create({
    baseURL: API_BASE_URL, // axios 实例的基础URL
    timeout: 10000, // 请求超时时间
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
        // 'Authorization': `Bearer ${localStorage.getItem('token')}` // 如果有认证需求
    }
});

// 请求拦截器
instance.interceptors.request.use(
    config => {
        // 添加认证token到请求头
        const token = localStorage.getItem('token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器
instance.interceptors.response.use(
    response => {
        // 可以在这里处理一些通用的响应逻辑，例如统一的成功消息处理
        return response;
    },
    error => {
        // 统一错误处理
        if (error.response) {
            const { status, data } = error.response;
            switch (status) {
                case 400:
                    console.error('Bad Request:', data.message || '请求参数错误');
                    // ElMessage.error(data.message || '请求参数错误');
                    break;
                case 401:
                    console.error('Unauthorized:', data.message || '未授权');
                    // ElMessage.error(data.message || '请登录');
                    // router.push('/login'); // 重定向到登录页
                    break;
                case 403:
                    console.error('Forbidden:', data.message || '无权限');
                    // ElMessage.error(data.message || '无权限访问');
                    break;
                case 404:
                    console.error('Not Found:', data.message || '资源不存在');
                    // ElMessage.error(data.message || '请求的资源不存在');
                    break;
                case 500:
                    console.error('Server Error:', data.message || '服务器内部错误');
                    // ElMessage.error(data.message || '服务器内部错误');
                    break;
                default:
                    console.error('HTTP Error:', data.message || '发生未知错误');
                // ElMessage.error(data.message || '发生未知错误');
            }
        } else if (error.request) {
            console.error('No response received:', error.request);
            // ElMessage.error('服务器无响应');
        } else {
            console.error('Error setting up request:', error.message);
            // ElMessage.error('请求发送失败');
        }
        return Promise.reject(error);
    }
);

export default instance; // 导出配置好的 axios 实例