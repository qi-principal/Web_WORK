Delivery 模块设计说明
1. 功能概述
Delivery 模块 主要为 广告主用户 提供以下功能：

广告投放任务管理：

创建广告投放任务
更新广告投放任务
获取广告投放任务详情
获取广告投放任务列表
删除广告投放任务
广告展示：

广告展示页面生成
广告展示内容获取
广告投放调度系统：

定时执行广告投放任务
生成和管理广告投放作业
2. 数据库设计
2.1 表结构
ad_delivery_task：存储广告投放任务的信息。
ad_delivery_job：存储具体的广告投放作业信息。
ad_display_page：存储生成的广告展示页面信息。
ad_delivery_task 表
sql
复制代码
CREATE TABLE `ad_delivery_task` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `ad_id` bigint NOT NULL COMMENT '广告ID',
    `ad_space_id` bigint NOT NULL COMMENT '广告位ID',
    `schedule_time` datetime NOT NULL COMMENT '调度时间',
    `status` int NOT NULL DEFAULT '0' COMMENT '状态：0-待执行、1-执行中、2-已完成、3-失败',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_ad_id` (`ad_id`),
    KEY `idx_ad_space_id` (`ad_space_id`),
    KEY `idx_schedule_time` (`schedule_time`),
    CONSTRAINT `fk_delivery_task_ad` FOREIGN KEY (`ad_id`) REFERENCES `advertisement` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_delivery_task_ad_space` FOREIGN KEY (`ad_space_id`) REFERENCES `ad_space` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告投放任务表';
ad_delivery_job 表
sql
复制代码
CREATE TABLE `ad_delivery_job` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '作业ID',
    `task_id` bigint NOT NULL COMMENT '任务ID',
    `status` int NOT NULL DEFAULT '0' COMMENT '状态：0-待执行、1-执行中、2-已完成、3-失败',
    `result` varchar(500) DEFAULT NULL COMMENT '执行结果',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    CONSTRAINT `fk_delivery_job_task` FOREIGN KEY (`task_id`) REFERENCES `ad_delivery_task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告投放作业表';
ad_display_page 表
sql
复制代码
CREATE TABLE `ad_display_page` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '页面ID',
    `ad_space_id` bigint NOT NULL COMMENT '广告位ID',
    `unique_path` varchar(255) NOT NULL COMMENT '唯一路径',
    `url` varchar(255) NOT NULL COMMENT '广告展示页面URL',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unique_path` (`unique_path`),
    KEY `idx_ad_space_id` (`ad_space_id`),
    CONSTRAINT `fk_display_page_ad_space` FOREIGN KEY (`ad_space_id`) REFERENCES `ad_space` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告展示页面表';
2.2 表说明
ad_delivery_task

id：主键，任务ID。
ad_id：关联广告表，表示要投放的广告。
ad_space_id：关联广告位表，表示广告要投放到哪个广告位。
schedule_time：调度时间，表示任务执行的时间。
status：任务状态（待执行、执行中、已完成、失败）。
create_time、update_time：记录创建和更新时间。
ad_delivery_job

id：主键，作业ID。
task_id：关联广告投放任务表，表示该作业属于哪个任务。
status：作业状态（待执行、执行中、已完成、失败）。
result：作业执行结果描述。
create_time、update_time：记录创建和更新时间。
ad_display_page

id：主键，页面ID。
ad_space_id：关联广告位表，表示该展示页面对应的广告位。
unique_path：唯一路径，用于生成广告展示页面URL。
url：广告展示页面的URL。
create_time、update_time：记录创建和更新时间。
2.3 索引设计
主键索引：所有表均设置主键索引，保证唯一性和高效查询。
唯一索引：
ad_display_page 表：unique_path 添加唯一索引，确保每个展示页面的唯一性。
外键索引：为外键字段创建索引，优化关联查询。
调度时间索引：为 ad_delivery_task 表的 schedule_time 字段创建索引，优化按时间范围查询任务。
3. 后端实现
3.1 模块结构
在现有项目结构中新增 delivery 模块，结构如下：

