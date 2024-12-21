package com.adplatform.module.website.service;

import com.adplatform.module.website.entity.Website;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class WebsiteServiceTest {

    @Autowired
    private WebsiteService websiteService;

    @Test
    public void testCreateWebsite() {
        // 准备测试数据
        Website website = new Website();
        website.setUserId(1L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        website.setDescription("这是一个测试网站");

        // 执行创建操作
        websiteService.createWebsite(website);

        // 验证结果
        assertNotNull(website.getId());
        assertEquals(0, website.getStatus()); // 验证状态是否为待审核
        
        // 从数据库查询验证
        Website saved = websiteService.getWebsiteById(website.getId());
        assertNotNull(saved);
        assertEquals(website.getName(), saved.getName());
        assertEquals(website.getUrl(), saved.getUrl());
        assertEquals(website.getDescription(), saved.getDescription());
    }

    @Test
    public void testUpdateWebsite() {
        // 准备测试数据
        Website website = new Website();
        website.setUserId(1L);
        website.setName("原网站");
        website.setUrl("http://old.com");
        website.setDescription("这是原网站");
        websiteService.createWebsite(website);

        // 执行更新操作
        Website update = new Website();
        update.setName("新网站");
        update.setUrl("http://new.com");
        update.setDescription("这是新网站");
        websiteService.updateWebsite(website.getId(), update);

        // 验证结果
        Website updated = websiteService.getWebsiteById(website.getId());
        assertNotNull(updated);
        assertEquals(update.getName(), updated.getName());
        assertEquals(update.getUrl(), updated.getUrl());
        assertEquals(update.getDescription(), updated.getDescription());
        assertEquals(0, updated.getStatus()); // 验证状态是否重置为待审核
    }

    @Test
    public void testGetWebsites() {
        // 准备测试数据
        Website website1 = new Website();
        website1.setUserId(1L);
        website1.setName("网站1");
        website1.setUrl("http://test1.com");
        websiteService.createWebsite(website1);

        Website website2 = new Website();
        website2.setUserId(1L);
        website2.setName("网站2");
        website2.setUrl("http://test2.com");
        websiteService.createWebsite(website2);

        // 测试查询列表
        List<Website> websites = websiteService.getWebsites(1L, 0, 1, 10);
        assertFalse(websites.isEmpty());
        assertTrue(websites.size() >= 2);

        // 测试状态筛选
        List<Website> pendingWebsites = websiteService.getWebsites(1L, 0, 1, 10);
        assertTrue(pendingWebsites.stream().allMatch(w -> w.getStatus() == 0));
    }

    @Test
    public void testApproveAndRejectWebsite() {
        // 准备测试数据
        Website website = new Website();
        website.setUserId(1L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        // 测试审核通过
        websiteService.approveWebsite(website.getId());
        Website approved = websiteService.getWebsiteById(website.getId());
        assertEquals(1, approved.getStatus());

        // 测试审核拒绝
        websiteService.rejectWebsite(website.getId());
        Website rejected = websiteService.getWebsiteById(website.getId());
        assertEquals(2, rejected.getStatus());
    }
} 