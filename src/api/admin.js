import request from '@/utils/request'

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/v1/admin/users',
    method: 'get',
    params
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

// 获取待审核广告列表
export function getPendingAdList(params) {
  return request({
    url: '/v1/admin/ads/pending',
    method: 'get',
    params
  })
}

// 审核通过广告
export function approveAd(id) {
  return request({
    url: `/v1/ads/${id}/approve`,
    method: 'post'
  })
}

// 拒绝广告
export function rejectAd(id, reason) {
  return request({
    url: `/v1/ads/${id}/reject`,
    method: 'post',
    data: { reason }
  })
}

// 获取待审核网站列表
export function getPendingWebsiteList(params) {
  return request({
    url: '/v1/admin/websites/pending',
    method: 'get',
    params
  })
}

// 审核通过网站
export function approveWebsite(id) {
  return request({
    url: `/v1/websites/${id}/approve`,
    method: 'post'
  })
}

// 拒绝网站
export function rejectWebsite(id, reason) {
  return request({
    url: `/v1/websites/${id}/reject`,
    method: 'post',
    data: { reason }
  })
}

// 获取待审核广告位列表
export function getPendingAdSpaceList(params) {
  return request({
    url: '/v1/admin/spaces/pending',
    method: 'get',
    params
  })
}

// 审核通过广告位
export function approveAdSpace(id) {
  return request({
    url: `/v1/spaces/${id}/approve`,
    method: 'post'
  })
}

// 拒绝广告位
export function rejectAdSpace(id, reason) {
  return request({
    url: `/v1/spaces/${id}/reject`,
    method: 'post',
    data: { reason }
  })
} 