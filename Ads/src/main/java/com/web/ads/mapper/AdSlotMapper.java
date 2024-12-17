package com.web.ads.mapper;

import com.web.ads.entity.AdSlot;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 广告位Mapper接口
 *
 * @author andrew
 * @date 2024/12/16
 */
@Mapper
public interface AdSlotMapper {
    
    /**
     * 插入广告位信息
     *
     * @param adSlot 广告位信息
     * @return 影响行数
     */
    @Insert("INSERT INTO ad_slots (website_id, slot_type, slot_size, created_at, updated_at) " +
            "VALUES (#{websiteId}, #{slotType}, #{slotSize}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "adSlotId")
    int insert(AdSlot adSlot);
    

    /**
     * 根据ID查询广告位信息
     *
     * @param adSlotId 广告位ID
     * @return 广告位信息
     */
    @Select("SELECT * FROM ad_slots WHERE ad_slot_id = #{adSlotId}")
    AdSlot findById(Integer adSlotId);

    /**
     * 更新广告位信息
     *
     * @param adSlot 广告位信息
     * @return 影响行数
     */
    @Update("UPDATE ad_slots SET slot_type = #{slotType}, slot_size = #{slotSize}, " +
            "updated_at = NOW() WHERE ad_slot_id = #{adSlotId}")
    int update(AdSlot adSlot);

    /**
     * 删除广告位信息
     *
     * @param adSlotId 广告位ID
     * @return 影响行数
     */
    @Delete("DELETE FROM ad_slots WHERE ad_slot_id = #{adSlotId}")
    int delete(Integer adSlotId);

    /**
     * 查询所有广告位信息
     *
     * @return 广告位信息列表
     */
    @Select("SELECT * FROM ad_slots")
    List<AdSlot> findAll();

    /**
     * 根据类型和尺寸查询广告位
     *
     * @param slotType 广告位类型
     * @param slotSize 广告位尺寸
     * @return 广告位信息
     */
    @Select("SELECT * FROM ad_slots WHERE slot_type = #{slotType} AND slot_size = #{slotSize}")
    AdSlot findByTypeAndSize(@Param("slotType") String slotType, @Param("slotSize") String slotSize);

    /**
     * 根据网站ID查询广告位
     *
     * @param websiteId 网站ID
     * @return 广告位信息列表
     */
    @Select("SELECT * FROM ad_slots WHERE website_id = #{websiteId}")
    List<AdSlot> findByWebsiteId(@Param("websiteId") Integer websiteId);
} 