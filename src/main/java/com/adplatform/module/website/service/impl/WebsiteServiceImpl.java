package com.adplatform.module.website.service.impl;

import com.adplatform.module.website.entity.Website;
import com.adplatform.module.website.mapper.WebsiteMapper;
import com.adplatform.module.website.service.WebsiteService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WebsiteServiceImpl extends ServiceImpl<WebsiteMapper, Website> implements WebsiteService {

    @Override
    @Transactional
    public void createWebsite(Website website) {
        // 检查用户是否已有网站
        Website existingWebsite = this.getWebsiteByUserId(website.getUserId());
        
        if (existingWebsite != null) {
            // 用户已有网站，执行更新操作
            existingWebsite.setName(website.getName());
            existingWebsite.setUrl(website.getUrl());
            existingWebsite.setDescription(website.getDescription());
            existingWebsite.setStatus(0); // 更新后重新审核
            existingWebsite.setUpdateTime(LocalDateTime.now());
            this.updateById(existingWebsite);
        } else {
            // 用户没有网站，执行创建操作
            website.setCreateTime(LocalDateTime.now());
            website.setUpdateTime(LocalDateTime.now());
            website.setStatus(0); // 待审核
            this.save(website);
        }
    }

    @Override
    @Transactional
    public void updateWebsite(Long id, Website website) {
        Website existing = this.getById(id);
        if (existing != null) {
            existing.setName(website.getName());
            existing.setUrl(website.getUrl());
            existing.setDescription(website.getDescription());
            existing.setStatus(0); // 更新后重新审核
            this.updateById(existing);
        }
    }

    @Override
    public Website getWebsiteById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<Website> getWebsites(Long userId, Integer status, int page, int size) {
        Page<Website> pageParam = new Page<>(page, size);
        return this.lambdaQuery()
                .eq(userId != null, Website::getUserId, userId)
                .eq(status != null, Website::getStatus, status)
                .page(pageParam)
                .getRecords();
    }

    @Override
    @Transactional
    public void approveWebsite(Long id) {
        Website website = this.getById(id);
        if (website != null) {
            website.setStatus(1); // 已审核
            this.updateById(website);
        }
    }

    @Override
    @Transactional
    public void rejectWebsite(Long id) {
        Website website = this.getById(id);
        if (website != null) {
            website.setStatus(2); // 已拒绝
            this.updateById(website);
        }
    }

    @Override
    public Website getWebsiteByUserId(Long userId) {
        return this.lambdaQuery()
                .eq(Website::getUserId, userId)
                .orderByDesc(Website::getCreateTime)
                .last("LIMIT 1")
                .one();
    }
} 