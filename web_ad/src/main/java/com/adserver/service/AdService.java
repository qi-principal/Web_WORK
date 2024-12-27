package com.adserver.service;

import com.adserver.model.Ad;
import com.adserver.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdService {
    @Autowired
    private AdRepository adRepository;
    
    public Ad createAd(Ad ad) {
        return adRepository.save(ad);
    }
    
    public void deleteAd(Long adId) {
        adRepository.deleteById(adId);
    }
    
    public Ad getAd(Long adId) {
        return adRepository.findById(adId)
            .orElseThrow(() -> new RuntimeException("Ad not found"));
    }
} 