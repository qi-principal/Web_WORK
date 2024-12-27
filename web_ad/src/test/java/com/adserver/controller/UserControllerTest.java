package com.adserver.controller;

import com.adserver.AdServerApplication;
import com.adserver.BaseTest;
import com.adserver.config.TestConfig;
import com.adserver.model.User;
import com.adserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import({TestConfig.class})
@WithMockUser
@ContextConfiguration(classes = {AdServerApplication.class, TestConfig.class})
public class UserControllerTest extends BaseTest {

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void initTest() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setAccountType(User.AccountType.STANDARD);
    }

    @Test
    void register_Success() throws Exception {
        // Arrange
        when(userService.registerUser(anyString(), anyString())).thenReturn(testUser);
        Map<String, String> request = createRegisterRequest("testuser", "password");

        // Act
        ResultActions result = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)));

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).registerUser("testuser", "password");
    }

    @Test
    void register_UserExists() throws Exception {
        // Arrange
        when(userService.registerUser(anyString(), anyString()))
                .thenThrow(new RuntimeException("Username already exists"));
        Map<String, String> request = createRegisterRequest("testuser", "password");

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_Success() throws Exception {
        // Arrange
        when(userService.validateUser("testuser", "password")).thenReturn(true);
        Map<String, String> request = createLoginRequest("testuser", "password");

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(cookie().exists("user_session"))
                .andExpect(cookie().httpOnly("user_session", true));
    }

    @Test
    void login_Failure() throws Exception {
        // Arrange
        when(userService.validateUser("testuser", "wrongpassword")).thenReturn(false);
        Map<String, String> request = createLoginRequest("testuser", "wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/users/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("user_session", 0));
    }

    private Map<String, String> createRegisterRequest(String username, String password) {
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);
        return request;
    }

    private Map<String, String> createLoginRequest(String username, String password) {
        return createRegisterRequest(username, password);
    }
} 