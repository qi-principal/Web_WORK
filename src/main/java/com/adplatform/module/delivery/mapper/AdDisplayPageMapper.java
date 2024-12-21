package com.adplatform.module.delivery.mapper;

import com.adplatform.module.delivery.entity.AdDisplayPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 广告展示页面Mapper接口
 */
@Mapper
public interface AdDisplayPageMapper extends BaseMapper<AdDisplayPage> {

    /**
     * 查找指定广告位ID的展示页面
     */
    @Select("SELECT * FROM ad_display_page WHERE ad_space_id = #{adSpaceId}")
    List<AdDisplayPage> findByAdSpaceId(Long adSpaceId);

    /**
     * 查找指定唯一路径的展示页面
     */
    @Select("SELECT * FROM ad_display_page WHERE unique_path = #{uniquePath}")
    AdDisplayPage findByUniquePath(String uniquePath);
} 