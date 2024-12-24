package com.adplatform.module.delivery.service.impl;

import com.adplatform.module.ad.service.OssService;
import com.adplatform.module.delivery.entity.AdDisplayPage;
import com.adplatform.module.delivery.mapper.AdDisplayPageMapper;
import com.adplatform.module.delivery.service.AdDisplayPageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.exceptions.TemplateInputException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

/**
 * 广告展示页面服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdDisplayPageServiceImpl implements AdDisplayPageService {

    private static final String TEMPLATE_NAME = "ad_display";
    private static final String OSS_DIR = "ad/display";

    @Value("${ad.proxy.url:/public/ad-proxy}")
    private String proxyUrl;

    private final AdDisplayPageMapper displayPageMapper;
    private final OssService ossService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdDisplayPage createDisplayPage(Long adSpaceId) {
        try {
            log.info("开始创建广告展示页面，广告位ID: {}", adSpaceId);
            
            // 检查是否已存在展示页面
            AdDisplayPage existingPage = getDisplayPage(adSpaceId);
            if (existingPage != null) {
                log.info("广告位{}已存在展示页面", adSpaceId);
                return existingPage;
            }

            // 创建新的展示页面
            AdDisplayPage page = new AdDisplayPage();
            page.setAdSpaceId(adSpaceId);
            
            // 使用模板生成HTML内容
            String htmlContent = generateHtmlContent(adSpaceId);
            log.info("生成HTML内容完成");
            
            // 生成文件名
            String fileName = generateFileName(adSpaceId);
            log.info("生成文件名: {}", fileName);
            
            // 上传到OSS
            try {
                String ossUrl = ossService.uploadContent(
                    htmlContent,
                    fileName,
                    OSS_DIR,
                    "text/html"
                );
                log.info("上传到OSS成功，URL: {}", ossUrl);
                
                // 设置页面信息
                page.setUniquePath(OSS_DIR + "/" + fileName);
                page.setUrl(ossUrl);
                page.setStatus(1);
                page.setCreateTime(new Date());
                page.setUpdateTime(new Date());
                
            } catch (Exception e) {
                log.error("上传到OSS失败: {}", e.getMessage());
                throw new RuntimeException("上传到OSS失败", e);
            }

            // 保存页面信息
            int result = displayPageMapper.insert(page);
            log.info("保存展示页面{}，页面ID: {}", result > 0 ? "成功" : "失败", page.getId());

            return page;
            
        } catch (Exception e) {
            log.error("创建广告展示页面失败：{}", e.getMessage(), e);
            throw new RuntimeException("创建广告展示页面失败：" + e.getMessage());
        }
    }

    /**
     * 生成HTML内容
     */
    private String generateHtmlContent(Long adSpaceId) {
        try {
            // 创建模板上下文
            Context context = new Context();
            context.setVariable("adSpaceId", adSpaceId);
            context.setVariable("proxyUrl", proxyUrl);
            
            // 使用模板引擎处理模板
            String content = templateEngine.process(TEMPLATE_NAME, context);
            if (content == null || content.isEmpty()) {
                throw new RuntimeException("模板生成的内容为空");
            }
            return content;
        } catch (TemplateInputException e) {
            log.error("模板文件不存在或无法访问: {}", TEMPLATE_NAME, e);
            throw new RuntimeException("模板处理失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("生成HTML内容失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成HTML内容失败：" + e.getMessage());
        }
    }

    /**
     * 生成文件名
     * 格式：display_page_{广告位ID}_{年月日}_{时分秒}_{环境标识}_{随机数}.html
     * 示例：display_page_123_20231224_153022_prod_7a8b9c.html
     */
    private String generateFileName(Long adSpaceId) {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String env = System.getProperty("spring.profiles.active", "prod");
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6);

        return String.format("display_page_%d_%s_%s_%s_%s.html",
                adSpaceId,
                date,
                time,
                env,
                randomSuffix);
    }

    @Override
    public AdDisplayPage getDisplayPage(Long adSpaceId) {
        System.out.println("查询广告位" + adSpaceId + "的展示页面");
        AdDisplayPage page = displayPageMapper.selectOne(
            new LambdaQueryWrapper<AdDisplayPage>()
                .eq(AdDisplayPage::getAdSpaceId, adSpaceId)
                .last("LIMIT 1")
        );
        System.out.println("查询结果: " + (page != null ? "找到展示页面" : "未找到展示页面"));
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePageStatus(Long pageId, Integer status) {
        AdDisplayPage page = displayPageMapper.selectById(pageId);
        if (page != null) {
            page.setStatus(status);
            page.setUpdateTime(new Date());
            displayPageMapper.updateById(page);
            log.info("更新展示页面状态成功：pageId={}, status={}", pageId, status);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePage(Long pageId) {
        AdDisplayPage page = displayPageMapper.selectById(pageId);
        if (page != null) {
            // 从OSS删除文件
            try {
                ossService.delete(page.getUrl());
                System.out.println("从OSS删除文件成功：" + page.getUrl());
            } catch (Exception e) {
                System.out.println("从OSS删除文件失败：" + e.getMessage());
            }
            
            // 从数据库删除记录
            displayPageMapper.deleteById(pageId);
            log.info("删除展示页面成功：pageId={}", pageId);
        }
    }
} 