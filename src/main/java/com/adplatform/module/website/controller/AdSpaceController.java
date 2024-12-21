package com.adplatform.module.website.controller;

import com.adplatform.module.website.entity.AdSpace;
import com.adplatform.module.website.service.AdSpaceService;
import com.adplatform.module.website.security.WebsitePermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AdSpaceController {

    @Autowired
    private AdSpaceService adSpaceService;

    @PostMapping("/websites/{websiteId}/spaces")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_CREATE + "') and @websiteSecurityService.isWebsiteOwner(#websiteId, principal)")
    public ResponseEntity<?> createAdSpace(@PathVariable Long websiteId, @RequestBody AdSpace adSpace) {
        adSpaceService.createAdSpace(websiteId, adSpace);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "message", "广告位创建成功，待审核。",
                "adSpaceId", adSpace.getId(),
                "adCode", adSpace.getCode()
            )
        );
    }

    @PutMapping("/spaces/{id}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_UPDATE + "') and @websiteSecurityService.isAdSpaceOwner(#id, principal)")
    public ResponseEntity<?> updateAdSpace(@PathVariable Long id, @RequestBody AdSpace adSpace) {
        adSpaceService.updateAdSpace(id, adSpace);
        AdSpace updatedAdSpace = adSpaceService.getAdSpaceById(id);
        return ResponseEntity.ok(
            Map.of(
                "message", "广告位更新成功，待审核。",
                "adCode", updatedAdSpace.getCode()
            )
        );
    }

    @GetMapping("/spaces/{id}")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_READ + "') and @websiteSecurityService.canAccessAdSpace(#id, principal)")
    public ResponseEntity<?> getAdSpace(@PathVariable Long id) {
        AdSpace adSpace = adSpaceService.getAdSpaceById(id);
        if (adSpace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "广告位不存在。"))
            );
        }
        return ResponseEntity.ok(adSpace);
    }

    @GetMapping("/websites/{websiteId}/spaces")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_READ + "') and @websiteSecurityService.canAccessWebsite(#websiteId, principal)")
    public ResponseEntity<?> getAdSpaces(
            @PathVariable Long websiteId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<AdSpace> adSpaces = adSpaceService.getAdSpaces(websiteId, status, page, size);
        return ResponseEntity.ok(
            Map.of(
                "currentPage", page,
                "pageSize", size,
                "items", adSpaces
            )
        );
    }

    @GetMapping("/spaces/{id}/code")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_READ + "') and @websiteSecurityService.canAccessAdSpace(#id, principal)")
    public ResponseEntity<?> getAdCode(@PathVariable Long id) {
        AdSpace adSpace = adSpaceService.getAdSpaceById(id);
        if (adSpace == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "广告位不存在。"))
            );
        }
        return ResponseEntity.ok(Map.of("adCode", adSpace.getCode()));
    }

    @PostMapping("/spaces/{id}/approve")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_APPROVE + "')")
    public ResponseEntity<?> approveAdSpace(@PathVariable Long id) {
        adSpaceService.approveAdSpace(id);
        AdSpace adSpace = adSpaceService.getAdSpaceById(id);
        return ResponseEntity.ok(
            Map.of(
                "message", "广告位已审核通过。",
                "adCode", adSpace.getCode()
            )
        );
    }

    @PostMapping("/spaces/{id}/reject")
    @PreAuthorize("hasAuthority('" + WebsitePermissions.AD_SPACE_APPROVE + "')")
    public ResponseEntity<?> rejectAdSpace(@PathVariable Long id) {
        adSpaceService.rejectAdSpace(id);
        return ResponseEntity.ok(Map.of("message", "广告位已审核拒绝。"));
    }
} 