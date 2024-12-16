package com.web.ads.controller;

import com.web.ads.entity.Website;
import com.web.ads.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 网站信息控制器
 *
 * @author andrew
 * @date 2024/12/16
 */
@RestController
@RequestMapping("/api/websites")
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;

    /**
     * 创建网站信息
     *
     * @param website 网站信息
     * @return 创建后的网站信息
     */
    @PostMapping
    public ResponseEntity<?> createWebsite(@RequestBody Website website) {
        try {
            Website createdWebsite = websiteService.createWebsite(website);
            return ResponseEntity.ok(createdWebsite);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 更新网站信息
     *
     * @param websiteId 网站ID
     * @param website 网站信息
     * @return 更新后的网站信息
     */
    @PutMapping("/{websiteId}")
    public ResponseEntity<?> updateWebsite(
            @PathVariable Integer websiteId,
            @RequestBody Website website) {
        try {
            website.setWebsiteId(websiteId);
            Website updatedWebsite = websiteService.updateWebsite(website);
            return ResponseEntity.ok(updatedWebsite);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 获取网站信息
     *
     * @param websiteId 网站ID
     * @return 网站信息
     */
    @GetMapping("/{websiteId}")
    public ResponseEntity<?> getWebsiteById(@PathVariable Integer websiteId) {
        Website website = websiteService.getWebsiteById(websiteId);
        if (website != null) {
            return ResponseEntity.ok(website);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 删除网站信息
     *
     * @param websiteId 网站ID
     * @return 操作结果
     */
    @DeleteMapping("/{websiteId}")
    public ResponseEntity<?> deleteWebsite(@PathVariable Integer websiteId) {
        try {
            websiteService.deleteWebsite(websiteId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 获取所有网站信息
     *
     * @return 网站信息列表
     */
    @GetMapping
    public ResponseEntity<List<Website>> getAllWebsites() {
        List<Website> websites = websiteService.getAllWebsites();
        return ResponseEntity.ok(websites);
    }

    /**
     * 检查URL是否存在
     *
     * @param url 网站URL
     * @return 检查结果
     */
    @GetMapping("/check-url")
    public ResponseEntity<Boolean> checkUrlExists(@RequestParam String url) {
        boolean exists = websiteService.isUrlExists(url);
        return ResponseEntity.ok(exists);
    }
} 