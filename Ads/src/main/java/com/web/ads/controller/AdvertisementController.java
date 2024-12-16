package com.web.ads.controller;

import com.web.ads.entity.Advertisement;
import com.web.ads.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @PostMapping
    public ResponseEntity<?> createAd(
            @RequestPart("ad") Advertisement ad,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Advertisement createdAd = advertisementService.createAd(ad, image);
            return ResponseEntity.ok(createdAd);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{adId}")
    public ResponseEntity<?> updateAd(
            @PathVariable Integer adId,
            @RequestPart("ad") Advertisement ad,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            ad.setAdId(adId);
            Advertisement updatedAd = advertisementService.updateAd(ad, image);
            return ResponseEntity.ok(updatedAd);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{adId}")
    public ResponseEntity<?> getAdById(@PathVariable Integer adId) {
        Advertisement ad = advertisementService.getAdById(adId);
        if (ad != null) {
            return ResponseEntity.ok(ad);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{adId}")
    public ResponseEntity<?> deleteAd(@PathVariable Integer adId) {
        try {
            advertisementService.deleteAd(adId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Advertisement>> getAllAds() {
        List<Advertisement> ads = advertisementService.getAllAds();
        return ResponseEntity.ok(ads);
    }
} 