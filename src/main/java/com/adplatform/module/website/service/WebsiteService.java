package com.adplatform.module.website.service;

import com.adplatform.module.website.entity.Website;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface WebsiteService extends IService<Website> {
    /**
     * 创建网站
     */
    void createWebsite(Website website);

    /**
     * 更新网站信息
     */
    void updateWebsite(Long id, Website website);

    /**
     * 获取网站详情
     */
    Website getWebsiteById(Long id);

    /**
     * 获取网站列表
     */
    List<Website> getWebsites(Long userId, Integer status, int page, int size);

    /**
     * 审核通过网站
     */
    void approveWebsite(Long id);

    /**
     * 审核拒绝网站
     */
    void rejectWebsite(Long id);
} 