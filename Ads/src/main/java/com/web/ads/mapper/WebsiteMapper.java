package com.web.ads.mapper;

import com.web.ads.entity.Website;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 网站信息Mapper接口
 *
 * @author andrew
 * @date 2024/12/16
 */
@Mapper
public interface WebsiteMapper {
    
    /**
     * 插入网站信息
     *
     * @param website 网站信息
     * @return 影响行数
     */
    @Insert("INSERT INTO website (website_name, website_url, website_desc, created_at, updated_at) " +
            "VALUES (#{websiteName}, #{websiteUrl}, #{websiteDesc}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "websiteId")
    int insert(Website website);

    /**
     * 根据ID查询网站信息
     *
     * @param websiteId 网站ID
     * @return 网站信息
     */
    @Select("SELECT * FROM website WHERE website_id = #{websiteId}")
    Website findById(Integer websiteId);

    /**
     * 更新网站信息
     *
     * @param website 网站信息
     * @return 影响行数
     */
    @Update("UPDATE website SET website_name = #{websiteName}, website_url = #{websiteUrl}, " +
            "website_desc = #{websiteDesc}, updated_at = NOW() " +
            "WHERE website_id = #{websiteId}")
    int update(Website website);

    /**
     * 删除网站信息
     *
     * @param websiteId 网站ID
     * @return 影响行数
     */
    @Delete("DELETE FROM website WHERE website_id = #{websiteId}")
    int delete(Integer websiteId);

    /**
     * 查询所有网站信息
     *
     * @return 网站信息列表
     */
    @Select("SELECT * FROM website")
    List<Website> findAll();

    /**
     * 根据网站URL查询网站信息
     *
     * @param websiteUrl 网站URL
     * @return 网站信息
     */
    @Select("SELECT * FROM website WHERE website_url = #{websiteUrl}")
    Website findByUrl(String websiteUrl);
} 