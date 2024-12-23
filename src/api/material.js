import request from '@/utils/request'

// 上传素材
export function uploadMaterial(data) {
  return request({
    url: '/v1/materials/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取素材列表
export function getMaterialList(adId) {
  return request({
    url: `/v1/materials/ad/${adId}`,
    method: 'get'
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