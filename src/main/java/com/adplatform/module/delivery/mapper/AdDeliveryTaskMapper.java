package com.adplatform.module.delivery.mapper;

import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
import com.adplatform.module.delivery.entity.AdDeliveryTask;
import com.adplatform.module.delivery.entity.Time;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 广告投放任务Mapper接口
 */
@Mapper
public interface AdDeliveryTaskMapper extends BaseMapper<AdDeliveryTask> {

    /**
     * 查找需要开始投放的任务
     * 条件：
     * 1. 状态为待投放(0)
     * 2. 开始时间小于等于当前时间
     * 3. 结束时间大于当前时间
     */
    @Select("SELECT * FROM ad_delivery_task " +
            "WHERE status = 0 " +
            "AND start_time <= #{now} " +
            "AND end_time > #{now} " +
            "ORDER BY priority DESC, create_time ASC")
    List<AdDeliveryTask> findTasksToStart(LocalDateTime now);

    /**
     * 查找需要结束投放的任务
     * 条件：
     * 1. 状态为投放中(1)
     * 2. 结束时间小于等于当前时间
     */
    @Select("SELECT * FROM ad_delivery_task " +
            "WHERE status = 1 " +
            "AND end_time <= #{now} " +
            "ORDER BY priority DESC, create_time ASC")
    List<AdDeliveryTask> findTasksToEnd(LocalDateTime now);

    /**
     * 查找指定任务ID的相关作业
     */
    @Select("SELECT * FROM ad_delivery_job WHERE task_id = #{taskId}")
    List<AdDeliveryTask> findByTaskId(Long taskId);

    @Insert("insert into ad_delivery_task (ad_id, ad_space_id, start_time, end_time, priority,status) VALUES " +
            "(#{adId},#{adSpaceId},#{startTime},#{endTime},#{priority},#{status})")
    void insetTask(DeliveryTaskRequest request);

    @Select("SELECT start_time,end_time FROM ad_delivery_task WHERE id=#{adId}")
    Time find(Long adId);


    void updateStatus(Long taskId, Integer status);

    @Select("SELECT ad_id from ad_delivery_task where status=1")
    List<Integer> selectIDS();
}