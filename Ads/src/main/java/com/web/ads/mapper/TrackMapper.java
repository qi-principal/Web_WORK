package com.web.ads.mapper;

import com.web.ads.entity.Track;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrackMapper {
    @Insert("INSERT INTO track (cookie_value, goods_url, visit_date) VALUES (#{cookieValue}, #{goodsUrl}, #{visitDate})")
    void insert(Track track);
}

