package com.ads.service;

import com.ads.entity.User;
import java.util.List;

public interface UserService {
    User register(User user);
    User login(String username, String password);
    User getUserById(Integer userId);
    User updateUser(User user);
    List<User> getAllUsers();
    boolean isUsernameExists(String username);
    boolean isEmailExists(String email);
} 