package com.adplatform.module.user.controller;

import com.adplatform.module.user.dto.RegisterRequest;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
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
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById() throws Exception {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("test123");
        request.setEmail("test@example.com");
        request.setUserType(1);

        // 注册用户
        UserDTO registered = userService.register(request);

        // 执行请求并验证结果
        mockMvc.perform(get("/api/v1/users/{id}", registered.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("test_user"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateStatus() throws Exception {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("test123");
        request.setEmail("test@example.com");
        request.setUserType(1);

        // 注册用户
        UserDTO registered = userService.register(request);

        // 执行请求并验证结果
        mockMvc.perform(put("/api/v1/users/{id}/status", registered.getId())
                .param("status", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证状态已更新
        mockMvc.perform(get("/api/v1/users/{id}", registered.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserByIdWithoutPermission() throws Exception {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("test123");
        request.setEmail("test@example.com");
        request.setUserType(1);

        // 注册用户
        UserDTO registered = userService.register(request);

        // 执行请求并验证结果（应该被拒绝）
        mockMvc.perform(get("/api/v1/users/{id}", registered.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateStatusWithoutPermission() throws Exception {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("test123");
        request.setEmail("test@example.com");
        request.setUserType(1);

        // 注册用户
        UserDTO registered = userService.register(request);

        // 执行请求并验证结果（应该被拒绝）
        mockMvc.perform(put("/api/v1/users/{id}/status", registered.getId())
                .param("status", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
} 