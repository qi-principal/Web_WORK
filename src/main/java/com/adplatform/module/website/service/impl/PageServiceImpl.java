package com.adplatform.module.website.service.impl;

import com.adplatform.module.website.entity.Page;
import com.adplatform.module.website.mapper.PageMapper;
import com.adplatform.module.website.service.PageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, com.adplatform.module.website.entity.Page> implements PageService {

    @Override
    @Transactional
    public void createPage(Long adSpaceId, com.adplatform.module.website.entity.Page page) {
        page.setAdSpaceId(adSpaceId);
        page.setStatus(0); // 待审核
        this.save(page);
    }

    @Override
    @Transactional
    public void updatePage(Long id, com.adplatform.module.website.entity.Page page) {
        com.adplatform.module.website.entity.Page existing = this.getById(id);
        if (existing != null) {
            existing.setUrl(page.getUrl());
            existing.setContent(page.getContent());
            existing.setStatus(0); // 更新后重新审核
            this.updateById(existing);
        }
    }

    @Override
    public com.adplatform.module.website.entity.Page getPageById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<com.adplatform.module.website.entity.Page> getPages(Long adSpaceId, Integer status, int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.adplatform.module.website.entity.Page> pageParam = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
            
        return this.lambdaQuery()
                .eq(adSpaceId != null, com.adplatform.module.website.entity.Page::getAdSpaceId, adSpaceId)
                .eq(status != null, com.adplatform.module.website.entity.Page::getStatus, status)
                .page(pageParam)
                .getRecords();
    }

    @Override
    @Transactional
    public void approvePage(Long id) {
        com.adplatform.module.website.entity.Page page = this.getById(id);
        if (page != null) {
            page.setStatus(1); // 已审核
            this.updateById(page);
        }
    }

    @Override
    @Transactional
    public void rejectPage(Long id) {
        com.adplatform.module.website.entity.Page page = this.getById(id);
        if (page != null) {
            page.setStatus(2); // 已拒绝
            this.updateById(page);
        }
    }
} 