package com.web.ads.mapper;

import com.web.ads.entity.AdTask;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 广告任务数据访问层
 */
@Mapper
public interface AdTaskMapper {
    /**
     * 插入广告任务
     *
     * @param adTask 广告任务信息
     * @return 影响的行数
     */
    @Insert("INSERT INTO ad_task (website_id, ad_slot_id, ad_id, task_status, created_at, updated_at) " +
            "VALUES (#{websiteId}, #{adSlotId}, #{adId}, #{taskStatus}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "taskId")
    int insert(AdTask adTask);

    /**
     * 根据ID查询广告任务
     *
     * @param taskId 任务ID
     * @return 广告任务信息
     */
    @Select("SELECT * FROM ad_task WHERE task_id = #{taskId}")
    AdTask selectById(@Param("taskId") Integer taskId);

    /**
     * 更新广告任务
     *
     * @param adTask 广告任务信息
     * @return 影响的行数
     */
    @Update("UPDATE ad_task SET website_id = #{websiteId}, " +
            "ad_slot_id = #{adSlotId}, " +
            "task_status = #{taskStatus}, " +
            "updated_at = #{updatedAt} " +
            "WHERE task_id = #{taskId}")
    int updateById(AdTask adTask);

    /**
     * 查询所有广告任务
     *
     * @return 广告任务列表
     */
    @Select("SELECT * FROM ad_task")
    List<AdTask> selectAll();

    /**
     * 根据网站ID查询广告任务
     *
     * @param websiteId 网站ID
     * @return 广告任务列表
     */
    @Select("SELECT * FROM ad_task WHERE website_id = #{websiteId}")
    List<AdTask> selectByWebsiteId(@Param("websiteId") Integer websiteId);

    /**
     * 根据任务状态查询广告任务
     *
     * @param taskStatus 任务状态
     * @return 广告任务列表
     */
    @Select("SELECT * FROM ad_task WHERE task_status = #{taskStatus}")
    List<AdTask> selectByStatus(@Param("taskStatus") String taskStatus);


    /**
     * 查询过期任务
     * 这里假设任务创建时间超过24小时且状态不是COMPLETED的任务为过期任务
     */
    @Select("SELECT * FROM ad_task " +
            "WHERE created_at < DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
            "AND task_status NOT IN ('COMPLETED', 'FAILED')")
    List<AdTask> selectExpiredTasks();
} 