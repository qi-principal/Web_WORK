package com.adplatform.module.delivery.service.impl;

import com.adplatform.module.ad.dto.AdvertisementDTO;
import com.adplatform.module.ad.service.AdvertisementService;
import com.adplatform.module.delivery.entity.AdDeliveryTask;
import com.adplatform.module.delivery.service.AdContentCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的广告内容缓存服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisAdContentCacheService implements AdContentCacheService {

    private static final String AD_CONTENT_KEY_PREFIX = "ad:content:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    private final StringRedisTemplate redisTemplate;
    private final AdvertisementService advertisementService;
    private final ObjectMapper objectMapper;

    @Override
    public void cacheAdContent(Long adSpaceId, AdDeliveryTask task) {
        String key = getKey(adSpaceId);
        String content = generateAdContent(task);
        redisTemplate.opsForValue().set(key, content, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        log.info("缓存广告内容成功，adSpaceId={}", adSpaceId);
    }

    @Override
    public String getAdContent(Long adSpaceId) {
        String key = getKey(adSpaceId);
        String content = redisTemplate.opsForValue().get(key);
        if (content == null) {
            log.warn("广告内容缓存不存在，adSpaceId={}", adSpaceId);
        }
        return content;
    }

    @Override
    public void deleteAdContent(Long adSpaceId) {
        String key = getKey(adSpaceId);
        redisTemplate.delete(key);
        log.info("删除广告内容缓存成功，adSpaceId={}", adSpaceId);
    }

    /**
     * 生成Redis键
     */
    private String getKey(Long adSpaceId) {
        return AD_CONTENT_KEY_PREFIX + adSpaceId;
    }

    /**
     * 生成广告内容
     */
    private String generateAdContent(AdDeliveryTask task) {
        try {
            // 获取广告详情
            AdvertisementDTO ad = advertisementService.getById(task.getAdId());
            if (ad == null) {
                throw new RuntimeException("广告不存在，adId=" + task.getAdId());
            }
            if (ad.getMaterials() == null || ad.getMaterials().isEmpty()) {
                throw new RuntimeException("广告素材不存在，adId=" + task.getAdId());
            }

            // 根据广告类型生成不同的内容
            ObjectNode content;
            switch (ad.getType()) {
                case 1:
                    content = generateImageAdContent(ad, task);
                    break;
                case 2:
                    content = generateVideoAdContent(ad, task);
                    break;
                case 3:
                    content = generateTextAdContent(ad, task);
                    break;
                default:
                    throw new RuntimeException("不支持的广告类型：" + ad.getType());
            }

            return objectMapper.writeValueAsString(content);
        } catch (Exception e) {
            log.error("生成广告内容失败，taskId=" + task.getId(), e);
            throw new RuntimeException("生成广告内容失败：" + e.getMessage());
        }
    }

    /**
     * 生成图片广告内容
     */
    private ObjectNode generateImageAdContent(AdvertisementDTO ad, AdDeliveryTask task) {
        var material = ad.getMaterials().get(0);
        
        // 验证必要字段
        validateMaterial(material, "url");
        validateAdvertisement(ad, "clickUrl");
        
        ObjectNode root = objectMapper.createObjectNode();
        root.put("adId", task.getAdId());
        root.put("adSpaceId", task.getAdSpaceId());
        
        ObjectNode content = root.putObject("content");
        content.put("type", "image");
        content.put("title", StringUtils.hasText(ad.getTitle()) ? ad.getTitle() : "");
        content.put("description", StringUtils.hasText(ad.getDescription()) ? ad.getDescription() : "");
        content.put("imageUrl", material.getUrl());
        content.put("clickUrl", ad.getClickUrl());
        content.put("width", 800);
        content.put("height", 600);
        
        return root;
    }

    /**
     * 生成视频广告内容
     */
    private ObjectNode generateVideoAdContent(AdvertisementDTO ad, AdDeliveryTask task) {
        var material = ad.getMaterials().get(0);
        
        // 验证必要字段
        validateMaterial(material, "url", "coverUrl");
        validateAdvertisement(ad, "clickUrl");
        
        ObjectNode root = objectMapper.createObjectNode();
        root.put("adId", task.getAdId());
        root.put("adSpaceId", task.getAdSpaceId());
        
        ObjectNode content = root.putObject("content");
        content.put("type", "video");
        content.put("title", StringUtils.hasText(ad.getTitle()) ? ad.getTitle() : "");
        content.put("description", StringUtils.hasText(ad.getDescription()) ? ad.getDescription() : "");
        content.put("videoUrl", material.getUrl());
        content.put("clickUrl", ad.getClickUrl());
        return root;
    }

    /**
     * 生成文字广告内容
     */
    private ObjectNode generateTextAdContent(AdvertisementDTO ad, AdDeliveryTask task) {
        var material = ad.getMaterials().get(0);
        
        // 验证必要字段
        validateMaterial(material, "content");
        validateAdvertisement(ad, "clickUrl");
        
        ObjectNode root = objectMapper.createObjectNode();
        root.put("adId", task.getAdId());
        root.put("adSpaceId", task.getAdSpaceId());
        
        ObjectNode content = root.putObject("content");
        content.put("type", "text");
        content.put("title", StringUtils.hasText(ad.getTitle()) ? ad.getTitle() : "");
        content.put("description", StringUtils.hasText(ad.getDescription()) ? ad.getDescription() : "");
        content.put("content", material.getContent());
        content.put("clickUrl", ad.getClickUrl());
        
        return root;
    }

    /**
     * 验证素材必要字段
     */
    private void validateMaterial(Object material, String... requiredFields) {
        for (String field : requiredFields) {
            try {
                Object value = material.getClass().getMethod("get" + StringUtils.capitalize(field)).invoke(material);
                if (value == null || (value instanceof String && !StringUtils.hasText((String) value))) {
                    throw new RuntimeException("广告素材缺少必要字段：" + field);
                }
            } catch (Exception e) {
                throw new RuntimeException("广告素材字段验证失败：" + e.getMessage());
            }
        }
    }

    /**
     * 验证广告必要字段
     */
    private void validateAdvertisement(AdvertisementDTO ad, String... requiredFields) {
        for (String field : requiredFields) {
            try {
                Object value = ad.getClass().getMethod("get" + StringUtils.capitalize(field)).invoke(ad);
                if (value == null || (value instanceof String && !StringUtils.hasText((String) value))) {
                    throw new RuntimeException("广告缺少必要字段：" + field);
                }
            } catch (Exception e) {
                throw new RuntimeException("广告字段验证失败：" + e.getMessage());
            }
        }
    }
} 