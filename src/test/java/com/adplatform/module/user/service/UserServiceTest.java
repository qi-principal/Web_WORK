package com.adplatform.module.user.service;

import com.adplatform.module.user.dto.LoginRequest;
import com.adplatform.module.user.dto.LoginResponse;
import com.adplatform.module.user.dto.RegisterRequest;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.entity.User;
import com.adplatform.module.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试类
 *
 * @author andrew
 * @date 2023-11-21
 */
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testRegister() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("test123");
        request.setEmail("test@example.com");
        request.setUserType(1);

        // 执行注册
        UserDTO result = userService.register(request);

        // 验证结果
        assertNotNull(result);
        assertEquals("test_user", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(1, result.getUserType());
        assertEquals(1, result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getBalance());

        // 验证数据库
        User user = userMapper.selectById(result.getId());
        assertNotNull(user);
        assertEquals("test_user", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    public void testRegisterWithDuplicateUsername() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("test123");
        request.setEmail("test1@example.com");
        request.setUserType(1);

        // 第一次注册
        userService.register(request);

        // 准备第二次注册数据
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setUsername("test_user");
        duplicateRequest.setPassword("test456");
        duplicateRequest.setEmail("test2@example.com");
        duplicateRequest.setUserType(1);

        // 验证重复用户名注册会抛出异常
        assertThrows(RuntimeException.class, () -> userService.register(duplicateRequest));
    }

    @Test
    public void testLogin() {
        // 准备测试数据
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("test_user");
        registerRequest.setPassword("test123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setUserType(1);

        // 先注册用户
        userService.register(registerRequest);

        // 执行登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test_user");
        loginRequest.setPassword("test123");

        LoginResponse response = userService.login(loginRequest);

        // 验证结果
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUser());
        assertEquals("test_user", response.getUser().getUsername());
    }

    @Test
    public void testLoginWithWrongPassword() {
        // 准备测试数据
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("test_user");
        registerRequest.setPassword("test123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setUserType(1);

        // 先注册用户
        userService.register(registerRequest);

        // 使用错误密码登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("test_user");
        loginRequest.setPassword("wrong_password");

        // 验证登录失败会抛出异常
        assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
    }

    @Test
    public void testGetUserById() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("test123");
        request.setEmail("test@example.com");
        request.setUserType(1);

        // 注册用户
        UserDTO registered = userService.register(request);

        // 获取用户信息
        UserDTO result = userService.getUserById(registered.getId());

        // 验证结果
        assertNotNull(result);
        assertEquals(registered.getId(), result.getId());
        assertEquals("test_user", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    public void testUpdateStatus() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("test123");
        request.setEmail("test@example.com");
        request.setUserType(1);

        // 注册用户
        UserDTO registered = userService.register(request);

        // 更新状态
        userService.updateStatus(registered.getId(), 0);

        // 验证结果
        User user = userMapper.selectById(registered.getId());
        assertNotNull(user);
        assertEquals(0, user.getStatus());
    }
} 