package com.web.ads.service;

import com.web.ads.dto.TrackingDTO;
import com.web.ads.entity.Track;
import com.web.ads.mapper.TrackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrackService {
    
    @Autowired
    private TrackMapper trackMapper;

    @Transactional
    public void saveTrackingData(TrackingDTO trackingDTO) {
        // 遍历所有商品项并保存
        for (TrackingDTO.TrackingItem item : trackingDTO.getItems()) {
            Track track = new Track();
            track.setDeviceId(trackingDTO.getDeviceId());
            track.setGoodsName(item.getName());
            track.setGoodsQuantity(item.getQuantity());
            trackMapper.insert(track);
        }
    }
} 