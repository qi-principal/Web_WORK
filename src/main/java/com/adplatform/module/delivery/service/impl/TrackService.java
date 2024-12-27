package com.adplatform.module.delivery.service.impl;

import com.adplatform.module.delivery.dto.request.TrackDTO;
import com.adplatform.module.delivery.entity.Track;
import com.adplatform.module.delivery.mapper.TrackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class TrackService {
    
    @Autowired
    private TrackMapper trackMapper;
    
    @Transactional
    public void saveTrack(TrackDTO trackDTO) {
        Track track = new Track();
        track.setCookieValue(trackDTO.getCookieValue());
        track.setGoodsUrl(trackDTO.getGoodsUrl());
        track.setVisitDate(trackDTO.getVisitDate());
        trackMapper.insert(track);
    }
    
    public String generateDeviceId() {
        //生成一个长度为16的随机字符串作为用户的唯一识别标识
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(16);
        Random random = new Random();
        
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }

    public List<Track> getTrackByCookieValue(String cookieValue){
        return trackMapper.findByCookieValue(cookieValue);
    }
}

