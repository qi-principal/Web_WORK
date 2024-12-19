package com.web.ads.controller;

import com.web.ads.dto.TrackingDTO;
import com.web.ads.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/track")
@CrossOrigin(origins = "*") // 允许跨域请求
public class TrackController {

    @Autowired
    private TrackService trackService;

    @PostMapping
    public ResponseEntity<String> trackPurchase(@RequestBody TrackingDTO trackingDTO) {
        try {
            trackService.saveTrackingData(trackingDTO);
            return ResponseEntity.ok("Tracking data saved successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error saving tracking data: " + e.getMessage());
        }
    }
} 