sql
复制代码
module/
├── ad/
├── statistics/
├── user/
├── website/
└── delivery/
    ├── controller/
    │   └── DeliveryController.java
    ├── service/
    │   ├── AdDeliveryService.java
    │   └── impl/
    │       └── AdDeliveryServiceImpl.java
    ├── mapper/
    │   ├── AdDeliveryTaskMapper.java
    │   ├── AdDeliveryJobMapper.java
    │   └── AdDisplayPageMapper.java
    ├── entity/
    │   ├── AdDeliveryTask.java
    │   ├── AdDeliveryJob.java
    │   └── AdDisplayPage.java
    ├── dto/
    ├── converter/
    ├── enums/
    └── job/
        └── AdDeliveryJob.java
3.2 实体类
AdDeliveryTask.java
java
复制代码
package com.adplatform.module.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("ad_delivery_task")
public class AdDeliveryTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adId;
    private Long adSpaceId;
    private Date scheduleTime;
    private Integer status; // 0-待执行、1-执行中、2-已完成、3-失败

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
AdDeliveryJob.java
java
复制代码
package com.adplatform.module.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("ad_delivery_job")
public class AdDeliveryJob {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;
    private Integer status; // 0-待执行、1-执行中、2-已完成、3-失败
    private String result;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
AdDisplayPage.java
java
复制代码
package com.adplatform.module.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("ad_display_page")
public class AdDisplayPage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adSpaceId;
    private String uniquePath;
    private String url;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
3.3 Mapper 接口
AdDeliveryTaskMapper.java
java
复制代码
package com.adplatform.module.delivery.mapper;

import com.adplatform.module.delivery.entity.AdDeliveryTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdDeliveryTaskMapper extends BaseMapper<AdDeliveryTask> {
}
AdDeliveryJobMapper.java
java
复制代码
package com.adplatform.module.delivery.mapper;

import com.adplatform.module.delivery.entity.AdDeliveryJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdDeliveryJobMapper extends BaseMapper<AdDeliveryJob> {
}
AdDisplayPageMapper.java
java
复制代码
package com.adplatform.module.delivery.mapper;

import com.adplatform.module.delivery.entity.AdDisplayPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdDisplayPageMapper extends BaseMapper<AdDisplayPage> {
}
3.4 服务接口与实现
AdDeliveryService.java
java
复制代码
package com.adplatform.module.delivery.service;

public interface AdDeliveryService {
    void scheduleDeliveryTask(Long taskId);
    void executeDeliveryTask(Long taskId);
}
AdDeliveryServiceImpl.java
java
复制代码
package com.adplatform.module.delivery.service.impl;

import com.adplatform.module.delivery.service.AdDeliveryService;
import com.adplatform.module.delivery.entity.AdDeliveryTask;
import com.adplatform.module.delivery.mapper.AdDeliveryTaskMapper;
import com.adplatform.module.delivery.job.AdDeliveryJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class AdDeliveryServiceImpl implements AdDeliveryService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private AdDeliveryTaskMapper taskMapper;

    @Override
    @Transactional
    public void scheduleDeliveryTask(Long taskId) {
        AdDeliveryTask task = taskMapper.selectById(taskId);
        if (task != null && task.getStatus() == 0) { // 待执行
            try {
                JobDetail jobDetail = JobBuilder.newJob(AdDeliveryJob.class)
                        .withIdentity("adDeliveryJob-" + taskId, "adDeliveryGroup")
                        .usingJobData("taskId", taskId)
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity("adDeliveryTrigger-" + taskId, "adDeliveryGroup")
                        .startAt(task.getScheduleTime())
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);

                task.setStatus(1); // 已调度
                taskMapper.updateById(task);
            } catch (SchedulerException e) {
                e.printStackTrace();
                // 处理调度异常
                throw new RuntimeException("调度任务失败。");
            }
        }
    }

    @Override
    @Transactional
    public void executeDeliveryTask(Long taskId) {
        AdDeliveryTask task = taskMapper.selectById(taskId);
        if (task != null && task.getStatus() == 1) { // 已调度
            try {
                // TODO: 实现广告在广告展示页面中的展示逻辑
                // 例如，更新 ad_display_page 表，记录展示信息

                // 示例逻辑：
                // 1. 获取广告位对应的展示页面
                // 2. 更新展示页面的广告内容
                // 3. 记录展示数据（如 impressions）

                // 假设展示逻辑完成
                task.setStatus(2); // 已完成
                taskMapper.updateById(task);
            } catch (Exception e) {
                e.printStackTrace();
                task.setStatus(3); // 失败
                taskMapper.updateById(task);
                throw new RuntimeException("执行投放任务失败。");
            }
        }
    }
}
3.5 控制器
DeliveryController.java
java
复制代码
package com.adplatform.module.delivery.controller;

