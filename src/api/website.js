import request from '@/utils/request'

// 获取网站列表
export function getWebsiteList(params) {
  return request({
    url: '/v1/websites',
    method: 'get',
    params
  })
}

// 创建网站
export function createWebsite(data) {
  return request({
    url: '/v1/websites',
    method: 'post',
    data
  })
}

// 获取网站详情
export function getWebsite(id) {
  return request({
    url: `/v1/websites/${id}`,
    method: 'get'
  })
}

// 更新网站
export function updateWebsite(id, data) {
  return request({
    url: `/v1/websites/${id}`,
    method: 'put',
    data
  })
}

// 删除网站
export function deleteWebsite(id) {
  return request({
    url: `/v1/websites/${id}`,
    method: 'delete'
  })
}

// 提交网站审核
export function submitWebsiteReview(id) {
  return request({
    url: `/v1/websites/${id}/submit`,
    method: 'post'
  })
} 