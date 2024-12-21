//package com.adplatform.module.delivery.service.impl;
//
//import com.adplatform.module.delivery.converter.DeliveryConverter;
//import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
//import com.adplatform.module.delivery.dto.response.DeliveryTaskResponse;
//import com.adplatform.module.delivery.entity.AdDeliveryJob;
//import com.adplatform.module.delivery.entity.AdDeliveryTask;
//import com.adplatform.module.delivery.enums.DeliveryStatus;
//import com.adplatform.module.delivery.executor.SimpleDeliveryExecutor;
//import com.adplatform.module.delivery.mapper.AdDeliveryJobMapper;
//import com.adplatform.module.delivery.mapper.AdDeliveryTaskMapper;
//import com.adplatform.module.delivery.service.AdDeliveryService;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * 广告投放服务实现类
// */
//@Service
//public class AdDeliveryServiceImpl implements AdDeliveryService {
//
//    @Autowired
//    private AdDeliveryTaskMapper taskMapper;
//
//    @Autowired
//    private AdDeliveryJobMapper jobMapper;
//
//    @Autowired
//    private SimpleDeliveryExecutor deliveryExecutor;
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public DeliveryTaskResponse createDeliveryTask(DeliveryTaskRequest request) {
//
//        // 转换请求为实体
//        AdDeliveryTask task = DeliveryConverter.INSTANCE.toEntity(request);
//
//
//        task.setDeliveryStatus(DeliveryStatus.PENDING);
//
//        // 保存任务
//        taskMapper.insert(task);
//
//        return DeliveryConverter.INSTANCE.toResponse(task);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public DeliveryTaskResponse updateDeliveryTask(Long taskId, DeliveryTaskRequest request) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//
//        // 更新任务信息
//        task.setAdId(request.getAdId());
//        task.setAdSpaceId(request.getAdSpaceId());
//        task.setStartTime(request.getStartTime());
//        task.setEndTime(request.getEndTime());
//        task.setPriority(request.getPriority());
//
//        taskMapper.updateById(task);
//
//        return DeliveryConverter.INSTANCE.toResponse(task);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void deleteDeliveryTask(Long taskId) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//
//        // 删除任务
//        taskMapper.deleteById(taskId);
//
//        // 删除相关的作业
//        jobMapper.delete(new LambdaQueryWrapper<AdDeliveryJob>()
//                .eq(AdDeliveryJob::getTaskId, taskId));
//    }
//
//    @Override
//    public DeliveryTaskResponse getDeliveryTask(Long taskId) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//        return DeliveryConverter.INSTANCE.toResponse(task);
//    }
//
//    @Override
//    public IPage<DeliveryTaskResponse> pageDeliveryTasks(Page<DeliveryTaskResponse> page, Long adId, Long adSpaceId, Integer status) {
//        Page<AdDeliveryTask> taskPage = taskMapper.selectPage(
//            new Page<>(page.getCurrent(), page.getSize()),
//            new LambdaQueryWrapper<AdDeliveryTask>()
//                .eq(Objects.nonNull(adId), AdDeliveryTask::getAdId, adId)
//                .eq(Objects.nonNull(adSpaceId), AdDeliveryTask::getAdSpaceId, adSpaceId)
//                .eq(Objects.nonNull(status), AdDeliveryTask::getStatus, status)
//                .orderByDesc(AdDeliveryTask::getCreateTime)
//        );
//
//        // 转换为响应DTO
//        IPage<DeliveryTaskResponse> responsePage = new Page<>(
//            taskPage.getCurrent(),
//            taskPage.getSize(),
//            taskPage.getTotal()
//        );
//        responsePage.setRecords(taskPage.getRecords().stream()
//            .map(DeliveryConverter.INSTANCE::toResponse)
//            .collect(Collectors.toList()));
//
//        return responsePage;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void executeDeliveryTask(Long taskId) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//
//        // 创建执行作业
//        AdDeliveryJob job = new AdDeliveryJob();
//        job.setTaskId(taskId);
//        job.setDeliveryStatus(DeliveryStatus.RUNNING);
//        jobMapper.insert(job);
//
//        try {
//            // 更新任务状态为执行中
//            task.setDeliveryStatus(DeliveryStatus.RUNNING);
//            taskMapper.updateById(task);
//
//            // 执行广告投放
//            String result = deliveryExecutor.execute(task);
//
//            // 更新作业状态为已完成
//            job.setDeliveryStatus(DeliveryStatus.COMPLETED);
//            job.setResult(result);
//            jobMapper.updateById(job);
//
//            // 更新任务状态为已完成
//            task.setDeliveryStatus(DeliveryStatus.COMPLETED);
//            taskMapper.updateById(task);
//
//        } catch (Exception e) {
//            // 更新作业状态为暂停
//            job.setDeliveryStatus(DeliveryStatus.PAUSED);
//            job.setResult("投放失败：" + e.getMessage());
//            jobMapper.updateById(job);
//
//            // 更新任务状态为暂停
//            task.setDeliveryStatus(DeliveryStatus.PAUSED);
//            taskMapper.updateById(task);
//
//            throw new RuntimeException("执行投放任务失败", e);
//        }
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void pauseDeliveryTask(Long taskId) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//
//        // 只有运行中的任务可以暂停
//        if (task.getDeliveryStatus() != DeliveryStatus.RUNNING) {
//            throw new RuntimeException("只有运行中的任务可以暂停");
//        }
//
//        // 更新任务状态为暂停
//        task.setDeliveryStatus(DeliveryStatus.PAUSED);
//        taskMapper.updateById(task);
//
//        // 暂停相关的运行中作业
//        jobMapper.update(null, new LambdaQueryWrapper<AdDeliveryJob>()
//            .eq(AdDeliveryJob::getTaskId, taskId)
//            .eq(AdDeliveryJob::getStatus, DeliveryStatus.RUNNING.getCode())
//            .set(AdDeliveryJob::getStatus, DeliveryStatus.PAUSED.getCode()));
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void resumeDeliveryTask(Long taskId) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//
//        // 只有暂停的任务可以恢复
//        if (task.getDeliveryStatus() != DeliveryStatus.PAUSED) {
//            throw new RuntimeException("只有暂停的任务可以恢复");
//        }
//
//        // 更新任务状态为运行中
//        task.setDeliveryStatus(DeliveryStatus.RUNNING);
//        taskMapper.updateById(task);
//
//        // 重新执行任务
//        executeDeliveryTask(taskId);
//    }
//
//    @Override
//    public String getTaskExecutionStatus(Long taskId) {
//        AdDeliveryTask task = taskMapper.selectById(taskId);
//        if (Objects.isNull(task)) {
//            throw new RuntimeException("投放任务不存在");
//        }
//
//        // 获取最新的作业状态
//        AdDeliveryJob latestJob = jobMapper.selectOne(
//            new LambdaQueryWrapper<AdDeliveryJob>()
//                .eq(AdDeliveryJob::getTaskId, taskId)
//                .orderByDesc(AdDeliveryJob::getCreateTime)
//                .last("LIMIT 1")
//        );
//
//        if (Objects.isNull(latestJob)) {
//            return "未开始执行";
//        }
//
//        return String.format("任务状态: %s, 作业状态: %s, 执行结果: %s",
//            task.getDeliveryStatus().getDescription(),
//            latestJob.getDeliveryStatus().getDescription(),
//            latestJob.getResult());
//    }
//}