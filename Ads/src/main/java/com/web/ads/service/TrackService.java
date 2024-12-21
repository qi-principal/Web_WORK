package com.web.ads.service;

import com.web.ads.dto.TrackingDTO;
import com.web.ads.entity.Track;
import com.web.ads.mapper.TrackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;

@Service
public class TrackService {
    
    @Autowired
    private TrackMapper trackMapper;
    
    @Transactional
    public void saveTracking(TrackingDTO trackingDTO) {
        Track track = new Track();
        track.setCookieId(trackingDTO.getCookieId());
        track.setGoodsUrl(trackingDTO.getGoodsUrl());
        trackMapper.insert(track);
    }
    
    public String generateDeviceId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(16);
        Random random = new Random();
        
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }
}

