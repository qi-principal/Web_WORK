package com.web.ads.service.impl;

import com.web.ads.entity.Advertisement;
import com.web.ads.mapper.AdvertisementMapper;
import com.web.ads.service.AdvertisementService;
import com.web.ads.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Override
    @Transactional
    public Advertisement createAd(Advertisement ad, MultipartFile image) {
        try {
            if (image != null && !image.isEmpty()) {
                String imagePath = FileUploadUtil.saveFile(image);
                ad.setAdImage(imagePath);
            }
            advertisementMapper.insert(ad);
            return ad;
        } catch (IOException e) {
            throw new RuntimeException("创建广告失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Advertisement updateAd(Advertisement ad, MultipartFile image) {
        Advertisement existingAd = advertisementMapper.findById(ad.getAdId());
        if (existingAd == null) {
            throw new RuntimeException("广告不存在");
        }

        try {
            if (image != null && !image.isEmpty()) {
                // 删除旧图片
                if (existingAd.getAdImage() != null) {
                    FileUploadUtil.deleteFile(existingAd.getAdImage());
                }
                // 保存新图片
                String imagePath = FileUploadUtil.saveFile(image);
                ad.setAdImage(imagePath);
            } else {
                // 如果没有上传新图片，保留原来的图片路径
                ad.setAdImage(existingAd.getAdImage());
            }

            advertisementMapper.update(ad);
            return ad;
        } catch (IOException e) {
            throw new RuntimeException("更新广告失败：" + e.getMessage());
        }
    }

    @Override
    public Advertisement getAdById(Integer adId) {
        return advertisementMapper.findById(adId);
    }

    @Override
    @Transactional
    public void deleteAd(Integer adId) {
        Advertisement ad = advertisementMapper.findById(adId);
        if (ad != null && ad.getAdImage() != null) {
            FileUploadUtil.deleteFile(ad.getAdImage());
        }
        advertisementMapper.delete(adId);
    }

    @Override
    public List<Advertisement> getAllAds() {
        return advertisementMapper.findAll();
    }
} 