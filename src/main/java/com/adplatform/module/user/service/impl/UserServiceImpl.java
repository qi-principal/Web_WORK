package com.adplatform.module.user.service.impl;

import com.adplatform.common.exception.BusinessException;
import com.adplatform.module.user.dto.LoginRequest;
import com.adplatform.module.user.dto.LoginResponse;
import com.adplatform.module.user.dto.RegisterRequest;
import com.adplatform.module.user.dto.UserDTO;
import com.adplatform.module.user.entity.User;
import com.adplatform.module.user.repository.UserRepository;
import com.adplatform.module.user.security.JwtTokenProvider;
import com.adplatform.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

/**
 * 用户服务实现类
 *
 * @author andrew
 * @date 2023-11-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO register(RegisterRequest request) {
        log.info("开始注册用户，用户名: {}", request.getUsername());
        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.error("注册失败，用户名已存在: {}", request.getUsername());
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("注册失败，邮箱已存在: {}", request.getEmail());
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
        user = userRepository.save(user);
        log.info("用户注册成功，用户ID: {}", user.getId());
        
        // 转换为DTO返回
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户开始登录，用户名: {}", request.getUsername());
        try {
            // 认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // 生成token
            String token = tokenProvider.generateToken(authentication);
            log.info("用户登录成功，生成token");
            
            // 获取用户信息
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            
            log.info("用户登录成功，用户ID: {}", user.getId());
            return new LoginResponse(token, userDTO);
        } catch (Exception e) {
            log.error("用户登录失败，用户名: {}, 原因: {}", request.getUsername(), e.getMessage());
            throw new BusinessException("用户名或密码错误");
        }
    }

    @Override
    public UserDTO getUserById(Long id) {
        log.info("开始获取用户信息，用户ID: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.error("获取用户信息失败，用户不存在，用户ID: {}", id);
                return new BusinessException("用户不存在");
            });
        
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        log.info("获取用户信息成功，用户ID: {}", id);
        return userDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        log.info("开始更新用户状态，用户ID: {}, 新状态: {}", id, status);
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.error("更新用户状态失败，用户不存在，用户ID: {}", id);
                return new BusinessException("用户不存在");
            });
        
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        log.info("更新用户状态成功，用户ID: {}", id);
    }

    @Override
    public Page<UserDTO> getUserList(Integer pageNum, Integer pageSize) {
        log.info("开始获取用户列表，页码: {}, 每页数量: {}", pageNum, pageSize);
        // 创建分页请求，注意：JPA的页码从0开始
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, 
            Sort.by(Sort.Direction.DESC, "createTime"));
            
        // 获取用户分页数据
        Page<User> userPage = userRepository.findAll(pageRequest);
        
        // 将实体转换为DTO
        Page<UserDTO> dtoPage = userPage.map(user -> UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .status(user.getStatus())
                .balance(user.getBalance())
                .createTime(user.getCreateTime())
                .updatedAt(user.getUpdateTime())
                .build());
                
        log.info("获取用户列表成功，总记录数: {}", dtoPage.getTotalElements());
        return dtoPage;
    }

    @Override
    public List<UserDTO> getUsersByType(Integer userType) {
        log.info("开始获取用户类型为 {} 的用户列表", userType);
        List<User> users = userRepository.findByUserType(userType);
        return users.stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .userType(user.getUserType())
                        .status(user.getStatus())
                        .balance(user.getBalance())
                        .createTime(user.getCreateTime())
                        .updatedAt(user.getUpdateTime())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserBalance(Long userId, BigDecimal amount) {
        log.info("开始更新用户余额，用户ID: {}, 金额: {}", userId, amount);
        int updated = userRepository.updateBalance(userId, amount);
        if (updated == 0) {
            throw new BusinessException("更新用户余额失败");
        }
        log.info("更新用户余额成功");
    }

    @Override
    public UserStatisticsDTO getUserStatistics() {
        log.info("开始获取用户统计信息");
        
        // 获取用户类型分布
        List<UserTypeCount> typeCounts = userRepository.countByUserType();
        Map<Integer, Long> distribution = typeCounts.stream()
                .collect(Collectors.toMap(
                        UserTypeCount::getUserType,
                        UserTypeCount::getCount
                ));
        
        // 构建统计信息
        return UserStatisticsDTO.builder()
                .totalUsers(userRepository.selectCount(null))
                .activeUsers(userRepository.selectCount(new QueryWrapper<User>()
                        .eq("status", 1)))
                .inactiveUsers(userRepository.selectCount(new QueryWrapper<User>()
                        .eq("status", 0)))
                .userTypeDistribution(distribution)
                .build();
    }

    @Override
    public List<UserDTO> getRecentUsers(Integer days, Integer limit) {
        log.info("开始获取最近 {} 天注册的 {} 个用户", days, limit);
        String daysAgo = LocalDateTime.now().minusDays(days)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        List<User> users = userRepository.findRecentUsers(daysAgo, limit);
        return users.stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .userType(user.getUserType())
                        .status(user.getStatus())
                        .balance(user.getBalance())
                        .createTime(user.getCreateTime())
                        .updatedAt(user.getUpdateTime())
                        .build())
                .collect(Collectors.toList());
    }
} 