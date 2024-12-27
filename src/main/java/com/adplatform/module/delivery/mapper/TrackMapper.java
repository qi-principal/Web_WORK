package com.adplatform.module.delivery.mapper;

import com.adplatform.module.delivery.entity.Track;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TrackMapper {
    @Insert("INSERT INTO track (cookie_value, goods_url, visit_date) VALUES (#{cookieValue}, #{goodsUrl}, #{visitDate})")
    void insert(Track track);

    @Select("SELECT * FROM track WHERE cookie_value = #{cookieValue}")
    List<Track> findByCookieValue(String cookieValue);
}

