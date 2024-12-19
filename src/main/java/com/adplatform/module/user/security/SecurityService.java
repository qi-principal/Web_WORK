package com.adplatform.module.user.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 安全服务类
 *
 * @author andrew
 * @date 2023-11-21
 */
@Slf4j
@Service("securityService")
public class SecurityService {

    /**
     * 检查是否是当前用户
     *
     * @param userId 用户ID
     * @return 是否是当前用户
     */
    public boolean isCurrentUser(Long userId) {
        log.info("检查是否是当前用户，目标用户ID: {}", userId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("用户未认证");
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            log.warn("认证主体不是UserPrincipal类型");
            return false;
        }

        boolean isCurrentUser = ((UserPrincipal) principal).getId().equals(userId);
        log.info("用户身份检查结果: {}, 当前用户ID: {}, 目标用户ID: {}", 
            isCurrentUser, ((UserPrincipal) principal).getId(), userId);
        return isCurrentUser;
    }
} 