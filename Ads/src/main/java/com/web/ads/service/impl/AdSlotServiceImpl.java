package com.web.ads.service.impl;

import com.web.ads.entity.AdSlot;
import com.web.ads.mapper.AdSlotMapper;
import com.web.ads.service.AdSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 广告位服务实现类
 *
 * @author andrew
 * @date 2024/12/16
 */
@Service
public class AdSlotServiceImpl implements AdSlotService {

    @Autowired
    private AdSlotMapper adSlotMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdSlot createAdSlot(AdSlot adSlot) {
        // 检查广告位是否已存在
        if (isAdSlotExists(adSlot.getSlotType(), adSlot.getSlotSize())) {
            throw new RuntimeException("相同类型和尺寸的广告位已存在");
        }
        
        adSlotMapper.insert(adSlot);
        return adSlot;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdSlot updateAdSlot(AdSlot adSlot) {
        AdSlot existingAdSlot = adSlotMapper.findById(adSlot.getAdSlotId());
        if (existingAdSlot == null) {
            throw new RuntimeException("广告位不存在");
        }

        // 如果修改了类型或尺寸，检查是否与其他广告位冲突
        if (!existingAdSlot.getSlotType().equals(adSlot.getSlotType()) 
            || !existingAdSlot.getSlotSize().equals(adSlot.getSlotSize())) {
            if (isAdSlotExists(adSlot.getSlotType(), adSlot.getSlotSize())) {
                throw new RuntimeException("相同类型和尺寸的广告位已存在");
            }
        }

        adSlotMapper.update(adSlot);
        return adSlot;
    }

    @Override
    public AdSlot getAdSlotById(Integer adSlotId) {
        return adSlotMapper.findById(adSlotId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdSlot(Integer adSlotId) {
        AdSlot adSlot = adSlotMapper.findById(adSlotId);
        if (adSlot == null) {
            throw new RuntimeException("广告位不存在");
        }
        adSlotMapper.delete(adSlotId);
    }

    @Override
    public List<AdSlot> getAllAdSlots() {
        return adSlotMapper.findAll();
    }

    @Override
    public boolean isAdSlotExists(String slotType, String slotSize) {
        return adSlotMapper.findByTypeAndSize(slotType, slotSize) != null;
    }
    
    /**
     * 根据网站ID获取广告位列表
     *
     * @param websiteId 网站ID
     * @return 广告位信息列表
     */
    @Override
    public List<AdSlot> getAdSlotsByWebsiteId(Integer websiteId) {
        return adSlotMapper.findByWebsiteId(websiteId);
    }
} 