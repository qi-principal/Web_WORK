package com.adplatform.module.user.controller;

import com.adplatform.module.user.dto.LoginRequest;
import com.adplatform.module.user.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 认证控制器测试类
 *
 * @author andrew
 * @date 2023-11-21
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegister() throws Exception {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("test123");
        request.setEmail("test@example.com");
        request.setUserType(1);

        // 执行请求并验证结果
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("test_user"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    public void testRegisterWithInvalidData() throws Exception {
        // 准备测试数据（缺少必填字段）
        RegisterRequest request = new RegisterRequest();
        request.setUsername("");
        request.setPassword("");
        request.setEmail("invalid_email");

        // 执行请求并验证结果
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    public void testLogin() throws Exception {
        // 准备测试数据
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("test_user");
        registerRequest.setPassword("test123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setUserType(1);

        // 先注册用户
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 准备登录数据
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test_user");
        loginRequest.setPassword("test123");

        // 执行登录请求并验证结果
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.user.username").value("test_user"));
    }

    @Test
    public void testLoginWithWrongPassword() throws Exception {
        // 准备测试数据
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("test_user");
        registerRequest.setPassword("test123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setUserType(1);

        // 先注册用户
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 准备登录数据（错误密码）
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test_user");
        loginRequest.setPassword("wrong_password");

        // 执行登录请求并验证结果
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
} 