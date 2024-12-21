//package com.adplatform.module.delivery.executor;
//
//import com.adplatform.module.delivery.entity.AdDeliveryJob;
//import com.adplatform.module.delivery.entity.AdDeliveryTask;
//import com.adplatform.module.delivery.entity.AdDisplayPage;
//import com.adplatform.module.delivery.mapper.AdDeliveryJobMapper;
//import com.adplatform.module.delivery.mapper.AdDeliveryTaskMapper;
//import com.adplatform.module.delivery.mapper.AdDisplayPageMapper;
//import com.adplatform.module.delivery.service.AdContentCacheService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * 简单的广告投放执行器
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class SimpleDeliveryExecutor {
//
//    private final AdDeliveryTaskMapper taskMapper;
//    private final AdDeliveryJobMapper jobMapper;
//    private final AdDisplayPageMapper pageMapper;
//    private final AdContentCacheService contentCacheService;
//
//    /**
//     * 定时检查并执行待投放的任务
//     * 每分钟执行一次
//     */
//    @Scheduled(fixedRate = 60000)
//    @Transactional(rollbackFor = Exception.class)
//    public void checkAndExecuteTasks() {
//        LocalDateTime now = LocalDateTime.now();
//
//        // 查找需要开始投放的任务
//        List<AdDeliveryTask> tasksToStart = taskMapper.findTasksToStart(now);
//        for (AdDeliveryTask task : tasksToStart) {
//            startDeliveryTask(task);
//        }
//
//        // 查找需要结束投放的任务
//        List<AdDeliveryTask> tasksToEnd = taskMapper.findTasksToEnd(now);
//        for (AdDeliveryTask task : tasksToEnd) {
//            endDeliveryTask(task);
//        }
//    }
//
//    /**
//     * 开始投放任务
//     */
//    private void startDeliveryTask(AdDeliveryTask task) {
//        try {
//            log.info("开始投放任务：taskId={}", task.getId());
//
//            // 更新任务状态为投放中
//            task.setStatus(1); // 投放中
//            taskMapper.updateById(task);
//
//            // 查找对应的广告位页面
//            List<AdDisplayPage> pages = pageMapper.findByAdSpaceId(task.getAdSpaceId());
//
//            // 为每个页面创建投放作业
//            for (AdDisplayPage page : pages) {
//                AdDeliveryJob job = new AdDeliveryJob();
//                job.setTaskId(task.getId());
//                job.setDisplayPageId(page.getId());
//                job.setStatus(0); // 待执行
//                jobMapper.insert(job);
//
//                // 执行投放
//                executeDeliveryJob(job, task, page);
//            }
//
//        } catch (Exception e) {
//            log.error("开始投放任务失败：taskId=" + task.getId(), e);
//            task.setStatus(3); // 已暂停
//            taskMapper.updateById(task);
//        }
//    }
//
//    /**
//     * 结束投放任务
//     */
//    private void endDeliveryTask(AdDeliveryTask task) {
//        try {
//            log.info("结束投放任务：taskId={}", task.getId());
//
//            // 更新任务状态为已完成
//            task.setStatus(2); // 已完成
//            taskMapper.updateById(task);
//
//            // 清理相关的投放作业
//            List<AdDeliveryJob> jobs = jobMapper.findByTaskId(task.getId());
//            for (AdDeliveryJob job : jobs) {
//                cleanupDeliveryJob(job);
//            }
//
//        } catch (Exception e) {
//            log.error("结束投放任务失败：taskId=" + task.getId(), e);
//        }
//    }
//
//    /**
//     * 执行投放作业
//     */
//    private void executeDeliveryJob(AdDeliveryJob job, AdDeliveryTask task, AdDisplayPage page) {
//        try {
//            // 更新作业状态为执行中
//            job.setStatus(1); // 执行中
//            jobMapper.updateById(job);
//
//            // 更新页面的当前广告
//            page.setCurrentAdId(task.getAdId());
//            pageMapper.updateById(page);
//
//            // 生成广告内容并缓存
//            String content = contentCacheService.generateAdContent(task);
//            contentCacheService.cacheContent(page.getUniquePath(), content);
//
//            // 更新作业状态为已完成
//            job.setStatus(2); // 已完成
//            job.setResult("投放成功");
//            jobMapper.updateById(job);
//
//        } catch (Exception e) {
//            log.error("执行投放作业失败：jobId=" + job.getId(), e);
//            job.setStatus(3); // 失败
//            job.setResult("投放失败：" + e.getMessage());
//            jobMapper.updateById(job);
//        }
//    }
//
//    /**
//     * 清理投放作业
//     */
//    private void cleanupDeliveryJob(AdDeliveryJob job) {
//        try {
//            // 获取页面信息
//            AdDisplayPage page = pageMapper.selectById(job.getDisplayPageId());
//            if (page != null && page.getCurrentAdId() != null) {
//                // 清除页面的当前广告
//                page.setCurrentAdId(null);
//                pageMapper.updateById(page);
//
//                // 清除缓存的广告内容
//                contentCacheService.deleteContent(page.getUniquePath());
//            }
//
//            // 更新作业状态
//            job.setStatus(2); // 已完成
//            job.setResult("任务已结束，投放已清理");
//            jobMapper.updateById(job);
//
//        } catch (Exception e) {
//            log.error("清理投放作业失败：jobId=" + job.getId(), e);
//        }
//    }
//}