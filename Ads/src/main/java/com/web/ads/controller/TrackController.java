package com.web.ads.controller;

import com.web.ads.dto.TrackDTO;
import com.web.ads.entity.Track;
import com.web.ads.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/track")
public class TrackController {

    @Autowired
    private TrackService trackService;
    
    @GetMapping
    public ResponseEntity<?> track(HttpServletRequest request, HttpServletResponse response) {
        // 获取或生成 device_id
        try {
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
            // 获取 referer
            String referer = request.getHeader("referer");
            // 如果没有referer则直接return
            if (referer == null){
                return ResponseEntity.ok().build();
            }

            // 如果没有找到 device_id，生成新的
            if (deviceId == null) {
                deviceId = trackService.generateDeviceId();
                Cookie cookie = new Cookie("device_id", deviceId);
                cookie.setMaxAge(365 * 24 * 60 * 60); // 设置cookie有效期为一年
                cookie.setPath("/");
                response.addCookie(cookie);
            }


            // 保存跟踪信息
            TrackDTO trackDTO = new TrackDTO();
            trackDTO.setCookieValue(deviceId);
            trackDTO.setGoodsUrl(referer);
            trackDTO.setVisitDate(LocalDateTime.now());

            trackService.saveTrack(trackDTO);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{cookieValue}")
    public ResponseEntity<List<Track>> getTrackByCookieValue(@PathVariable String cookieValue) {
        List<Track> tracks = trackService.getTrackByCookieValue(cookieValue);
        return ResponseEntity.ok(tracks);
    }
}
