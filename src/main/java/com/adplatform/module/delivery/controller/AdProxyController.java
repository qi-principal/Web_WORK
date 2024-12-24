package com.adplatform.module.delivery.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.service.AdvertisementService;
import com.adplatform.module.delivery.service.AdDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 广告代理控制器
 * 处理来自广告展示页面的公开请求
 */
@Slf4j
@RestController
@RequestMapping("/public/ad-proxy")
@RequiredArgsConstructor
public class AdProxyController {

    private final AdvertisementService advertisementService;
    private final AdDeliveryService deliveryService;

    /**
     * 获取广告内容
     * 这是一个公开接口
     */
    @GetMapping("/content/{adSpaceId}")
    public Result<AdvertisementDTO> getAdContent(@PathVariable Long adSpaceId) {
        try {
            // 获取广告内容
            AdvertisementDTO ad = advertisementService.getById(adSpaceId);
            if (ad == null) {
                return Result.error("没有可用的广告");
            }

            // 处理广告内容，移除敏感信息
            ad.setUserId(null);  // 移除广告主ID
            ad.setBudget(null);  // 移除预算信息
            ad.setDailyBudget(null);

            return Result.success(ad);
        } catch (Exception e) {
            log.error("获取广告内容失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 处理广告点击
     * 记录点击并重定向到目标URL
     */
    @GetMapping("/click/{adId}")
    public void handleClick(
            @PathVariable Long adId,
            @RequestParam Long adSpaceId,
            HttpServletResponse response) throws IOException {
        try {

            // 获取广告信息
            AdvertisementDTO ad = advertisementService.getById(adId);
            if (ad != null && ad.getClickUrl() != null) {
                // 重定向到目标URL
                response.sendRedirect(ad.getClickUrl());
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "广告不存在或目标URL未设置");
            }
        } catch (Exception e) {
            log.error("处理广告点击失败: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}