package com.adplatform.module.website.service.impl;

import com.adplatform.module.delivery.entity.AdDisplayPage;
import com.adplatform.module.delivery.service.AdDisplayPageService;
import com.adplatform.module.website.entity.AdSpace;
import com.adplatform.module.website.mapper.AdSpaceMapper;
import com.adplatform.module.website.service.AdSpaceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 广告位服务实现类
 */
@Service
@RequiredArgsConstructor
public class AdSpaceServiceImpl extends ServiceImpl<AdSpaceMapper, AdSpace> implements AdSpaceService {

    private final AdDisplayPageService displayPageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAdSpace(Long websiteId, AdSpace adSpace) {
        System.out.println("1. 开始创建广告位，websiteId: " + websiteId);
        
        // 设置基本信息
        adSpace.setWebsiteId(websiteId);
        adSpace.setStatus(0); // 待审核
        
        // 设置时间
        Date now = new Date();
        adSpace.setCreateTime(now);
        adSpace.setUpdateTime(now);
        
        // 保存广告位信息
        boolean saveResult = this.save(adSpace);
        System.out.println("2. 广告位基本信息保存" + (saveResult ? "成功" : "失败") + "，广告位ID: " + adSpace.getId());
        
        try {
            // 创建广告展示页面
            System.out.println("3. 开始创建广告展示页面，广告位ID: " + adSpace.getId());

            
            AdDisplayPage displayPage = displayPageService.createDisplayPage(adSpace.getId());

            System.out.println("4. 广告展示页面创建成功，展示页面URL: " + displayPage.getUrl());
            

            // 生成并设置广告代码
            String iframeCode = generateIframeCode(displayPage.getUrl(), adSpace.getWidth(), adSpace.getHeight());
            System.out.println("5. 生成的广告代码: " + iframeCode);
            
            adSpace.setCode(iframeCode);
            adSpace.setUpdateTime(new Date());
            
            // 更新广告位信息
            boolean updateResult = this.updateById(adSpace);
            System.out.println("6. 更新广告位代码" + (updateResult ? "成功" : "失败"));
            
        } catch (Exception e) {
            System.out.println("创建广告位过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e;  // 重新抛出异常以触发事务回滚
        }
        
        System.out.println("7. 广告位创建完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdSpace(Long id, AdSpace adSpace) {
        AdSpace existing = this.getById(id);
        if (existing != null) {
            existing.setUpdateTime(new Date());
            existing.setName(adSpace.getName());
            existing.setWidth(adSpace.getWidth());
            existing.setHeight(adSpace.getHeight());
            existing.setStatus(0); // 更新后重新审核
            
            // 获取展示页面
            AdDisplayPage displayPage = displayPageService.getDisplayPage(id);
            if (displayPage != null) {
                // 更新广告代码
                existing.setCode(generateIframeCode(displayPage.getUrl(), adSpace.getWidth(), adSpace.getHeight()));
            }
            
            this.updateById(existing);
        }
    }

    @Override
    public AdSpace getAdSpaceById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<AdSpace> getAdSpaces(Long websiteId, Integer status, int page, int size) {
        Page<AdSpace> pageParam = new Page<>(page, size);
        return this.lambdaQuery()
                .eq(websiteId != null, AdSpace::getWebsiteId, websiteId)
                .eq(status != null, AdSpace::getStatus, status)
                .page(pageParam)
                .getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveAdSpace(Long id) {
        AdSpace adSpace = this.getById(id);
        if (adSpace != null) {
            adSpace.setStatus(1); // 已审核
            this.updateById(adSpace);
            adSpace.setUpdateTime(new Date());
            
            // 启用展示页面
            AdDisplayPage displayPage = displayPageService.getDisplayPage(id);
            if (displayPage != null) {
                displayPageService.updatePageStatus(displayPage.getId(), 1);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectAdSpace(Long id) {
        AdSpace adSpace = this.getById(id);
        if (adSpace != null) {
            adSpace.setStatus(2); // 已拒绝
            this.updateById(adSpace);
            adSpace.setUpdateTime(new Date());
            
            // 禁用展示页面
            AdDisplayPage displayPage = displayPageService.getDisplayPage(id);
            if (displayPage != null) {
                displayPageService.updatePageStatus(displayPage.getId(), 0);
            }
        }
    }

    @Override
    public String generateAdCode(Long adSpaceId) {
        // 获取广告位信息
        AdSpace adSpace = this.getById(adSpaceId);
        if (adSpace == null) {
            throw new RuntimeException("广告位不存在");
        }
        
        // 获取展示页面
        AdDisplayPage displayPage = displayPageService.getDisplayPage(adSpaceId);
        if (displayPage == null) {
            throw new RuntimeException("广告展示页面不存在");
        }
        
        // 生成广告代码
        return generateIframeCode(displayPage.getUrl(), adSpace.getWidth(), adSpace.getHeight());
    }

    /**
     * 生成iframe广告代码
     */
    private String generateIframeCode(String pageUrl, Integer width, Integer height) {
        String iframeCode = String.format("<iframe src=\"%s\" width=\"%d\" height=\"%d\" frameborder=\"0\"></iframe>",
                pageUrl, width, height);
        System.out.println("生成iframe代码: " + iframeCode);
        return iframeCode;
    }
} 