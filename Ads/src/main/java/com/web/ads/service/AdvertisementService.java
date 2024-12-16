package com.web.ads.service;

import com.web.ads.entity.Advertisement;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AdvertisementService {
    Advertisement createAd(Advertisement ad, MultipartFile image);
    Advertisement updateAd(Advertisement ad, MultipartFile image);
    Advertisement getAdById(Integer adId);
    void deleteAd(Integer adId);
    List<Advertisement> getAllAds();
} 