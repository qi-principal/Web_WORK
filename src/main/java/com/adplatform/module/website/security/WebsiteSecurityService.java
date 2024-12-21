package com.adplatform.module.website.security;

import com.adplatform.module.user.security.UserPrincipal;
import com.adplatform.module.website.entity.AdSpace;
import com.adplatform.module.website.entity.Website;
import com.adplatform.module.website.service.AdSpaceService;
import com.adplatform.module.website.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("websiteSecurityService")
public class WebsiteSecurityService {

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private AdSpaceService adSpaceService;

    /**
     * 检查用户是否是网站的所有者
     */
    public boolean isWebsiteOwner(Long websiteId, Authentication authentication) {
        Website website = websiteService.getWebsiteById(websiteId);
        if (website == null) {
            return false;
        }
        
        // 获取当前用户ID
        Long currentUserId = getCurrentUserId(authentication);
        return website.getUserId().equals(currentUserId);
    }

    /**
     * 检查用户是否是广告位的所有者
     */
    public boolean isAdSpaceOwner(Long adSpaceId, Authentication authentication) {
        AdSpace adSpace = adSpaceService.getAdSpaceById(adSpaceId);
        if (adSpace == null) {
            return false;
        }
        
        return isWebsiteOwner(adSpace.getWebsiteId(), authentication);
    }

    /**
     * 检查用户是否可以访问网站
     */
    public boolean canAccessWebsite(Long websiteId, Authentication authentication) {
        // 如果是管理员,可以访问所有网站
        if (hasRole(authentication, WebsitePermissions.ROLE_ADMIN)) {
            return true;
        }
        
        // 如果是网站所有者,可以访问自己的网站
        return isWebsiteOwner(websiteId, authentication);
    }

    /**
     * 检查用户是否可以访问广告位
     */
    public boolean canAccessAdSpace(Long adSpaceId, Authentication authentication) {
        // 如果是管理员,可以访问所有广告位
        if (hasRole(authentication, WebsitePermissions.ROLE_ADMIN)) {
            return true;
        }
        
        // 如果是广告位所有者,可以访问自己的广告位
        return isAdSpaceOwner(adSpaceId, authentication);
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || 
            !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new RuntimeException("用户未登录或认证信息无效");
        }
        return ((UserPrincipal) authentication.getPrincipal()).getId();
    }

    /**
     * 检查用户是否具有指定角色
     */
    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
} 