package com.adplatform.module.delivery.executor;

import com.adplatform.module.delivery.entity.AdDeliveryTask;
import com.adplatform.module.delivery.entity.AdDisplayPage;
import com.adplatform.module.delivery.mapper.AdDisplayPageMapper;
import com.adplatform.module.delivery.service.AdContentCacheService;
import com.adplatform.module.delivery.service.CdnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 广告投放执行器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdDeliveryExecutor {

    private final AdDisplayPageMapper displayPageMapper;
    private final AdContentCacheService contentCacheService;
    private final TemplateEngine templateEngine;
    private final CdnService cdnService;

    /**
     * 执行广告投放
     */
    public String execute(AdDeliveryTask task) {
        log.info("开始执行广告投放任务，taskId={}", task.getId());
        try {
            // 1. 检查广告位是否存在展示页面
            AdDisplayPage displayPage = displayPageMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdDisplayPage>()
                            .eq(AdDisplayPage::getAdSpaceId, task.getAdSpaceId())
            );

            // 2. 如果展示页面不存在，创建新的展示页面
            if (displayPage == null) {
                displayPage = createDisplayPage(task.getAdSpaceId());
                displayPageMapper.insert(displayPage);
                log.info("创建广告展示页面成功，pageId={}", displayPage.getId());
            }

            // 3. 更新展示页面的广告内容
            String cdnUrl = updateDisplayPageContent(displayPage, task);
            
            // 4. 更新展示页面的URL
            displayPage.setUrl(cdnUrl);
            displayPageMapper.updateById(displayPage);
            log.info("更新广告展示页面URL成功，pageId={}", displayPage.getId());

            // 5. 记录投放数据
            recordDeliveryData(task);

            return "投放成功：广告已更新到CDN " + cdnUrl;
        } catch (Exception e) {
            log.error("执行广告投放任务失败，taskId=" + task.getId(), e);
            throw new RuntimeException("执行广告投放任务失败：" + e.getMessage());
        }
    }

    /**
     * 创建广告展示页面
     */
    private AdDisplayPage createDisplayPage(Long adSpaceId) {
        AdDisplayPage page = new AdDisplayPage();
        page.setAdSpaceId(adSpaceId);
        page.setUniquePath(generateUniquePath());
        return page;
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
     * 更新展示页面内容
     */
    private String updateDisplayPageContent(AdDisplayPage page, AdDeliveryTask task) {
        try {
            // 1. 更新Redis缓存中的广告内容
            contentCacheService.cacheAdContent(task.getAdSpaceId(), task);

            // 2. 生成静态HTML页面
            Context context = new Context();
            context.setVariable("adContent", contentCacheService.getAdContent(task.getAdSpaceId()));
            String html = templateEngine.process("ad_display", context);

            // 3. 上传到CDN
            String cdnUrl = cdnService.uploadFile(html, page.getUniquePath());
            log.info("上传广告展示页面到CDN成功，pageId={}, url={}", page.getId(), cdnUrl);

            return cdnUrl;
        } catch (Exception e) {
            log.error("更新广告展示页面内容失败，pageId=" + page.getId(), e);
            throw new RuntimeException("更新广告展示页面内容失败：" + e.getMessage());
        }
    }

    /**
     * 记录投放数据
     */
    private void recordDeliveryData(AdDeliveryTask task) {
        // TODO: 实现投放数据记录逻辑
        String impressionKey = String.format("ad:stats:impression:%d:%d", task.getAdId(), task.getAdSpaceId());
        String clickKey = String.format("ad:stats:click:%d:%d", task.getAdId(), task.getAdSpaceId());
        log.info("初始化投放数据记录成功，taskId={}", task.getId());
    }
} 