package com.adplatform.module.delivery.mapper;

import com.adplatform.module.delivery.entity.AdDeliveryJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 广告投放作业Mapper接口
 */
@Mapper
public interface AdDeliveryJobMapper extends BaseMapper<AdDeliveryJob> {

    /**
     * 查找指定任务ID的相关作业
     */
    @Select("SELECT * FROM ad_delivery_job WHERE task_id = #{taskId}")
    List<AdDeliveryJob> findByTaskId(Long taskId);

    /**
     * 查找指定页面ID的相关作业
     */
    @Select("SELECT * FROM ad_delivery_job WHERE display_page_id = #{pageId}")
    List<AdDeliveryJob> findByPageId(Long pageId);
} 