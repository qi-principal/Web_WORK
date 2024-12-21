# Quartz 调度服务实现文档

## 1. 整体架构

### 1.1 核心组件
- `QuartzConfig`: Quartz配置类，负责配置调度器
- `QuartzDeliveryJob`: 广告投放任务的具体执行类
- `DeliveryJobListener`: 任务监听器，监控任务执行状态
- `AdDeliveryServiceImpl`: 广告投放服务，管理投放任务的创建、更新和删除

### 1.2 数据模型
- `AdDeliveryTask`: 投放任务实体
- `AdDeliveryJob`: 投放作业实体
- `DeliveryStatus`: 投放状态枚举

## 2. 详细实现

### 2.1 任务调度配置
```java
@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail deliveryJobDetail() {
        return JobBuilder.newJob(QuartzDeliveryJob.class)
                .withIdentity("deliveryJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger deliveryJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(deliveryJobDetail())
                .withIdentity("deliveryTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();
    }
}
```

### 2.2 任务执行
```java
@DisallowConcurrentExecution
public class QuartzDeliveryJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        // 1. 从JobDataMap获取任务ID
        // 2. 调用投放服务执行任务
        // 3. 更新任务状态
    }
}
```

### 2.3 任务管理流程

#### 创建任务
1. 接收任务请求
2. 保存任务信息
3. 创建Quartz调度任务
4. 返回任务信息

```java
public DeliveryTaskResponse createDeliveryTask(DeliveryTaskRequest request) {
    // 1. 转换请求为实体
    AdDeliveryTask task = DeliveryConverter.INSTANCE.toEntity(request);
    
    // 2. 保存任务
    taskMapper.insert(task);
    
    // 3. 创建调度任务
    scheduleDeliveryTask(task);
    
    return DeliveryConverter.INSTANCE.toResponse(task);
}
```

#### 更新任务
1. 验证任务存在
2. 更新任务信息
3. 重新调度任务
4. 返回更新后的任务信息

```java
public DeliveryTaskResponse updateDeliveryTask(Long taskId, DeliveryTaskRequest request) {
    // 1. 验证并获取任务
    AdDeliveryTask task = getTask(taskId);
    
    // 2. 更新任务信息
    updateTaskInfo(task, request);
    
    // 3. 重新调度任务
    rescheduleDeliveryTask(task);
    
    return DeliveryConverter.INSTANCE.toResponse(task);
}
```

#### 删除任务
1. 验证任务存在
2. 删除任务记录
3. 删除相关作业
4. 删除调度任务

```java
public void deleteDeliveryTask(Long taskId) {
    // 1. 删除任务记录
    taskMapper.deleteById(taskId);
    
    // 2. 删除相关作业
    deleteRelatedJobs(taskId);
    
    // 3. 删除调度任务
    deleteQuartzJob(taskId);
}
```

### 2.4 任务执行流程

1. **任务触发**
   - Quartz调度器根据配置的时间触发任务
   - 调用`QuartzDeliveryJob.execute()`方法

2. **任务执行**
   ```java
   public void executeDeliveryTask(Long taskId) {
       // 1. 创建执行作业
       AdDeliveryJob job = createJob(taskId);
       
       try {
           // 2. 更新任务状态
           updateTaskStatus(taskId, DeliveryStatus.RUNNING);
           
           // 3. 执行广告投放
           String result = deliveryExecutor.execute(task);
           
           // 4. 更新作业状态
           updateJobStatus(job, DeliveryStatus.COMPLETED, result);
           
           // 5. 更新任务状态
           updateTaskStatus(taskId, DeliveryStatus.COMPLETED);
           
       } catch (Exception e) {
           handleExecutionError(job, e);
       }
   }
   ```

3. **状态管理**
   - PENDING: 待执行
   - RUNNING: 执行中
   - COMPLETED: 已完成
   - FAILED: 执行失败

## 3. 监控和错误处理

### 3.1 任务监听器
```java
public class DeliveryJobListener implements JobListener {
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        // 任务执行前的处理
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        // 任务执行后的处理
    }
}
```

### 3.2 错误处理
1. 捕获执行异常
2. 记录错误日志
3. 更新任务状态
4. 通知相关人员

## 4. 优化建议

1. **性能优化**
   - 使用线程池管理任务执行
   - 实现任务优先级机制
   - 添加任务执行超时控制

2. **可靠性优化**
   - 实现任务重试机制
   - 添加任务执行日志
   - 实现任务恢复机制

3. **扩展性优化**
   - 支持动态调度策略
   - 实现任务分片执行
   - 添加任务执行指标监控

## 5. 使用示例

```java
// 创建投放任务
DeliveryTaskRequest request = new DeliveryTaskRequest();
request.setAdId(123L);
request.setAdSpaceId(456L);
request.setScheduleTime(LocalDateTime.now().plusHours(1));

DeliveryTaskResponse response = adDeliveryService.createDeliveryTask(request);

// 更新投放任务
request.setScheduleTime(LocalDateTime.now().plusHours(2));
response = adDeliveryService.updateDeliveryTask(taskId, request);

// 删除投放任务
adDeliveryService.deleteDeliveryTask(taskId);
``` 