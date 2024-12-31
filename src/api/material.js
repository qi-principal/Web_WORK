import request from '@/utils/request'

// 上传素材
export function uploadMaterial(data) {
  if (!(data instanceof FormData)) {
    return Promise.reject(new Error('上传数据格式错误'));
  }

  const userId = data.get('userId');
  if (!userId || userId === 'undefined') {
    return Promise.reject(new Error('用户ID不能为空'));
  }

  console.log('API-发起上传请求：', {
    url: '/v1/materials/upload',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
  
  // 确保FormData中包含content字段
  if (!data.has('content')) {
    data.append('content', '') // 如果没有content，添加一个空字符串作为默认值
  }
  
  return request({
    url: '/v1/materials/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(response => {
    console.log('API-上传请求成功响应：', response)
    return response
  }).catch(error => {
    console.error('API-上传请求失败：', error)
    throw error
  })
}

// 获取素材列表
export function getMaterialList(userId) {
  if (!userId || userId === 'undefined') {
    return Promise.reject(new Error('用户ID不能为空'));
  }
  return request({
    url: `/v1/materials/user/${userId}`,
    method: 'get'
  })
}

// 分页获取素材列表
export function getMaterialListByPage(userId, params) {
  if (!userId || userId === 'undefined') {
    return Promise.reject(new Error('用户ID不能为空'));
  }
  return request({
    url: `/v1/materials/user/${userId}/page`,
    method: 'get',
    params
  })
}

// 获取素材详情
export function getMaterialDetail(id) {
  return request({
    url: `/v1/materials/${id}`,
    method: 'get'
  })
}

// 删除素材
export function deleteMaterial(id) {
  return request({
    url: `/v1/materials/${id}`,
    method: 'delete'
  })
}

// 添加素材到广告
export function addMaterialToAd(materialId, adId) {
  return request({
    url: `/v1/materials/${materialId}/ads/${adId}`,
    method: 'post'
  })
}

// 从广告中移除素材
export function removeMaterialFromAd(materialId, adId) {
  return request({
    url: `/v1/materials/${materialId}/ads/${adId}`,
    method: 'delete'
  })
} 