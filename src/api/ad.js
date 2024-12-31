import request from '@/utils/request'

// 获取广告列表
export function getAdList(params) {
  return request({
    url: 'public/ad-proxy/all',
    method: 'get',
    params
  })
}
// 获取广告列表
export function getSpaceAdList(params) {
  return request({
    url: '/v1/websites/all',
    method: 'get',
    params
  })
}

// 创建广告
export function createAd(data) {
  return request({
    url: '/v1/ads',
    method: 'post',
    data
  })
}

// 更新广告
export function updateAd(id, data) {
  return request({
    url: `/v1/ads/${id}`,
    method: 'put',
    data
  })
}

// 删除广告
export function deleteAd(id) {
  return request({
    url: `/v1/ads/${id}`,
    method: 'delete'
  })
}

// 获取广告详情
export function getAdDetail(id) {
  return request({
    url: `/v1/ads/${id}`,
    method: 'get'
  })
}

// 提交广告审核
export function submitAdReview(id) {
  return request({
    url: `/v1/ads/${id}/submit`,
    method: 'post'
  })
}
