package com.adplatform.module.website.controller;

import com.adplatform.module.website.entity.Page;
import com.adplatform.module.website.service.PageService;
import com.adplatform.module.website.security.WebsitePermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pages")
public class PageController {

    @Autowired
    private PageService pageService;

    @PostMapping("/ad_spaces/{adSpaceId}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.PAGE_CREATE + "') and @websiteSecurityService.isAdSpaceOwner(#adSpaceId, principal)")
    public ResponseEntity<?> createPage(@PathVariable Long adSpaceId, @RequestBody Page page) {
        pageService.createPage(adSpaceId, page);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "message", "页面创建成功，待审核。",
                "pageId", page.getId(),
                "pageUrl", page.getUrl()
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.PAGE_UPDATE + "') and @websiteSecurityService.isPageOwner(#id, principal)")
    public ResponseEntity<?> updatePage(@PathVariable Long id, @RequestBody Page page) {
        pageService.updatePage(id, page);
        Page updatedPage = pageService.getPageById(id);
        return ResponseEntity.ok(
            Map.of(
                "message", "页面更新成功，待审核。",
                "pageUrl", updatedPage.getUrl()
            )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.PAGE_READ + "') and @websiteSecurityService.canAccessPage(#id, principal)")
    public ResponseEntity<?> getPage(@PathVariable Long id) {
        Page page = pageService.getPageById(id);
        if (page == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "页面不存在。"))
            );
        }
        return ResponseEntity.ok(page);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + WebsitePermissions.PAGE_READ + "')")
    public ResponseEntity<?> getPages(
            @RequestParam(required = false) Long adSpaceId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Page> pages = pageService.getPages(adSpaceId, status, page, size);
        return ResponseEntity.ok(
            Map.of(
                "currentPage", page,
                "pageSize", size,
                "items", pages
            )
        );
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.PAGE_APPROVE + "')")
    public ResponseEntity<?> approvePage(@PathVariable Long id) {
        pageService.approvePage(id);
        Page page = pageService.getPageById(id);
        return ResponseEntity.ok(
            Map.of(
                "message", "页面已审核通过。",
                "pageUrl", page.getUrl()
            )
        );
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.PAGE_APPROVE + "')")
    public ResponseEntity<?> rejectPage(@PathVariable Long id) {
        pageService.rejectPage(id);
        return ResponseEntity.ok(Map.of("message", "页面已审核拒绝。"));
    }
} 