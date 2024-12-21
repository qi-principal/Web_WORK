package com.adplatform.module.delivery.service;

import com.adplatform.module.delivery.entity.AdDeliveryTask;

/**
 * 广告内容缓存服务
 */
public interface AdContentCacheService {
    
    /**
     * 缓存广告内容
     */
    void cacheAdContent(Long adSpaceId, AdDeliveryTask task);

    /**
     * 获取广告内容
     */
    String getAdContent(Long adSpaceId);

    /**
     * 删除广告内容缓存
     */
    void deleteAdContent(Long adSpaceId);
} 