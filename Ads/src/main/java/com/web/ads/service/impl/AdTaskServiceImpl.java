package com.web.ads.service.impl;

import com.web.ads.dto.ActiveAdTaskDTO;
import com.web.ads.dto.AdTaskRecordDTO;
import com.web.ads.entity.AdTask;
import com.web.ads.entity.AdTaskRecord;
import com.web.ads.entity.Advertisement;
import com.web.ads.mapper.AdTaskMapper;
import com.web.ads.mapper.AdTaskRecordMapper;
import com.web.ads.mapper.AdvertisementMapper;
import com.web.ads.service.AdTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 广告任务服务实现类
 */
@Service
public class AdTaskServiceImpl implements AdTaskService {

    @Autowired
    private AdTaskMapper adTaskMapper;

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Autowired
    private AdTaskRecordMapper adTaskRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdTask createAdTask(AdTask adTask) {
        if (adTask.getWebsiteId() == null) {
            throw new IllegalArgumentException("目标网站ID不能为空");
        }
        if (adTask.getAdSlotId() == null || adTask.getAdSlotId().trim().isEmpty()) {
            throw new IllegalArgumentException("广告位信息不能为空");
        }

        // 设置初始状态和时间
        adTask.setTaskStatus("PENDING");
        LocalDateTime now = LocalDateTime.now();
        adTask.setCreatedAt(now);
        adTask.setUpdatedAt(now);
        
        int rows = adTaskMapper.insert(adTask);
        if (rows <= 0) {
            throw new RuntimeException("创建广告任务失败");
        }
        return adTask;
    }

    @Override
    public AdTask getAdTaskById(Integer taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        return adTaskMapper.selectById(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdTask(AdTask adTask) {
        if (adTask.getTaskId() == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        
        adTask.setUpdatedAt(LocalDateTime.now());
        return adTaskMapper.updateById(adTask) > 0;
    }

    @Override
    public List<AdTask> getAllAdTasks() {
        return adTaskMapper.selectAll();
    }

    @Override
    public List<AdTask> getAdTasksByWebsiteId(Integer websiteId) {
        if (websiteId == null) {
            throw new IllegalArgumentException("网站ID不能为空");
        }
        return adTaskMapper.selectByWebsiteId(websiteId);
    }

    @Override
    public List<AdTask> getAdTasksByStatus(String taskStatus) {
        if (taskStatus == null || taskStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("任务状态不能为空");
        }
        return adTaskMapper.selectByStatus(taskStatus);
    }

    /**
     * 获取活动广告任务列表
     * 1. 获取所有RUNNING状态的任务
     * 2. 获取相关的广告信息
     * 3. 组装DTO并设置优先级
     */
    @Override
    public List<ActiveAdTaskDTO> getActiveTasks() {
        // 获取所有运行中的任务
        List<AdTask> runningTasks = adTaskMapper.selectByStatus("RUNNING");
        if (runningTasks.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取相关的广告ID列表
        List<Integer> adIds = runningTasks.stream()
                .map(AdTask::getAdId)
                .collect(Collectors.toList());

        // 批量获取广告信息
        List<Advertisement> advertisements = advertisementMapper.findByIds(adIds);
        Map<Integer, Advertisement> adMap = advertisements.stream()
                .collect(Collectors.toMap(Advertisement::getAdId, ad -> ad));

        // 组装DTO列表
        List<ActiveAdTaskDTO> activeTasks = new ArrayList<>();
        for (int i = 0; i < runningTasks.size(); i++) {
            AdTask task = runningTasks.get(i);
            Advertisement ad = adMap.get(task.getAdId());
            
            if (ad != null) {
                ActiveAdTaskDTO dto = new ActiveAdTaskDTO();
                dto.setTaskId(task.getTaskId());
                dto.setAdId(ad.getAdId());
                dto.setAdTitle(ad.getTitle());
                dto.setImageUrl(ad.getAdImage());
                dto.setAdLink(ad.getTargetUrl());
                dto.setTaskStatus(task.getTaskStatus());
                dto.setCreatedAt(task.getCreatedAt());
                // 设置优先级：创建时间越早优先级越高
                dto.setPriority(runningTasks.size() - i);
                
                activeTasks.add(dto);
            }
        }

        return activeTasks;
    }

    /**
     * 记录广告任务展示或点击
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordAdTask(AdTaskRecordDTO recordDTO) {
        // 参数校验
        if (recordDTO.getTaskId() == null) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        if (recordDTO.getRecordType() == null || recordDTO.getRecordType().trim().isEmpty()) {
            throw new IllegalArgumentException("记录类型不能为空");
        }

        // 检查任务是否存在
        AdTask task = adTaskMapper.selectById(recordDTO.getTaskId());
        if (task == null) {
            throw new IllegalArgumentException("广告任务不存在");
        }

        // 创建记录实体
        AdTaskRecord record = new AdTaskRecord();
        record.setTaskId(recordDTO.getTaskId());
        record.setRecordType(recordDTO.getRecordType());
        record.setRecordTime(LocalDateTime.now());
        record.setClientIp(recordDTO.getClientIp());
        record.setUserAgent(recordDTO.getUserAgent());

        // 保存记录
        int rows = adTaskRecordMapper.insert(record);
        if (rows <= 0) {
            throw new RuntimeException("保存广告任务记录失败");
        }

        // 如果是点击记录，可以在这里添加额外的处理逻辑
        if ("CLICK".equals(recordDTO.getRecordType())) {
            handleAdClick(task);
        }
    }

    /**
     * 处理广告点击
     * 可以在这里添加点击后的业务逻辑，比如：
     * 1. 更新点击统计
     * 2. 触发奖励机制
     * 3. 更新任务状态等
     */
    private void handleAdClick(AdTask task) {
        // TODO: 实现点击处理逻辑
    }
} 