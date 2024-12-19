# 广告投放系统后端开发指南

## 目录

1. [架构概览](#1-架构概览)
2. [技术选型](#2-技术选型)
3. [开发环境搭建](#3-开发环境搭建)
4. [模块详细说明](#4-模块详细说明)
5. [数据库设计](#5-数据库设计)
6. [部署说明](#6-部署说明)

## 1. 架构概览

### 1.1 系统架构
- 基于 Spring Boot 的单体应用架构
- 采用标准的多层架构：Controller -> Service -> Repository
- 使用 Redis 做缓存和会话管理
- 使用 OSS 存储媒体文件
- 使用 MySQL 做持久化存储
- 使用 RabbitMQ 处理异步任务

### 1.2 目录结构
```
ad-platform-backend/
├── src/main/java/com/adplatform/
│   ├── common/           # 公共模块
│   │   ├── config/      # 配置类
│   │   ├── exception/   # 异常处理
│   │   ├── utils/       # 工具类
│   │   └── response/    # 响应封装
│   ├── module/          # 业务模块
│   │   ├── user/        # 用户模块
│   │   ├── ad/          # 广告模块
│   │   ├── site/        # 网站模块
│   │   ├── payment/     # 支付模块
│   │   └── stats/       # 统计模块
│   ├── security/        # 安全相关
│   └── AdPlatformApplication.java
└── pom.xml
```

## 2. 技术选型

### 2.1 核心框架
- Spring Boot 3.1.5
- Spring Security + JWT 0.12.3
- MyBatis-Plus 3.5.4.1
- Redis 6.x
- MySQL 8.0

### 2.2 中间件
- RabbitMQ 3.9.x
- Elasticsearch 7.x
- Quartz 2.3.x

### 2.3 工具类库
- Hutool 5.8.22
- MapStruct 1.5.5.Final
- Lombok
- JWT 0.12.3

### 2.4 开发环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.x+

## 3. 开发环境搭建

### 3.1 必要条件
- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.x+
- RabbitMQ 3.9+

### 3.2 环境配置
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ad_platform
    username: root
    password: root
  
  redis:
    host: localhost
    port: 6379
    
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

## 4. 模块详细说明

### 4.1 用户模块 (user)

#### 4.1.1 功能说明
- 用户注册登录
- 用户信息管理
- 角色权限管理

#### 4.1.2 关键接口

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    /**
     * 用户注册
     * @param request 注册请求
     * @return 用户信息
     */
    @PostMapping("/register")
    public Result<UserDTO> register(@RequestBody UserRegisterRequest request);
    
    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录结果（含token）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request);
}
```

#### 4.1.3 数据结构
```java
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Integer userType;  // 1-广告主 2-网站主 3-管理员
    private Date createTime;
    private Date updateTime;
    private Integer status;
}
```

### 4.2 广告模块 (ad)

#### 4.2.1 功能说明
- 广告创建与管理
- 广告投放策略
- 广告审核流程
- 广告素材管理

#### 4.2.2 关键接口
```java
@RestController
@RequestMapping("/api/v1/ads")
public class AdController {
    /**
     * 创建广告
     * @param request 广告创建请求
     * @return 广告信息
     */
    @PostMapping
    public Result<AdDTO> createAd(@RequestBody AdCreateRequest request);
    
    /**
     * 提交审核
     * @param id 广告ID
     * @return 操作结果
     */
    @PostMapping("/{id}/submit-review")
    public Result<Void> submitReview(@PathVariable Long id);
    
    /**
     * 更新广告状态
     * @param id 广告ID
     * @param status 目标状态
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
        @PathVariable Long id, 
        @RequestParam AdStatus status
    );
}
```

#### 4.2.3 生命周期状态
```java
public enum AdStatus {
    DRAFT,              // 草稿
    PENDING_REVIEW,     // 待审核
    REVIEWING,          // 审核中
    REJECTED,           // 已拒绝
    APPROVED,           // 已通过
    RUNNING,            // 投放中
    PAUSED,             // 已暂停
    COMPLETED           // 已完成
}
```

### 4.3 网站模块 (site)

#### 4.3.1 功能说明
- 网站信息管理
- 广告位管理
- 广告代码管理

#### 4.3.2 关键接口
```java
@RestController
@RequestMapping("/api/v1/sites")
public class SiteController {
    /**
     * 创建网站
     * @param request 网站创建请求
     * @return 网站信息
     */
    @PostMapping
    public Result<WebsiteDTO> createWebsite(
        @RequestBody WebsiteCreateRequest request
    );
    
    /**
     * 获取广告代码
     * @param id 广告位ID
     * @return 广告代码
     */
    @GetMapping("/ad-spaces/{id}/code")
    public Result<String> getAdCode(@PathVariable Long id);
}
```

### 4.4 支付模块 (payment)

#### 4.4.1 功能说明
- 充值功能
- 支付管理
- 交易记录
- 结算功能

#### 4.4.2 关键接口
```java
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    /**
     * 创建充值订单
     * @param request 充值请求
     * @return 订单信息
     */
    @PostMapping("/recharge")
    public Result<RechargeOrderDTO> createRechargeOrder(
        @RequestBody RechargeOrderRequest request
    );
    
    /**
     * 支付回调
     * @param channel 支付渠道
     * @param request HTTP请求
     * @return 处理结果
     */
    @PostMapping("/notify/{channel}")
    public String handlePaymentNotify(
        @PathVariable String channel,
        HttpServletRequest request
    );
}
```

### 4.5 统计模块 (stats)

#### 4.5.1 功能说明
- 广告展示统计
- 点击率统计
- 效果分析
- 收益统计

#### 4.5.2 关键接口
```java
@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {
    /**
     * 获取广告统计数据
     * @param adId 广告ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    @GetMapping("/ads/{adId}")
    public Result<AdStatsDTO> getAdStats(
        @PathVariable Long adId,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
    );
}
```

## 5. 数据库设计

### 5.1 用户相关表
```sql
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `user_type` tinyint NOT NULL,
  `status` tinyint NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 5.2 广告相关表
```sql
CREATE TABLE `advertisement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `content` text,
  `user_id` bigint NOT NULL,
  `status` tinyint NOT NULL,
  `budget` decimal(10,2) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 6. 部署说明

### 6.1 打包
```bash
mvn clean package -DskipTests
```

### 6.2 运行
```bash
java -jar ad-platform-backend.jar \
  --spring.profiles.active=prod \
  --server.port=8080
```

### 6.3 配置说明
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/ad_platform
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  redis:
    host: prod-redis
    port: 6379
    password: ${REDIS_PASSWORD}
```

## 7. 开发规范

### 7.1 代码规范
- 遵循 Alibaba Java 编程规范
- 使用 Lombok 简化代码
- 使用统一的响应格式 Result<T>
- 统一异常处理
- 添加完整的接口文档注释

### 7.2 Git 规范
- feature/* : 新功能分支
- bugfix/* : 问题修复分支
- release/* : 发布分支

### 7.3 提交信息规范
- feat: 新功能
- fix: 修复问题
- docs: 文档变更
- style: 代码格式
- refactor: 代码重构
- test: 测试相关
- chore: 其他变更
