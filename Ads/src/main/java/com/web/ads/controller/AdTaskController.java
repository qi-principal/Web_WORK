package com.web.ads.controller;

import com.web.ads.entity.AdTask;
import com.web.ads.service.AdTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 广告任务控制器
 */
@RestController
@RequestMapping("/api/ad-tasks")
public class AdTaskController {

    @Autowired
    private AdTaskService adTaskService;

    /**
     * 创建广告任务
     *
     * @param adTask 广告任务信息
     * @return 创建后的广告任务
     */
    @PostMapping
    public ResponseEntity<?> createAdTask(@Validated @RequestBody AdTask adTask) {
        try {
            AdTask createdTask = adTaskService.createAdTask(adTask);
            return ResponseEntity.ok(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("创建广告任务失败：" + e.getMessage());
        }
    }

    /**
     * 获取广告任务
     *
     * @param taskId 任务ID
     * @return 广告任务信息
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getAdTask(@PathVariable Integer taskId) {
        try {
            AdTask task = adTaskService.getAdTaskById(taskId);
            if (task != null) {
                return ResponseEntity.ok(task);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("获取广告任务失败：" + e.getMessage());
        }
    }

    /**
     * 更新广告任务
     *
     * @param taskId 任务ID
     * @param adTask 广告任务信息
     * @return 更新结果
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateAdTask(@PathVariable Integer taskId, @Validated @RequestBody AdTask adTask) {
        try {
            adTask.setTaskId(taskId);
            boolean updated = adTaskService.updateAdTask(adTask);
            if (updated) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("更新广告任务失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有广告任务
     *
     * @return 广告任务列表
     */
    @GetMapping
    public ResponseEntity<?> getAllAdTasks() {
        try {
            List<AdTask> tasks = adTaskService.getAllAdTasks();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("获取广告任务列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据网站ID获取广告任务
     *
     * @param websiteId 网站ID
     * @return 广告任务列表
     */
    @GetMapping("/website/{websiteId}")
    public ResponseEntity<?> getAdTasksByWebsiteId(@PathVariable Integer websiteId) {
        try {
            List<AdTask> tasks = adTaskService.getAdTasksByWebsiteId(websiteId);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("获取网站广告任务失败：" + e.getMessage());
        }
    }

    /**
     * 根据任务状态获取广告任务
     *
     * @param status 任务状态
     * @return 广告任务列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAdTasksByStatus(@PathVariable String status) {
        try {
            List<AdTask> tasks = adTaskService.getAdTasksByStatus(status);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("获取指定状态的广告任务失败：" + e.getMessage());
        }
    }
} 