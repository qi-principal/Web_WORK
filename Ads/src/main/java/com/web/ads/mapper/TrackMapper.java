package com.web.ads.mapper;

import com.web.ads.entity.Track;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrackMapper {
    @Insert("INSERT INTO track (cookie_id, goods_url) VALUES (#{cookieId}, #{goodsUrl})")
    void insert(Track track);
}

