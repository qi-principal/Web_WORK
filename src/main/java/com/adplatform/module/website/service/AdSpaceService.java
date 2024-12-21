package com.adplatform.module.website.service;

import com.adplatform.module.website.entity.AdSpace;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface AdSpaceService extends IService<AdSpace> {
    /**
     * 创建广告位
     * 
     * @param websiteId 网站ID
     * @param adSpace 广告位信息
     * 
     * 创建过程包括：
     * 1. 保存广告位基本信息
     * 2. 自动创建对应的广告展示页面
     * 3. 生成广告展示代码
     */
    void createAdSpace(Long websiteId, AdSpace adSpace);

    /**
     * 更新广告位信息
     */
    void updateAdSpace(Long id, AdSpace adSpace);

    /**
     * 获取广告位详情
     */
    AdSpace getAdSpaceById(Long id);

    /**
     * 获取广告位列表
     */
    List<AdSpace> getAdSpaces(Long websiteId, Integer status, int page, int size);

    /**
     * 审核通过广告位
     */
    void approveAdSpace(Long id);

    /**
     * 审核拒绝广告位
     */
    void rejectAdSpace(Long id);

    /**
     * 生成广告位代码
     */
    String generateAdCode(Long adSpaceId);
} 