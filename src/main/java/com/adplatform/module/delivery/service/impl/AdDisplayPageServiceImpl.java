package com.adplatform.module.delivery.service.impl;

import com.adplatform.module.delivery.entity.AdDisplayPage;
import com.adplatform.module.delivery.mapper.AdDisplayPageMapper;
import com.adplatform.module.delivery.service.AdDisplayPageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 广告展示页面服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdDisplayPageServiceImpl implements AdDisplayPageService {

    private final AdDisplayPageMapper displayPageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdDisplayPage createDisplayPage(Long adSpaceId) {
        try {
            // 检查是否已存在展示页面
            AdDisplayPage existingPage = getDisplayPage(adSpaceId);
            if (existingPage != null) {
                log.info("广告位{}已存在展示页面", adSpaceId);
                return existingPage;
            }

            // 创建新的展示页面
            AdDisplayPage page = new AdDisplayPage();
            page.setAdSpaceId(adSpaceId);
            page.setUniquePath(generateUniquePath());
            page.setUrl(generatePageUrl(page.getUniquePath()));
            page.setStatus(1); // 启用状态

            // 保存页面信息
            displayPageMapper.insert(page);
            log.info("为广告位{}创建展示页面成功", adSpaceId);

            return page;
        } catch (Exception e) {
            log.error("创建广告展示页面失败：adSpaceId=" + adSpaceId, e);
            throw new RuntimeException("创建广告展示页面失败：" + e.getMessage());
        }
    }

    @Override
    public AdDisplayPage getDisplayPage(Long adSpaceId) {
        return displayPageMapper.selectOne(
            new LambdaQueryWrapper<AdDisplayPage>()
                .eq(AdDisplayPage::getAdSpaceId, adSpaceId)
                .last("LIMIT 1")
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePageStatus(Long pageId, Integer status) {
        AdDisplayPage page = displayPageMapper.selectById(pageId);
        if (page != null) {
            page.setStatus(status);
            displayPageMapper.updateById(page);
            log.info("更新展示页面状态成功：pageId={}, status={}", pageId, status);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePage(Long pageId) {
        displayPageMapper.deleteById(pageId);
        log.info("删除展示页面成功：pageId={}", pageId);
    }

    /**
     * 生成唯一路径
     */
    private String generateUniquePath() {
        LocalDateTime now = LocalDateTime.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ".html";
        return datePath + "/" + fileName;
    }

    /**
     * 生成页面URL
     */
    private String generatePageUrl(String uniquePath) {
        return "/ad/display/" + uniquePath;
    }
} 