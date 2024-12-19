// API基础URL
const BASE_URL = '/api';

// API请求工具类
const api = {
    // 发送POST请求
    async post(url, data) {
        try {
            console.log('发送POST请求到:', BASE_URL + url);
            console.log('请求数据:', data);
            const response = await fetch(BASE_URL + url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
                },
                body: JSON.stringify(data)
            });
            const result = await response.json();
            console.log('响应结果:', result);
            return result;
        } catch (error) {
            console.error('请求错误:', error);
            throw error;
        }
    },

    // 发送GET请求
    async get(url) {
        try {
            console.log('发送GET请求到:', BASE_URL + url);
            const response = await fetch(BASE_URL + url, {
                method: 'GET',
                headers: {
                    'Authorization': localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
                }
            });
            const result = await response.json();
            console.log('响应结果:', result);
            return result;
        } catch (error) {
            console.error('请求错误:', error);
            throw error;
        }
    },

    // 发送PUT请求
    async put(url, data) {
        try {
            console.log('发送PUT请求到:', BASE_URL + url);
            console.log('请求数据:', data);
            const response = await fetch(BASE_URL + url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
                },
                body: JSON.stringify(data)
            });
            const result = await response.json();
            console.log('响应结果:', result);
            return result;
        } catch (error) {
            console.error('请求错误:', error);
            throw error;
        }
    }
};

// 用户相关API
const userApi = {
    // 用户注册
    async register(data) {
        return await api.post('/v1/auth/register', data);
    },

    // 用户登录
    async login(data) {
        return await api.post('/v1/auth/login', data);
    },

    // 获取当前用户信息
    async getCurrentUser() {
        return await api.get('/v1/users/me');
    },

    // 获取指定用户信息
    async getUserById(id) {
        return await api.get(`/v1/users/${id}`);
    },

    // 更新用户状态
    async updateStatus(id, status) {
        return await api.put(`/v1/users/${id}/status?status=${status}`);
    }
}; 