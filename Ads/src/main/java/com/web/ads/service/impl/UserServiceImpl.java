package com.web.ads.service.impl;

import com.web.ads.entity.User;
import com.web.ads.mapper.UserMapper;
import com.web.ads.service.UserService;
import com.web.ads.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public User register(User user) {
        // 检查用户名和邮箱是否已存在
        if (isUsernameExists(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (isEmailExists(user.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 加密密码
        user.setPassword(PasswordEncoder.encode(user.getPassword()));
        
        // 插入用户
        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && PasswordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        throw new RuntimeException("用户名或密码错误");
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.findById(userId);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = userMapper.findById(user.getUserId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 如果修改了用户名，检查新用户名是否已存在
        if (!existingUser.getUsername().equals(user.getUsername()) 
            && isUsernameExists(user.getUsername())) {
            throw new RuntimeException("新用户名已存在");
        }

        // 如果修改了邮箱，检查新邮箱是否已存在
        if (!existingUser.getEmail().equals(user.getEmail()) 
            && isEmailExists(user.getEmail())) {
            throw new RuntimeException("新邮箱已存在");
        }

        // 如果密码不为空，说明要修改密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(PasswordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }

        userMapper.update(user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userMapper.findByUsername(username) != null;
    }

    @Override
    public boolean isEmailExists(String email) {
        return userMapper.findByEmail(email) != null;
    }
} 