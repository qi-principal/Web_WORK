package com.web.ads.mapper;

import com.web.ads.entity.Advertisement;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AdvertisementMapper {
    
    @Insert("INSERT INTO ads (title, description, target_url, ad_image, created_at, updated_at) " +
            "VALUES (#{title}, #{description}, #{targetUrl}, #{adImage}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "adId")
    int insert(Advertisement ad);

    @Select("SELECT * FROM ads WHERE ad_id = #{adId}")
    Advertisement findById(Integer adId);

    @Update("UPDATE ads SET title = #{title}, description = #{description}, " +
            "target_url = #{targetUrl}, ad_image = #{adImage}, updated_at = NOW() " +
            "WHERE ad_id = #{adId}")
    int update(Advertisement ad);

    @Delete("DELETE FROM ads WHERE ad_id = #{adId}")
    int delete(Integer adId);

    @Select("SELECT * FROM ads")
    List<Advertisement> findAll();

    /**
     * 批量查询广告信息
     * @param adIds 广告ID列表
     * @return 广告信息列表
     */
    @Select("SELECT * FROM ads WHERE ad_id IN (#{adIds})")
    List<Advertisement> findByIds(@Param("adIds") List<Integer> adIds);
    
    
} 