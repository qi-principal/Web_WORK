package com.adserver.controller;

import com.adserver.model.Ad;
import com.adserver.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ads")
public class AdController {
    @Autowired
    private AdService adService;
    
    @PostMapping
    public ResponseEntity<Ad> createAd(@RequestBody Ad ad) {
        return ResponseEntity.ok(adService.createAd(ad));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        adService.deleteAd(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Ad> getAd(@PathVariable Long id) {
        return ResponseEntity.ok(adService.getAd(id));
    }
} 