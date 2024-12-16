package com.web.ads.service;

import com.web.ads.entity.AdTask;
import java.util.List;

/**
 * 广告任务服务接口
 */
public interface AdTaskService {
    /**
     * 创建广告任务
     *
     * @param adTask 广告任务信息
     * @return 创建后的广告任务
     */
    AdTask createAdTask(AdTask adTask);

    /**
     * 根据ID查询广告任务
     *
     * @param taskId 任务ID
     * @return 广告任务信息
     */
    AdTask getAdTaskById(Integer taskId);

    /**
     * 更新广告任务
     *
     * @param adTask 广告任务信息
     * @return 是否更新成功
     */
    boolean updateAdTask(AdTask adTask);

    /**
     * 查询所有广告任务
     *
     * @return 广告任务列表
     */
    List<AdTask> getAllAdTasks();

    /**
     * 根据网站ID查询广告任务
     *
     * @param websiteId 网站ID
     * @return 广告任务列表
     */
    List<AdTask> getAdTasksByWebsiteId(Integer websiteId);

    /**
     * 根据任务状态查询广告任务
     *
     * @param taskStatus 任务状态
     * @return 广告任务列表
     */
    List<AdTask> getAdTasksByStatus(String taskStatus);
} 