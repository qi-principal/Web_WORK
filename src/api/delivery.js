import request from '@/utils/request'

// 获取投放任务列表
export function getDeliveryTasks(params) {
  return request({
    url: '/v1/delivery/tasks',
    method: 'get',
    params
  })
}

// 创建投放任务
export function createDeliveryTask(data) {
  return request({
    url: '/v1/delivery/tasks',
    method: 'post',
    data
  })
}

// 更新投放任务状态
export function updateDeliveryTaskStatus(id, status) {
  return request({
    url: `/v1/delivery/tasks/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取广告投放数据统计
export function getAdStatistics(adId, params) {
  return request({
    url: `/v1/statistics/ads/${adId}`,
    method: 'get',
    params
  })
} 