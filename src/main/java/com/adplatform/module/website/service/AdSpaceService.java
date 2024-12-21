package com.adplatform.module.website.service;

import com.adplatform.module.website.entity.AdSpace;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface AdSpaceService extends IService<AdSpace> {
    /**
     * 创建广告位
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