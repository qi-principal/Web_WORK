package com.web.ads.service.impl;

import com.web.ads.entity.AdTask;
import com.web.ads.mapper.AdTaskMapper;
import com.web.ads.service.AdTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 广告任务服务实现类
 */
@Service
public class AdTaskServiceImpl implements AdTaskService {

    @Autowired
    private AdTaskMapper adTaskMapper;

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
} 