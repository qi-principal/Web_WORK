package com.adplatform.module.delivery.controller;

import com.adplatform.common.response.Result;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.delivery.dto.request.DeliveryTaskRequest;
import com.adplatform.module.delivery.dto.response.DeliveryTaskResponse;
import com.adplatform.module.delivery.dto.response.News;
import com.adplatform.module.delivery.service.AdDeliveryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 广告投放控制器
 */
@RestController
@RequestMapping("/delivery")
public class AdDeliveryController {

    @Autowired
    private AdDeliveryService deliveryService;



    @GetMapping("/getAd")
    public Result<List<Advertisement>> giveAd(){
        List<Advertisement> list= deliveryService.getAd();
        return Result.success(list);
    }

    /**
     * 创建投放任务
     */
    @PostMapping("/tasks")
    public Result<String> createDeliveryTask(@RequestBody DeliveryTaskRequest request) {

       deliveryService.createDeliveryTask(request);
       return Result.success("插入成功");
    }

    /**
     * 更新投放任务
     */
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<DeliveryTaskResponse> updateDeliveryTask(
            @PathVariable Long taskId,
            @Valid @RequestBody DeliveryTaskRequest request) {
        return ResponseEntity.ok(deliveryService.updateDeliveryTask(taskId, request));
    }

    /**
     * 删除投放任务
     */
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteDeliveryTask(@PathVariable Long taskId) {
        deliveryService.deleteDeliveryTask(taskId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取投放任务详情
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<DeliveryTaskResponse> getDeliveryTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(deliveryService.getDeliveryTask(taskId));
    }

    /**
     * 分页查询投放任务
     */
    @GetMapping("/tasks")
    public Result<IPage<News>> pageDeliveryTasks(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long adId,
            @RequestParam(required = false) Long adSpaceId,
            @RequestParam(required = false) Integer status) {
        Page<News> page = new Page<>(current, size);
        return Result.success(deliveryService.pageDeliveryTasks(page, adId, adSpaceId, status));
    }

//    /**
//     * 执行投放任务
//     */
//    @PostMapping("/tasks/{taskId}/execute")
//    public ResponseEntity<Void> executeDeliveryTask(@PathVariable Long taskId) {
//        deliveryService.executeDeliveryTask(taskId);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * 暂停投放任务
//     */
//    @PostMapping("/tasks/{taskId}/pause")
//    public ResponseEntity<Void> pauseDeliveryTask(@PathVariable Long taskId) {
//        deliveryService.pauseDeliveryTask(taskId);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * 恢复投放任务
//     */
//    @PostMapping("/tasks/{taskId}/resume")
//    public ResponseEntity<Void> resumeDeliveryTask(@PathVariable Long taskId) {
//        deliveryService.resumeDeliveryTask(taskId);
//        return ResponseEntity.ok().build();
//    }
//
    /**
     * 获取任务执行状态
     */
    @PutMapping("/tasks/{taskId}/status")
    public Result getTaskExecutionStatus(@PathVariable Long taskId, Integer status) {
        deliveryService.updateStatus(taskId,status);
        return Result.success();
    }
}