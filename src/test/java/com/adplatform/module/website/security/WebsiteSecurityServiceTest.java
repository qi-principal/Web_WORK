package com.adplatform.module.website.security;

import com.adplatform.module.user.security.UserPrincipal;
import com.adplatform.module.website.entity.AdSpace;
import com.adplatform.module.website.entity.Page;
import com.adplatform.module.website.entity.Website;
import com.adplatform.module.website.service.AdSpaceService;
import com.adplatform.module.website.service.PageService;
import com.adplatform.module.website.service.WebsiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class WebsiteSecurityServiceTest {

    @Autowired
    private WebsiteSecurityService securityService;

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private AdSpaceService adSpaceService;

    @Autowired
    private PageService pageService;

    private Authentication adminAuth;
    private Authentication publisherAuth;
    private Authentication otherPublisherAuth;

    @BeforeEach
    public void setup() {
        // 创建管理员认证
        adminAuth = new UsernamePasswordAuthenticationToken(
            createUserPrincipal(1L, "admin", 3),
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        // 创建网站主认证
        publisherAuth = new UsernamePasswordAuthenticationToken(
            createUserPrincipal(2L, "publisher", 2),
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_PUBLISHER"))
        );

        // 创建其他网站主认证
        otherPublisherAuth = new UsernamePasswordAuthenticationToken(
            createUserPrincipal(3L, "other_publisher", 2),
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_PUBLISHER"))
        );
    }

    @Test
    public void testIsWebsiteOwner() {
        // 创建测试网站
        Website website = new Website();
        website.setUserId(2L); // 设置为publisher的ID
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        // 测试网站所有者
        assertTrue(securityService.isWebsiteOwner(website.getId(), publisherAuth));
        // 测试其他用户
        assertFalse(securityService.isWebsiteOwner(website.getId(), otherPublisherAuth));
        // 测试管理员
        assertFalse(securityService.isWebsiteOwner(website.getId(), adminAuth));
    }

    @Test
    public void testIsAdSpaceOwner() {
        // 创建测试网站和广告位
        Website website = new Website();
        website.setUserId(2L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        AdSpace adSpace = new AdSpace();
        adSpace.setWebsiteId(website.getId());
        adSpace.setName("测试广告位");
        adSpace.setWidth(300);
        adSpace.setHeight(250);
        adSpaceService.createAdSpace(website.getId(), adSpace);

        // 测试广告位所有者
        assertTrue(securityService.isAdSpaceOwner(adSpace.getId(), publisherAuth));
        // 测试其他用户
        assertFalse(securityService.isAdSpaceOwner(adSpace.getId(), otherPublisherAuth));
        // 测试管理员
        assertFalse(securityService.isAdSpaceOwner(adSpace.getId(), adminAuth));
    }

    @Test
    public void testIsPageOwner() {
        // 创建测试网站、广告位和页面
        Website website = new Website();
        website.setUserId(2L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        AdSpace adSpace = new AdSpace();
        adSpace.setWebsiteId(website.getId());
        adSpace.setName("测试广告位");
        adSpace.setWidth(300);
        adSpace.setHeight(250);
        adSpaceService.createAdSpace(website.getId(), adSpace);

        Page page = new Page();
        page.setAdSpaceId(adSpace.getId());
        page.setUrl("http://test.com/page1");
        pageService.createPage(adSpace.getId(), page);

        // 测试页面所有者
        assertTrue(securityService.isPageOwner(page.getId(), publisherAuth));
        // 测试其他用户
        assertFalse(securityService.isPageOwner(page.getId(), otherPublisherAuth));
        // 测试管理员
        assertFalse(securityService.isPageOwner(page.getId(), adminAuth));
    }

    @Test
    public void testCanAccessWebsite() {
        // 创建测试网站
        Website website = new Website();
        website.setUserId(2L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        // 测试网站所有者
        assertTrue(securityService.canAccessWebsite(website.getId(), publisherAuth));
        // 测试其他用户
        assertFalse(securityService.canAccessWebsite(website.getId(), otherPublisherAuth));
        // 测试管理员
        assertTrue(securityService.canAccessWebsite(website.getId(), adminAuth));
    }

    @Test
    public void testCanAccessAdSpace() {
        // 创建测试网站和广告位
        Website website = new Website();
        website.setUserId(2L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        AdSpace adSpace = new AdSpace();
        adSpace.setWebsiteId(website.getId());
        adSpace.setName("测试广告位");
        adSpace.setWidth(300);
        adSpace.setHeight(250);
        adSpaceService.createAdSpace(website.getId(), adSpace);

        // 测试广告位所有者
        assertTrue(securityService.canAccessAdSpace(adSpace.getId(), publisherAuth));
        // 测试其他用户
        assertFalse(securityService.canAccessAdSpace(adSpace.getId(), otherPublisherAuth));
        // 测试管理员
        assertTrue(securityService.canAccessAdSpace(adSpace.getId(), adminAuth));
    }

    @Test
    public void testCanAccessPage() {
        // 创建测试网站、广告位和页面
        Website website = new Website();
        website.setUserId(2L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        AdSpace adSpace = new AdSpace();
        adSpace.setWebsiteId(website.getId());
        adSpace.setName("测试广告位");
        adSpace.setWidth(300);
        adSpace.setHeight(250);
        adSpaceService.createAdSpace(website.getId(), adSpace);

        Page page = new Page();
        page.setAdSpaceId(adSpace.getId());
        page.setUrl("http://test.com/page1");
        pageService.createPage(adSpace.getId(), page);

        // 测试页面所有者
        assertTrue(securityService.canAccessPage(page.getId(), publisherAuth));
        // 测试其他用户
        assertFalse(securityService.canAccessPage(page.getId(), otherPublisherAuth));
        // 测试管理员
        assertTrue(securityService.canAccessPage(page.getId(), adminAuth));
    }

    private UserPrincipal createUserPrincipal(Long id, String username, Integer userType) {
        com.adplatform.module.user.entity.User user = new com.adplatform.module.user.entity.User();
        user.setId(id);
        user.setUsername(username);
        user.setUserType(userType);
        user.setStatus(1);
        return new UserPrincipal(user);
    }
} 