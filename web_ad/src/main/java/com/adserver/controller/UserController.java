package com.adserver.controller;

import com.adserver.model.User;
import com.adserver.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        User user = userService.registerUser(
            request.get("username"),
            request.get("password")
        );
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request,
            HttpServletResponse response) {
        String username = request.get("username");
        String password = request.get("password");

        if (userService.validateUser(username, password)) {
            // 创建Cookie
            Cookie cookie = new Cookie("user_session", username);
            cookie.setMaxAge(86400); // 24小时
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful");
            responseBody.put("username", username);
            
            return ResponseEntity.ok(responseBody);
        }

        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // 删除Cookie
        Cookie cookie = new Cookie("user_session", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }
} 