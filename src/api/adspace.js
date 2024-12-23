import request from '@/utils/request'

// 获取广告位列表
export function getAdSpaceList(params) {
  return request({
    url: '/v1/websites/{websiteId}/spaces',
    method: 'get',
    params
  })
}

// 创建广告位
export function createAdSpace(websiteId, data) {
  return request({
    url: `/v1/websites/${websiteId}/spaces`,
    method: 'post',
    data
  })
}

// 获取广告位详情
export function getAdSpace(id) {
  return request({
    url: `/v1/spaces/${id}`,
    method: 'get'
  })
}

// 更新广告位
export function updateAdSpace(id, data) {
  return request({
    url: `/v1/spaces/${id}`,
    method: 'put',
    data
  })
}

// 获取广告位代码
export function getAdSpaceCode(id) {
  return request({
    url: `/v1/spaces/${id}/code`,
    method: 'get'
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
export function rejectAdSpace(id) {
  return request({
    url: `/v1/spaces/${id}/reject`,
    method: 'post'
  })
} 