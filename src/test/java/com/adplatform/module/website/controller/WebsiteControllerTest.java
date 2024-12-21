package com.adplatform.module.website.controller;

import com.adplatform.module.user.entity.User;
import com.adplatform.module.user.security.JwtTokenProvider;
import com.adplatform.module.website.entity.Website;
import com.adplatform.module.website.service.WebsiteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class WebsiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String publisherToken;
    private String adminToken;

    @BeforeEach
    public void setup() {
        // 创建网站主用户的token
        UserDetails publisher = new org.springframework.security.core.userdetails.User(
            "publisher", "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_PUBLISHER"))
        );
        Authentication publisherAuth = new UsernamePasswordAuthenticationToken(
            publisher, null, publisher.getAuthorities()
        );
        publisherToken = tokenProvider.generateToken(publisherAuth);

        // 创建管理员用户的token
        UserDetails admin = new org.springframework.security.core.userdetails.User(
            "admin", "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
            admin, null, admin.getAuthorities()
        );
        adminToken = tokenProvider.generateToken(adminAuth);
    }

    @Test
    public void testCreateWebsite() throws Exception {
        Website website = new Website();
        website.setName("测试网站");
        website.setUrl("http://test.com");
        website.setDescription("这是一个测试网站");

        mockMvc.perform(post("/api/v1/websites")
                .header("Authorization", "Bearer " + publisherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(website)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.websiteId").exists());
    }

    @Test
    public void testUpdateWebsite() throws Exception {
        // 先创建一个网站
        Website website = new Website();
        website.setUserId(1L);
        website.setName("原网站");
        website.setUrl("http://old.com");
        websiteService.createWebsite(website);

        // 准备更新数据
        Website update = new Website();
        update.setName("新网站");
        update.setUrl("http://new.com");

        mockMvc.perform(put("/api/v1/websites/" + website.getId())
                .header("Authorization", "Bearer " + publisherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testGetWebsite() throws Exception {
        // 先创建一个网站
        Website website = new Website();
        website.setUserId(1L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        mockMvc.perform(get("/api/v1/websites/" + website.getId())
                .header("Authorization", "Bearer " + publisherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(website.getId()))
                .andExpect(jsonPath("$.name").value(website.getName()))
                .andExpect(jsonPath("$.url").value(website.getUrl()));
    }

    @Test
    public void testGetWebsites() throws Exception {
        // 先创建两个网站
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

        mockMvc.perform(get("/api/v1/websites")
                .header("Authorization", "Bearer " + publisherToken)
                .param("userId", "1")
                .param("status", "0")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    public void testApproveWebsite() throws Exception {
        // 先创建一个网站
        Website website = new Website();
        website.setUserId(1L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        mockMvc.perform(post("/api/v1/websites/" + website.getId() + "/approve")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // 验证状态是否更新
        Website approved = websiteService.getWebsiteById(website.getId());
        assertEquals(1, approved.getStatus());
    }

    @Test
    public void testRejectWebsite() throws Exception {
        // 先创建一个网站
        Website website = new Website();
        website.setUserId(1L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        mockMvc.perform(post("/api/v1/websites/" + website.getId() + "/reject")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // 验证状态是否更新
        Website rejected = websiteService.getWebsiteById(website.getId());
        assertEquals(2, rejected.getStatus());
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/websites"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testForbiddenAccess() throws Exception {
        // 使用网站主token尝试审核网站
        Website website = new Website();
        website.setUserId(1L);
        website.setName("测试网站");
        website.setUrl("http://test.com");
        websiteService.createWebsite(website);

        mockMvc.perform(post("/api/v1/websites/" + website.getId() + "/approve")
                .header("Authorization", "Bearer " + publisherToken))
                .andExpect(status().isForbidden());
    }
} 