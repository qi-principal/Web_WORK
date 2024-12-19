package com.adplatform.module.user.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT令牌工具类
 *
 * @author andrew
 * @date 2023-11-21
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    /**
     * 从认证信息生成令牌
     *
     * @param authentication 认证信息
     * @return JWT令牌
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000L);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * 验证令牌
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("无效的JWT签名");
        } catch (MalformedJwtException ex) {
            log.error("无效的JWT令牌");
        } catch (ExpiredJwtException ex) {
            log.error("过期的JWT令牌");
        } catch (UnsupportedJwtException ex) {
            log.error("不支持的JWT令牌");
        } catch (IllegalArgumentException ex) {
            log.error("JWT声明为空");
        }
        return false;
    }
} 