import com.adplatform.module.delivery.entity.AdDeliveryTask;
import com.adplatform.module.delivery.mapper.AdDeliveryTaskMapper;
import com.adplatform.module.delivery.service.AdDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    @Autowired
    private AdDeliveryTaskMapper taskMapper;

    @Autowired
    private AdDeliveryService adDeliveryService;

    @PostMapping("/tasks")
    public ResponseEntity<?> createDeliveryTask(@RequestBody AdDeliveryTask task) {
        // 设定任务初始状态为待执行
        task.setStatus(0);
        taskMapper.insert(task);
        adDeliveryService.scheduleDeliveryTask(task.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "message", "投放任务创建并调度成功。",
                "taskId", task.getId()
            )
        );
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getDeliveryTask(@PathVariable Long id) {
        AdDeliveryTask task = taskMapper.selectById(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "投放任务不存在。"))
            );
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getDeliveryTasks(
            @RequestParam(required = false) Long adId,
            @RequestParam(required = false) Long adSpaceId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 使用 MyBatis-Plus 的分页插件
        List<AdDeliveryTask> tasks = taskMapper.selectList(
            new LambdaQueryWrapper<AdDeliveryTask>()
                .eq(adId != null, AdDeliveryTask::getAdId, adId)
                .eq(adSpaceId != null, AdDeliveryTask::getAdSpaceId, adSpaceId)
                .eq(status != null, AdDeliveryTask::getStatus, status)
                .last("LIMIT " + (page - 1) * size + "," + size)
        );
        // 这里可以进一步封装分页信息
        return ResponseEntity.ok(
            Map.of(
                "currentPage", page,
                "pageSize", size,
                "items", tasks
            )
        );
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<?> updateDeliveryTask(@PathVariable Long id, @RequestBody AdDeliveryTask task) {
        AdDeliveryTask existingTask = taskMapper.selectById(id);
        if (existingTask == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "投放任务不存在。"))
            );
        }
        // 更新任务信息
        existingTask.setAdId(task.getAdId());
        existingTask.setAdSpaceId(task.getAdSpaceId());
        existingTask.setScheduleTime(task.getScheduleTime());
        existingTask.setStatus(0); // 更新后重新置为待执行
        taskMapper.updateById(existingTask);
        adDeliveryService.scheduleDeliveryTask(existingTask.getId());
        return ResponseEntity.ok(Map.of("message", "投放任务更新成功，待执行。"));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteDeliveryTask(@PathVariable Long id) {
        AdDeliveryTask task = taskMapper.selectById(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", Map.of("code", "NOT_FOUND", "message", "投放任务不存在。"))
            );
        }
        taskMapper.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "投放任务删除成功。"));
    }
}
AdDisplayController.java
java
复制代码
package com.adplatform.module.delivery.controller;

import com.adplatform.module.delivery.entity.AdDisplayPage;
import com.adplatform.module.delivery.mapper.AdDisplayPageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.adplatform.module.delivery.entity.AdDeliveryTask;
import com.adplatform.module.delivery.mapper.AdDeliveryTaskMapper;
import com.adplatform.module.delivery.entity.AdContent;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.mapper.AdvertisementMapper;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

@Controller
@RequestMapping("/ad/display")
public class AdDisplayController {

    @Autowired
    private AdDisplayPageMapper displayPageMapper;

    @Autowired
    private AdDeliveryTaskMapper taskMapper;

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @GetMapping("/{uniquePath}")
    public String displayAd(@PathVariable String uniquePath, Model model) {
        AdDisplayPage page = displayPageMapper.selectOne(
                new LambdaQueryWrapper<AdDisplayPage>()
                        .eq(AdDisplayPage::getUniquePath, uniquePath)
        );
        if (page == null) {
            return "404"; // 返回404页面
        }
        model.addAttribute("adSpaceId", page.getAdSpaceId());
        return "adDisplay"; // 返回广告展示页面视图
    }

