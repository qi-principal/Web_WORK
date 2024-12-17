package com.web.ads.service;

public interface AdTaskScheduleService {
    /**
     * 更新待处理任务状态为进行中
     */
    void updatePendingTasks();
    
    /**
     * 更新进行中任务状态
     */
    void updateRunningTasks();
    
    /**
     * 检查任务是否过期
     */
    void checkExpiredTasks();
} 