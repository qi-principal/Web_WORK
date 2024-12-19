package com.adplatform.module.user.service.impl;

import com.adplatform.common.exception.BusinessException;
import com.adplatform.module.user.dto.LoginRequest;
import com.adplatform.module.user.dto.LoginResponse;
import com.adplatform.module.user.dto.RegisterRequest;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.entity.User;
import com.adplatform.module.user.mapper.UserMapper;
import com.adplatform.module.user.security.JwtTokenProvider;
import com.adplatform.module.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户服务实现类
 *
 * @author andrew
 * @date 2023-11-21
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (lambdaQuery().eq(User::getUsername, request.getUsername()).count() > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (lambdaQuery().eq(User::getEmail, request.getEmail()).count() > 0) {
            throw new BusinessException("邮箱已存在");
        }
        
        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(1);
        user.setBalance(BigDecimal.ZERO);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 保存用户
        save(user);
        
        // 转换为DTO返回
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            // 认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // 生成token
            String token = tokenProvider.generateToken(authentication);
            
            // 获取用户信息
            User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            
            return new LoginResponse(token, userDTO);
        } catch (Exception e) {
            throw new BusinessException("用户名或密码错误");
        }
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }
} 