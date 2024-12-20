package com.adplatform.module.user.security;

import com.adplatform.module.user.entity.User;
import com.adplatform.module.website.security.WebsitePermissions;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义用户认证信息类
 *
 * @author andrew
 * @date 2023-11-21
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.getStatus() == 1;
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 根据用户类型设置角色和权限
        switch (user.getUserType()) {
            case 1: // 广告主
                authorities.add(new SimpleGrantedAuthority("ROLE_ADVERTISER"));
                break;
                
            case 2: // 网站主
                authorities.add(new SimpleGrantedAuthority("ROLE_PUBLISHER"));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.ROLE_WEBSITE_OWNER));
                // 添加网站相关权限
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.WEBSITE_CREATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.WEBSITE_UPDATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.WEBSITE_READ));
                // 添加广告位相关权限
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.AD_SPACE_CREATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.AD_SPACE_UPDATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.AD_SPACE_READ));
                // 添加页面相关权限
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.PAGE_CREATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.PAGE_UPDATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.PAGE_READ));
                break;
                
            case 3: // 管理员
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                // 添加所有网站相关权限
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.WEBSITE_CREATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.WEBSITE_UPDATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.WEBSITE_READ));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.WEBSITE_APPROVE));
                // 添加所有广告位相关权限
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.AD_SPACE_CREATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.AD_SPACE_UPDATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.AD_SPACE_READ));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.AD_SPACE_APPROVE));
                // 添加所有页面相关权限
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.PAGE_CREATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.PAGE_UPDATE));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.PAGE_READ));
                authorities.add(new SimpleGrantedAuthority(WebsitePermissions.PAGE_APPROVE));
                break;
                
            default:
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
        }
        
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
} 