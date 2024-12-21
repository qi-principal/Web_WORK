package com.web.ads.controller;

import com.web.ads.dto.TrackingDTO;
import com.web.ads.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/track")
public class TrackController {

    @Autowired
    private TrackService trackService;
    
    @GetMapping
    public ResponseEntity<Void> track(HttpServletRequest request, HttpServletResponse response) {
        // 获取或生成 device_id
        String deviceId = null;
        Cookie[] cookies = request.getCookies();
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("device_id".equals(cookie.getName())) {
                    deviceId = cookie.getValue();
                    break;
                }
            }
        }
        
        // 如果没有找到 device_id，生成新的
        if (deviceId == null) {
            deviceId = trackService.generateDeviceId();
            Cookie cookie = new Cookie("device_id", deviceId);
            cookie.setMaxAge(365 * 24 * 60 * 60); // 设置cookie有效期为一年
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        
        // 获取 referer
        String referer = request.getHeader("referer");
        
        // 保存跟踪信息
        TrackingDTO trackingDTO = new TrackingDTO();
        trackingDTO.setCookieId(deviceId);
        trackingDTO.setGoodsUrl(referer);
        
        trackService.saveTracking(trackingDTO);
        
        return ResponseEntity.ok().build();
    }
}

