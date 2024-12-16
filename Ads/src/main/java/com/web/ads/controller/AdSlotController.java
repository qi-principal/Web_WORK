package com.web.ads.controller;

import com.web.ads.entity.AdSlot;
import com.web.ads.service.AdSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 广告位控制器
 *
 * @author andrew
 * @date 2024/12/16
 */
@RestController
@RequestMapping("/api/ad-slots")
public class AdSlotController {

    @Autowired
    private AdSlotService adSlotService;

    /**
     * 创建广告位
     *
     * @param adSlot 广告位信息
     * @return 创建后的广告位信息
     */
    @PostMapping
    public ResponseEntity<?> createAdSlot(@RequestBody AdSlot adSlot) {
        try {
            AdSlot createdAdSlot = adSlotService.createAdSlot(adSlot);
            return ResponseEntity.ok(createdAdSlot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 更新广告位
     *
     * @param adSlotId 广告位ID
     * @param adSlot 广告位信息
     * @return 更新后的广告位信息
     */
    @PutMapping("/{adSlotId}")
    public ResponseEntity<?> updateAdSlot(
            @PathVariable Integer adSlotId,
            @RequestBody AdSlot adSlot) {
        try {
            adSlot.setAdSlotId(adSlotId);
            AdSlot updatedAdSlot = adSlotService.updateAdSlot(adSlot);
            return ResponseEntity.ok(updatedAdSlot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 获取广告位信息
     *
     * @param adSlotId 广告位ID
     * @return 广告位信息
     */
    @GetMapping("/{adSlotId}")
    public ResponseEntity<?> getAdSlotById(@PathVariable Integer adSlotId) {
        AdSlot adSlot = adSlotService.getAdSlotById(adSlotId);
        if (adSlot != null) {
            return ResponseEntity.ok(adSlot);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 删除广告位
     *
     * @param adSlotId 广告位ID
     * @return 操作结果
     */
    @DeleteMapping("/{adSlotId}")
    public ResponseEntity<?> deleteAdSlot(@PathVariable Integer adSlotId) {
        try {
            adSlotService.deleteAdSlot(adSlotId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 获取所有广告位
     *
     * @return 广告位列表
     */
    @GetMapping
    public ResponseEntity<List<AdSlot>> getAllAdSlots() {
        List<AdSlot> adSlots = adSlotService.getAllAdSlots();
        return ResponseEntity.ok(adSlots);
    }

    /**
     * 根据网站ID获取广告位列表
     *
     * @param websiteId 网站ID
     * @return 广告位列表
     */
    @GetMapping("/website/{websiteId}")
    public ResponseEntity<?> getAdSlotsByWebsiteId(@PathVariable Integer websiteId) {
        try {
            List<AdSlot> adSlots = adSlotService.getAdSlotsByWebsiteId(websiteId);
            return ResponseEntity.ok(adSlots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 检查广告位是否存在
     *
     * @param slotType 广告位类型
     * @param slotSize 广告位尺寸
     * @return 检查结果
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAdSlotExists(
            @RequestParam String slotType,
            @RequestParam String slotSize) {
        boolean exists = adSlotService.isAdSlotExists(slotType, slotSize);
        return ResponseEntity.ok(exists);
    }
} 