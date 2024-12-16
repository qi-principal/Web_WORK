package com.web.ads.service;

import com.web.ads.entity.Website;
import java.util.List;

/**
 * 网站信息服务接口
 *
 * @author andrew
 * @date 2024/12/16
 */
public interface WebsiteService {
    
    /**
     * 创建网站信息
     *
     * @param website 网站信息
     * @return 创建后的网站信息
     */
    Website createWebsite(Website website);

    /**
     * 更新网站信息
     *
     * @param website 网站信息
     * @return 更新后的网站信息
     */
    Website updateWebsite(Website website);

    /**
     * 获取网站信息
     *
     * @param websiteId 网站ID
     * @return 网站信息
     */
    Website getWebsiteById(Integer websiteId);

    /**
     * 删除网站信息
     *
     * @param websiteId 网站ID
     */
    void deleteWebsite(Integer websiteId);

    /**
     * 获取所有网站信息
     *
     * @return 网站信息列表
     */
    List<Website> getAllWebsites();

    /**
     * 检查网站URL是否已存在
     *
     * @param websiteUrl 网站URL
     * @return true-存在，false-不存在
     */
    boolean isUrlExists(String websiteUrl);
} 