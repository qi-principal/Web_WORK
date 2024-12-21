package com.adplatform.module.delivery.service;

import com.adplatform.module.delivery.entity.AdDisplayPage;

/**
 * 广告展示页面服务接口
 */
public interface AdDisplayPageService {

    /**
     * 为广告位创建展示页面
     *
     * @param adSpaceId 广告位ID
     * @return 创建的展示页面
     */
    AdDisplayPage createDisplayPage(Long adSpaceId);

    /**
     * 获取广告位的展示页面
     *
     * @param adSpaceId 广告位ID
     * @return 展示页面
     */
    AdDisplayPage getDisplayPage(Long adSpaceId);

    /**
     * 更新展示页面状态
     *
     * @param pageId 页面ID
     * @param status 状态
     */
    void updatePageStatus(Long pageId, Integer status);

    /**
     * 删除展示页面
     *
     * @param pageId 页面ID
     */
    void deletePage(Long pageId);
} 