    @GetMapping("/ads/display")
    @ResponseBody
    public AdContent getAdContent(@RequestParam Long adSpaceId) {
        // 根据广告位ID查询当前投放的广告
        // 这里简化为随机选择一个已完成的广告投放任务
        AdDeliveryTask task = taskMapper.selectOne(
                new LambdaQueryWrapper<AdDeliveryTask>()
                        .eq(AdDeliveryTask::getAdSpaceId, adSpaceId)
                        .eq(AdDeliveryTask::getStatus, 2) // 已完成
                        .last("ORDER BY RAND() LIMIT 1")
        );
        if (task == null){
            return null;
        }
        Advertisement ad = advertisementMapper.selectById(task.getAdId());
        if(ad == null){
            return null;
        }
        AdContent content = new AdContent();
        content.setUrl(ad.getUrl());
        content.setImageUrl(ad.getMaterialUrl()); // 假设广告素材URL存储在advertisement表中
        return content;
    }

    public static class AdContent {
        private String url;
        private String imageUrl;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
3.6 广告投放调度系统
为了实现广告投放任务的调度系统，我们采用 Quartz 作为任务调度框架。Quartz 允许我们创建定时任务，并根据调度结果执行相应的操作。

3.6.1 依赖引入
在 pom.xml 中添加 Quartz 依赖：

xml
复制代码
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
3.6.2 配置 Quartz
在 application.yml 中添加 Quartz 配置：

yaml
复制代码
spring:
  quartz:
    job-store-type: jdbc
    jdbc:
      initialize-schema: always
    properties:
      org:
        quartz:
          scheduler:
            instanceName: AdDeliveryScheduler
          threadPool:
            threadCount: 10
          jobStore:
            misfireThreshold: 10000
3.6.3 创建 Job 类
AdDeliveryJob.java
java
复制代码
package com.adplatform.module.delivery.job;

import com.adplatform.module.delivery.service.AdDeliveryService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdDeliveryJob implements Job {

    @Autowired
    private AdDeliveryService adDeliveryService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long taskId = context.getJobDetail().getJobDataMap().getLong("taskId");
        adDeliveryService.executeDeliveryTask(taskId);
    }
}
4. 广告展示页面生成
每个广告位对应一个唯一的广告展示页面，该页面用于嵌入广告。广告展示页面需要动态加载并展示广告内容。

4.1 页面生成逻辑
生成唯一URL：在广告位创建时生成一个唯一的路径，例如 http://yourdomain.com/ad/display/{uniquePath}。
存储映射关系：将 uniquePath 与 ad_space_id 关联，存储在 ad_display_page 表中。
广告展示页面：前端页面通过获取 ad_space_id，查询投放任务，并展示相应广告。
4.2 广告展示页面实现
AdDisplayController.java

java
复制代码
package com.adplatform.module.delivery.controller;

import com.adplatform.module.delivery.entity.AdDisplayPage;
import com.adplatform.module.delivery.mapper.AdDisplayPageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.adplatform.module.delivery.entity.AdDeliveryTask;
import com.adplatform.module.delivery.mapper.AdDeliveryTaskMapper;
import com.adplatform.module.ad.entity.Advertisement;
import com.adplatform.module.ad.mapper.AdvertisementMapper;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

@Controller
@RequestMapping("/ad/display")
public class AdDisplayController {

    @Autowired
    private AdDisplayPageMapper displayPageMapper;

    @Autowired
    private AdDeliveryTaskMapper taskMapper;

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @GetMapping("/{uniquePath}")
    public String displayAd(@PathVariable String uniquePath, Model model) {
        AdDisplayPage page = displayPageMapper.selectOne(
                new LambdaQueryWrapper<AdDisplayPage>()
                        .eq(AdDisplayPage::getUniquePath, uniquePath)
        );
        if (page == null) {
            return "404"; // 返回404页面
        }
        model.addAttribute("adSpaceId", page.getAdSpaceId());
        return "adDisplay"; // 返回广告展示页面视图
    }

