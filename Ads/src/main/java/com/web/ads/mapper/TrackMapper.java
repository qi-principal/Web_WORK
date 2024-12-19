package com.web.ads.mapper;

import com.web.ads.entity.Track;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrackMapper {
    @Insert("INSERT INTO track (device_id, goods_name, goods_quantity) " +
            "VALUES (#{deviceId}, #{goodsName}, #{goodsQuantity})")
    void insert(Track track);
} 