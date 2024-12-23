import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/v1/auth/login',
    method: 'post',
    data
  })
}

// 注册
export function register(data) {
  return request({
    url: '/v1/auth/register',
    method: 'post',
    data
  })
}

// 获取当前用户信息
export function getUserInfo() {
  return request({
    url: '/v1/users/me',
    method: 'get'
  })
}

// 获取用户信息
export function getUserById(id) {
  return request({
    url: `/v1/users/${id}`,
    method: 'get'
  })
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request({
    url: `/v1/users/${id}/status`,
    method: 'put',
    params: { status }
  })
} 