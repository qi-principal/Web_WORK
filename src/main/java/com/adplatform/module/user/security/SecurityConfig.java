package com.adplatform.module.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置类
 *
 * @author andrew
 * @date 2023-11-21
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 静态资源完全绕过Spring Security的所有过滤器
        web.ignoring()
            .antMatchers(
                "/",
                "/*.html",
                "/css/**",
                "/js/**",
                "/img/**",
                "/fonts/**",
                "/favicon.ico",
                "/material/**",
                "/site/**",
                "/admin/**",
                "/advertiser/**",
                "/publisher/**",
                "/api/track"
            );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 启用CORS，禁用CSRF
            .cors().and()
            .csrf().disable()
            // 基于token，不需要session
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            // 过滤请求
            .authorizeRequests()
                // 对于登录注册允许匿名访问
                .antMatchers("/v1/auth/**", "/api/v1/auth/**").permitAll()
                // swagger
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/*/api-docs").permitAll()
                // 静态资源
                .antMatchers("/", "/*.html", "/css/**", "/js/**", "/img/**",
                           "/material/**", "/site/**", "/admin/**",
                           "/advertiser/**", "/publisher/**").permitAll()
                // 网站模块的公开接口
                .antMatchers(HttpMethod.GET, "/api/v1/websites/*/spaces/*/code").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/pages/*/display").permitAll()
                .antMatchers( "/api/track").permitAll()
                .antMatchers("/track").permitAll()
                // 广告代理的公开接口
                .antMatchers("/public/ad-proxy/**", "/api/public/ad-proxy/**").permitAll()
                // 网站模块的审核接口
                .antMatchers("/api/v1/websites/*/approve", "/api/v1/websites/*/reject",
                           "/api/v1/spaces/*/approve", "/api/v1/spaces/*/reject",
                           "/api/v1/pages/*/approve", "/api/v1/pages/*/reject")
                    .hasRole("ADMIN")
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                .and()
            // 禁用缓存
            .headers()
                .cacheControl()
                .and()
                .frameOptions().disable();

        // 添加JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 