package com.adplatform.module.delivery.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.service.AdvertisementService;
import com.adplatform.module.delivery.service.AdDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
     * 获取所有广告
     * @return
     */
    @GetMapping("/all")
    public Result<List<Advertisement>> getall(){
        List<Advertisement> list=advertisementService.getall();
        return Result.success(list);
    }
    /**
     * 获取广告内容
     * 这是一个公开接口
     */

    @GetMapping("/content/{id}")
    public Result<AdvertisementDTO> getAdContent(@PathVariable long id) {
        try {
            long a = id;
            // 获取广告内容
            AdvertisementDTO ad = advertisementService.getById(a);

            System.out.println(advertisementService.getById(a));

            if (ad == null) {
                return Result.error("没有可用的广告");
            }
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