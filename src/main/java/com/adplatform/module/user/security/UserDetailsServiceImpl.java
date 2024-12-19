package com.adplatform.module.user.security;

import com.adplatform.module.user.entity.User;
import com.adplatform.module.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现类
 *
 * @author andrew
 * @date 2023-11-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("开始加载用户详情，用户名: {}", username);
        // 查询用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        if (user == null) {
            log.error("用户不存在，用户名: {}", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            log.error("账号已被禁用，用户名: {}", username);
            throw new UsernameNotFoundException("账号已被禁用");
        }

        log.info("用户详情加载成功，用户ID: {}", user.getId());
        // 返回自定义UserDetails实现
        return new UserPrincipal(user);
    }
} 