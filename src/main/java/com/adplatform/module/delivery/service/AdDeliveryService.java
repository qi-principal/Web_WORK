//package com.adplatform.module.delivery.service;
//
//import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
//import com.adplatform.module.delivery.dto.response.DeliveryTaskResponse;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//
///**
// * 广告投放服务接口
// */
//public interface AdDeliveryService {
//
//    /**
//     * 创建投放任务
//     */
//    DeliveryTaskResponse createDeliveryTask(DeliveryTaskRequest request);
//
//    /**
//     * 更新投放任务
//     */
//    DeliveryTaskResponse updateDeliveryTask(Long taskId, DeliveryTaskRequest request);
//
//    /**
//     * 删除投放任务
//     */
//    void deleteDeliveryTask(Long taskId);
//
//    /**
//     * 获取投放任务详情
//     */
//    DeliveryTaskResponse getDeliveryTask(Long taskId);
//
//    /**
//     * 分页查询投放任务
//     */
//    IPage<DeliveryTaskResponse> pageDeliveryTasks(Page<DeliveryTaskResponse> page, Long adId, Long adSpaceId, Integer status);
//
//    /**
//     * 执行投放任务
//     */
//    void executeDeliveryTask(Long taskId);
//
//    /**
//     * 暂停投放任务
//     */
//    void pauseDeliveryTask(Long taskId);
//
//    /**
//     * 恢复投放任务
//     */
//    void resumeDeliveryTask(Long taskId);
//
//    /**
//     * 获取任务执行状态
//     */
//    String getTaskExecutionStatus(Long taskId);
//}