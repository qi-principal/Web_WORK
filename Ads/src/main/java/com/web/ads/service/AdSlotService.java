package com.web.ads.service;

import com.web.ads.entity.AdSlot;
import java.util.List;

/**
 * 广告位服务接口
 *
 * @author andrew
 * @date 2024/12/16
 */
public interface AdSlotService {
    
    /**
     * 创建广告位
     *
     * @param adSlot 广告位信息
     * @return 创建后的广告位信息
     */
    AdSlot createAdSlot(AdSlot adSlot);

    /**
     * 更新广告位
     *
     * @param adSlot 广告位信息
     * @return 更新后的广告位信息
     */
    AdSlot updateAdSlot(AdSlot adSlot);

    /**
     * 获取广告位信息
     *
     * @param adSlotId 广告位ID
     * @return 广告位信息
     */
    AdSlot getAdSlotById(Integer adSlotId);

    /**
     * 删除广告位
     *
     * @param adSlotId 广告位ID
     */
    void deleteAdSlot(Integer adSlotId);

    /**
     * 获取所有广告位
     *
     * @return 广告位列表
     */
    List<AdSlot> getAllAdSlots();

    /**
     * 检查广告位是否已存在
     *
     * @param slotType 广告位类型
     * @param slotSize 广告位尺寸
     * @return true-存在，false-不存在
     */
    boolean isAdSlotExists(String slotType, String slotSize);

    /**
     * 根据网站ID获取广告位列表
     *
     * @param websiteId 网站ID
     * @return 广告位信息列表
     */
    List<AdSlot> getAdSlotsByWebsiteId(Integer websiteId);
} 