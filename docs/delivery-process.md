# 广告投放系统流程说明

## 1. 核心实体

### 1.1 广告投放任务 (AdDeliveryTask)
- 记录广告投放的基本信息
- 包含开始时间、结束时间、优先级等属性
- 状态流转：待投放 -> 投放中 -> 已完成/已暂停

### 1.2 广告投放作业 (AdDeliveryJob)
- 记录具体的投放执行情况
- 关联投放任务和展示页面
- 状态流转：待执行 -> 执行中 -> 已完成/失败

### 1.3 广告展示页面 (AdDisplayPage)
- 记录广告展示页面信息
- 包含唯一路径和当前展示的广告
- 状态：启用/禁用

## 2. 业务流程

### 2.1 广告位创建流程
1. **创建广告位** (`AdSpaceServiceImpl.createAdSpace`)
   - 保存广告位基本信息
   - 调用 `AdDisplayPageService.createDisplayPage` 创建展示页面
   - 生成广告代码

2. **创建展示页面** (`AdDisplayPageServiceImpl.createDisplayPage`)
   - 检查是否已存在展示页面
   - 生成唯一路径
   - 创建并保存页面记录

### 2.2 广告投放流程
1. **定时检查任务** (`SimpleDeliveryExecutor.checkAndExecuteTasks`)
   - 每分钟执行一次
   - 查找需要开始和结束的任务
   - 分别调用相应的处理方法

2. **开始投放任务** (`SimpleDeliveryExecutor.startDeliveryTask`)
   - 更新任务状态为"投放中"
   - 查找对应的广告位页面
   - 为每个页面创建投放作业
   - 执行投放作业

3. **执行投放作业** (`SimpleDeliveryExecutor.executeDeliveryJob`)
   - 更新作业状态为"执行中"
   - 更新页面的当前广告
   - 生成并缓存广告内容
   - 更新作业状态为"已完成"

4. **结束投放任务** (`SimpleDeliveryExecutor.endDeliveryTask`)
   - 更新任务状态为"已完成"
   - 清理相关的投放作业
   - 清理页面和缓存

### 2.3 广告内容管理流程
1. **内容缓存** (`AdContentCacheService`)
   - 缓存广告内容
   - 获取广告内容
   - 删除过期内容

2. **页面展示** (`AdDisplayController`)
   - 处理页面访问请求
   - 获取并展示广告内容
   - 记录展示和点击数据

## 3. 数据访问层

### 3.1 AdDeliveryTaskMapper
- `findTasksToStart`: 查找需要开始投放的任务
  ```sql
  WHERE status = 0 AND start_time <= #{now} AND end_time > #{now}
  ORDER BY priority DESC, create_time ASC
  ```
- `findTasksToEnd`: 查找需要结束投放的任务
  ```sql
  WHERE status = 1 AND end_time <= #{now}
  ORDER BY priority DESC, create_time ASC
  ```

### 3.2 AdDeliveryJobMapper
- `findByTaskId`: 查找指定任务的相关作业
- `findByPageId`: 查找指定页面的相关作业

### 3.3 AdDisplayPageMapper
- `findByAdSpaceId`: 查找指定广告位的展示页面
- `findByUniquePath`: 查找指定唯一路径的展示页面

## 4. 状态管理

### 4.1 投放任务状态 (DeliveryStatus)
- PENDING(0): 待投放
- RUNNING(1): 投放中
- COMPLETED(2): 已完成
- PAUSED(3): 已暂停

### 4.2 展示页面状态
- 0: 禁用
- 1: 启用

## 5. 关键功能点

### 5.1 优先级管理
- 任务优先级影响投放顺序
- 优先级越高越优先投放
- 同优先级按创建时间排序

### 5.2 并发控制
- 使用事务确保数据一致性
- 状态流转严格控制
- 异常处理和回滚机制

### 5.3 缓存策略
- 广告内容使用Redis缓存
- 定期清理过期内容
- 支持实时更新

## 6. 扩展建议

### 6.1 性能优化
- 添加任务执行队列
- 实现任务分片执行
- 优化数据库查询

### 6.2 监控告警
- 添加任务执行监控
- 实现异常告警机制
- 记录详细的操作日志

### 6.3 统计分析
- 实现投放效果统计
- 添加点击率分析
- 生成投放报表 