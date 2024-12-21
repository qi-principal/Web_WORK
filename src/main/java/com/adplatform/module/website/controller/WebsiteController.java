package com.adplatform.module.website.controller;

import com.adplatform.module.website.entity.Website;
import com.adplatform.module.website.service.WebsiteService;
import com.adplatform.module.website.security.WebsitePermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/websites")
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;

    /**
     * 创建网站
     * @param website
     * @return
    */
    @PostMapping
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_CREATE + "')")
    public ResponseEntity<?> createWebsite(@RequestBody Website website) {
        websiteService.createWebsite(website);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "message", "网站创建成功，待审核。",
                "websiteId", website.getId()
            )
        );
    }

    /**
     * 更新网站
     * @param id
     * @param website
     * @return
    */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_UPDATE + "') and @websiteSecurityService.isWebsiteOwner(#id, principal)")
    public ResponseEntity<?> updateWebsite(@PathVariable Long id, @RequestBody Website website) {
        websiteService.updateWebsite(id, website);
        return ResponseEntity.ok(Map.of("message", "网站更新成功，待审核。"));
    }

    /**
     * 获取网站
     * @param id
     * @return
    */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_READ + "') and @websiteSecurityService.canAccessWebsite(#id, principal)")
    public ResponseEntity<?> getWebsite(@PathVariable Long id) {
        Website website = websiteService.getWebsiteById(id);
        if (website == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "网站不存在。"))
            );
        }
        return ResponseEntity.ok(website);
    }

    /**
     * 获取网站列表
     * @param userId
     * @param status
     * @param page
     * @param size
     * @return
    */
    @GetMapping
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_READ + "')")
    public ResponseEntity<?> getWebsites(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Website> websites = websiteService.getWebsites(userId, status, page, size);
        return ResponseEntity.ok(
            Map.of(
                "currentPage", page,
                "pageSize", size,
                "items", websites
            )
        );
    }

    /**
     * 审核通过网站
     * @param id
     * @return
    */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_APPROVE + "')")
    public ResponseEntity<?> approveWebsite(@PathVariable Long id) {
        websiteService.approveWebsite(id);
        return ResponseEntity.ok(Map.of("message", "网站已审核通过。"));
    }

    /**
     * 审核通过网站
     * @param id
     * @return
    */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.WEBSITE_APPROVE + "')")
    public ResponseEntity<?> rejectWebsite(@PathVariable Long id) {
        websiteService.rejectWebsite(id);
        return ResponseEntity.ok(Map.of("message", "网站已审核拒绝。"));
    }
} 