    @GetMapping("/ads/display")
    @ResponseBody
    public AdContent getAdContent(@RequestParam Long adSpaceId) {
        // 根据广告位ID查询当前投放的广告
        // 这里简化为随机选择一个已完成的广告投放任务
        AdDeliveryTask task = taskMapper.selectOne(
                new LambdaQueryWrapper<AdDeliveryTask>()
                        .eq(AdDeliveryTask::getAdSpaceId, adSpaceId)
                        .eq(AdDeliveryTask::getStatus, 2) // 已完成
                        .last("ORDER BY RAND() LIMIT 1")
        );
        if (task == null){
            return null;
        }
        Advertisement ad = advertisementMapper.selectById(task.getAdId());
        if(ad == null){
            return null;
        }
        AdContent content = new AdContent();
        content.setUrl(ad.getUrl());
        content.setImageUrl(ad.getMaterialUrl()); // 假设广告素材URL存储在advertisement表中
        return content;
    }

    public static class AdContent {
        private String url;
        private String imageUrl;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
adDisplay.html

在 src/main/resources/templates/adDisplay.html 中创建广告展示页面模板：

html
复制代码
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>广告展示</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        $(document).ready(function(){
            var adSpaceId = /*[[${adSpaceId}]]*/ '';
            $.ajax({
                url: '/api/v1/delivery/ads/display',
                method: 'GET',
                data: { adSpaceId: adSpaceId },
                success: function(data){
                    if(data && data.url && data.imageUrl){
                        $('#ad-content').html('<a href="' + data.url + '"><img src="' + data.imageUrl + '" alt="广告"></a>');
                    } else {
                        $('#ad-content').html('暂无广告展示。');
                    }
                },
                error: function(){
                    $('#ad-content').html('广告加载失败。');
                }
            });
        });
    </script>
</head>
<body>
    <div id="ad-content">
        <!-- 广告内容将动态加载 -->
    </div>
</body>
</html>
5. 调度系统设计
5.1 调度系统架构
调度系统的核心是 Quartz，负责根据 ad_delivery_task 表中的调度时间，执行相应的广告投放作业。调度系统设计需考虑以下几点：

任务调度：根据调度时间，触发广告投放作业。
作业执行：执行广告投放逻辑，将广告展示到广告展示页面。
任务状态管理：更新任务和作业的状态，记录执行结果。
扩展性：为后续植入推荐系统做好接口设计，支持精准广告投递。
5.2 Quartz 配置与使用
Quartz 的使用已在 3.6 广告投放调度系统 中详细说明。

5.3 广告投放逻辑
在 AdDeliveryServiceImpl 中，executeDeliveryTask 方法负责广告投放逻辑。具体实现可根据业务需求扩展，例如：

广告选择策略：根据广告投放策略选择合适的广告进行展示。
广告计费：记录展示次数，计算费用。
用户画像与推荐系统：集成用户画像数据，实现精准投放（预留接口）。
6. API 接口说明
6.1 广告投放任务接口
6.1.1 创建广告投放任务
接口路径: POST /api/v1/delivery/tasks

描述: 广告主用户创建新的广告投放任务，任务将根据调度系统进行执行。

请求头:

Authorization: Bearer <token>
Content-Type: application/json
角色要求: 广告主
请求体:

json
复制代码
{
    "adId": 12345,                // 广告ID，必填
    "adSpaceId": 54321,           // 广告位ID，必填
    "scheduleTime": "2024-05-01T10:00:00Z" // 调度时间，必填，ISO 8601 格式
}
响应:

成功 (201 Created):

json
复制代码
{
    "message": "投放任务创建并调度成功。",
    "taskId": 98765
}
失败:

400 Bad Request: 请求参数错误
401 Unauthorized: 未授权访问
403 Forbidden: 无权限创建投放任务
404 Not Found: 广告或广告位不存在
500 Internal Server Error: 服务器内部错误
6.1.2 获取广告投放任务详情
接口路径: GET /api/v1/delivery/tasks/{id}
描述: 获取指定ID的广告投放任务详情。
请求头:
Authorization: Bearer <token>
路径参数:
id (long): 投放任务ID，必填
响应:
成功 (200 OK):

json
复制代码
{
    "id": 98765,
    "adId": 12345,
    "adSpaceId": 54321,
    "scheduleTime": "2024-05-01T10:00:00Z",
    "status": 0,             // 状态：0-待执行、1-执行中、2-已完成、3-失败
    "createTime": "2024-04-27T12:34:56Z",
    "updateTime": "2024-04-27T12:34:56Z"
}
失败:

401 Unauthorized: 未授权访问
403 Forbidden: 无权限查看该投放任务
404 Not Found: 投放任务不存在
500 Internal Server Error: 服务器内部错误
6.1.3 获取广告投放任务列表
接口路径: GET /api/v1/delivery/tasks
描述: 获取广告主用户的广告投放任务列表，支持分页和筛选。
请求头:
Authorization: Bearer <token>
角色要求: 广告主
查询参数:
adId (long): 广告ID，选填
adSpaceId (long): 广告位ID，选填
status (int): 任务状态，选填（0-待执行、1-执行中、2-已完成、3-失败）
page (int): 页码，默认值为1
size (int): 每页数量，默认值为10
响应:
成功 (200 OK):

json
复制代码
{
    "currentPage": 1,
    "pageSize": 10,
    "totalPages": 2,
    "totalItems": 15,
    "items": [
        {
            "id": 98765,
            "adId": 12345,
            "adSpaceId": 54321,
            "scheduleTime": "2024-05-01T10:00:00Z",
            "status": 0,
            "createTime": "2024-04-27T12:34:56Z",
            "updateTime": "2024-04-27T12:34:56Z"
        },
        // 更多任务
    ]
}
失败:

401 Unauthorized: 未授权访问
500 Internal Server Error: 服务器内部错误
6.1.4 更新广告投放任务
接口路径: PUT /api/v1/delivery/tasks/{id}

描述: 广告主用户更新指定ID的广告投放任务，任务状态重新置为待执行。

请求头:

Authorization: Bearer <token>
Content-Type: application/json
角色要求: 广告主
路径参数:

id (long): 投放任务ID，必填
请求体:

json
复制代码
{
    "adId": 12345,                // 广告ID，必填
    "adSpaceId": 54321,           // 广告位ID，必填
    "scheduleTime": "2024-05-01T10:00:00Z" // 调度时间，必填，ISO 8601 格式
}
响应:

成功 (200 OK):

json
复制代码
{
    "message": "投放任务更新成功，待执行。"
}
失败:

400 Bad Request: 请求参数错误
401 Unauthorized: 未授权访问
403 Forbidden: 无权限修改该投放任务
404 Not Found: 投放任务不存在
500 Internal Server Error: 服务器内部错误
6.1.5 删除广告投放任务
接口路径: DELETE /api/v1/delivery/tasks/{id}
描述: 广告主用户删除指定ID的广告投放任务。
请求头:
Authorization: Bearer <token>
角色要求: 广告主
路径参数:
id (long): 投放任务ID，必填
响应:
成功 (200 OK):

json
复制代码
{
    "message": "投放任务删除成功。"
}
失败:

401 Unauthorized: 未授权访问
403 Forbidden: 无权限删除该投放任务
404 Not Found: 投放任务不存在
500 Internal Server Error: 服务器内部错误
6.2 广告展示接口
6.2.1 广告展示页面
接口路径: GET /ad/display/{uniquePath}
描述: 广告展示页面，用于在网站主用户的网站中嵌入广告。该页面动态加载并展示广告内容。
请求头:
无需认证
路径参数:
uniquePath (string): 广告展示页面唯一路径，必填
响应:
成功 (200 OK): 返回广告展示页面 HTML
失败:
404 Not Found: 广告展示页面不存在
500 Internal Server Error: 服务器内部错误
6.2.2 获取广告内容
接口路径: GET /api/v1/delivery/ads/display
描述: 获取指定广告位的当前应展示的广告内容。
请求头:
无需认证
查询参数:
adSpaceId (long): 广告位ID，必填
响应:
成功 (200 OK):

json
复制代码
{
    "url": "https://advertiser.com/click",
    "imageUrl": "https://cdn.adplatform.com/materials/12345.jpg"
}
失败:

400 Bad Request: 缺少必要参数或参数格式错误
404 Not Found: 广告位不存在或无可展示广告
500 Internal Server Error: 服务器内部错误