package com.adplatform.module.user.controller;

import com.adplatform.module.user.config.TestSecurityConfig;
import com.adplatform.module.user.dto.RegisterRequest;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.security.UserPrincipal;
import com.adplatform.module.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户控制器测试类
 *
 * @author andrew
 * @date 2023-11-21
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        log.info("开始创建测试用户 test_user");
        // 创建测试用户
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("test123");
        request.setEmail("test@example.com");
        request.setUserType(1);
        UserDTO user = userService.register(request);
        log.info("测试用户创建成功，用户ID: {}", user.getId());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById() throws Exception {
        log.info("开始测试管理员获取用户信息");
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user2");
        request.setPassword("test123");
        request.setEmail("test2@example.com");
        request.setUserType(1);

        // 注册用户
        UserDTO registered = userService.register(request);
        log.info("创建待查询用户成功，用户ID: {}", registered.getId());

        // 执行请求并验证结果
        MvcResult result = mockMvc.perform(get("/api/v1/users/{id}", registered.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("test_user2"))
                .andExpect(jsonPath("$.data.email").value("test2@example.com"))
                .andReturn();
        
        log.info("管理员获取用户信息成功，响应结果: {}", result.getResponse().getContentAsString());
    }

    @Test
    @WithUserDetails(value = "test_user", userDetailsServiceBeanName = "testUserDetailsService")
    public void testGetUserByIdWithoutPermission() throws Exception {
        log.info("开始测试普通用户无权限获取其他用户信息");
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user2");
        request.setPassword("test123");
        request.setEmail("test2@example.com");
        request.setUserType(1);

        // 注册另一个用户
        UserDTO registered = userService.register(request);
        log.info("创建目标用户成功，用户ID: {}", registered.getId());

        // 执行请求并验证结果（应该被拒绝，因为当前用户不是管理员且不是请求的用户本身）
        MvcResult result = mockMvc.perform(get("/api/v1/users/{id}", registered.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        
        log.info("测试无权限访问结果: {}", result.getResponse().getContentAsString());
    }

    @Test
    @WithUserDetails(value = "test_user", userDetailsServiceBeanName = "testUserDetailsService")
    public void testGetCurrentUser() throws Exception {
        log.info("开始测试获取当前用户信息");
        // 执行请求并验证结果
        MvcResult result = mockMvc.perform(get("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("test_user"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andReturn();
        
        log.info("获取当前用户信息成功，响应结果: {}", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateStatus() throws Exception {
        log.info("开始测试管理员更新用户状态");
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user2");
        request.setPassword("test123");
        request.setEmail("test2@example.com");
        request.setUserType(1);

        // 注册用户
        UserDTO registered = userService.register(request);
        log.info("创建待更新状态的用户成功，用户ID: {}", registered.getId());

        // 执行请求并验证结果
        MvcResult updateResult = mockMvc.perform(put("/api/v1/users/{id}/status", registered.getId())
                .param("status", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        
        log.info("更新用户状态请求结果: {}", updateResult.getResponse().getContentAsString());

        // 验证状态已更新
        MvcResult verifyResult = mockMvc.perform(get("/api/v1/users/{id}", registered.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(0))
                .andReturn();
        
        log.info("验证用户状态更新结果: {}", verifyResult.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateStatusWithoutPermission() throws Exception {
        log.info("开始测试普通用户无权限更新用户状态");
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user2");
        request.setPassword("test123");
        request.setEmail("test2@example.com");
        request.setUserType(1);

        // 注册用户
        UserDTO registered = userService.register(request);
        log.info("创建目标用户成功，用户ID: {}", registered.getId());

        // 执行请求并验证结果（应该被拒绝）
        MvcResult result = mockMvc.perform(put("/api/v1/users/{id}/status", registered.getId())
                .param("status", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        
        log.info("测试无权限更新状态结果: {}", result.getResponse().getContentAsString());
    }
} 