package com.web.ads.service.impl;

import com.web.ads.entity.Website;
import com.web.ads.mapper.WebsiteMapper;
import com.web.ads.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 网站信息服务实现类
 *
 * @author andrew
 * @date 2024/12/16
 */
@Service
public class WebsiteServiceImpl implements WebsiteService {

    @Autowired
    private WebsiteMapper websiteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Website createWebsite(Website website) {
        // 检查URL是否已存在
        if (isUrlExists(website.getWebsiteUrl())) {
            throw new RuntimeException("网站URL已存在");
        }
        
        websiteMapper.insert(website);
        return website;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Website updateWebsite(Website website) {
        Website existingWebsite = websiteMapper.findById(website.getWebsiteId());
        if (existingWebsite == null) {
            throw new RuntimeException("网站不存在");
        }

        // 如果修改了URL，检查新URL是否已存在
        if (!existingWebsite.getWebsiteUrl().equals(website.getWebsiteUrl()) 
            && isUrlExists(website.getWebsiteUrl())) {
            throw new RuntimeException("新网站URL已存在");
        }

        websiteMapper.update(website);
        return website;
    }

    @Override
    public Website getWebsiteById(Integer websiteId) {
        return websiteMapper.findById(websiteId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWebsite(Integer websiteId) {
        Website website = websiteMapper.findById(websiteId);
        if (website == null) {
            throw new RuntimeException("网站不存在");
        }
        websiteMapper.delete(websiteId);
    }

    @Override
    public List<Website> getAllWebsites() {
        return websiteMapper.findAll();
    }

    @Override
    public boolean isUrlExists(String websiteUrl) {
        return websiteMapper.findByUrl(websiteUrl) != null;
    }
} 