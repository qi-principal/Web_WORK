# 广告投放平台开发总结文档

## 1. 项目概述

本项目实现了一个完整的广告投放平台，包括用户管理系统和广告管理系统两大核心模块。采用前后端分离架构，使用Spring Boot + Vue.js技术栈开发。

### 1.1 核心功能

#### 1.1.1 用户模块
- 用户认证：注册、登录、退出
- 用户管理：信息查询、状态管理
- 角色管理：广告主和网站主两种角色

#### 1.1.2 广告模块
- 广告管理：创建、编辑、删除和状态流转
- 素材管理：多种类型素材的上传、预览和管理
- OSS存储：集成阿里云OSS服务存储素材文件

### 1.2 技术栈

#### 1.2.1 后端技术
- 核心框架：Spring Boot 2.7.5
- 安全框架：Spring Security
- 数据库：MySQL + MyBatis-Plus 3.5.2
- 认证方案：JWT (JSON Web Token)
- 缓存：Redis

#### 1.2.2 前端技术
- 框架：Vue.js 2.x
- UI组件：Element UI 2.15.7
- HTTP客户端：Axios

#### 1.2.3 存储服务
- 文件存储：阿里云OSS

#### 1.2.4 测试框架
- 单元测试：JUnit 5
- 接口测试：Postman
- UI测试：Selenium WebDriver

## 2. 系统架构

### 2.1 项目结构
```
src/
├── main/
│   ├── java/
│   │   └── com/adplatform/
│   │       ├── common/                           # 公共模块
│   │       │   ├── exception/                    # 异常处理
│   │       │   │   ├── ApiException.java         # API异常
│   │       │   │   ├── BusinessException.java    # 业务异常
│   │       │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │       │   └── response/                    # 响应处理
│   │       │       ├── Result.java              # 统一响应结果
│   │       │       └── ResultCode.java          # 响应状态码
│   │       └── module/                          # 业务模块
│   │           ├── user/                        # 用户模块
│   │           │   ├── controller/              # 控制器层
│   │           │   │   ├── AuthController.java  # 认证控制器
│   │           │   │   └── UserController.java  # 用户控制器
│   │           │   ├── dto/                     # 数据传输对象
│   │           │   │   ├── LoginRequest.java    # 登录请求
│   │           │   │   ├── LoginResponse.java   # 登录响应
│   │           │   │   └── UserDTO.java         # 用户DTO
│   │           │   ├── entity/                  # 实体类
│   │           │   │   ├── User.java            # 用户实体
│   │           │   │   └── Role.java            # 角色实体
│   │           │   ├── mapper/                  # 数据访问层
│   │           │   │   └── UserMapper.java      # 用户数据访问
│   │           │   ├── security/                # 安全相关
│   │           │   │   ├── JwtTokenProvider.java # JWT工具类
│   │           │   │   └── SecurityConfig.java   # 安全配置
│   │           │   └── service/                 # 服务层
│   │           │       ├── UserService.java     # 用户服务接口
│   │           │       └── impl/                # 服务实现
│   │           │           └── UserServiceImpl.java # 用户服务实现
│   │           └── ad/                          # 广告模块
│   │               ├── config/                  # 模块配置
│   │               │   └── SwaggerConfig.java   # Swagger配置
│   │               ├── controller/              # 控制器层
│   │               │   ├── AdvertisementController.java # 广告控制器
│   │               │   └── MaterialController.java # 素材控制器
│   │               ├── converter/               # 对象转换器
│   │               │   └── AdConverter.java     # 广告对象转换
│   │               ├── dto/                     # 数据传输对象
│   │               │   ├── AdvertisementDTO.java # 广告DTO
│   │               │   └── MaterialDTO.java     # 素材DTO
│   │               ├── entity/                  # 实体类
│   │               │   ├── Advertisement.java   # 广告实体
│   │               │   └── Material.java        # 素材实体
│   │               ├── enums/                   # 枚举类
│   │               │   ├── AdStatus.java        # 广告状态
│   │               │   └── MaterialType.java    # 素材类型
│   │               ├── mapper/                  # 数据访问层
│   │               │   ├── AdvertisementMapper.java # 广告数据访问
│   │               │   └── MaterialMapper.java  # 素材数据访问
│   │               └── service/                 # 服务层
│   │                   ├── AdvertisementService.java # 广告服务接口
│   │                   ├── MaterialService.java # 素材服务接口
│   │                   └── impl/                # 服务实现
│   │                       ├── AdvertisementServiceImpl.java # 广告服务实现
│   │                       └── MaterialServiceImpl.java # 素材服务实现
│   └── resources/                               # 资源文件
│       ├── application.yml                      # 应用配置
│       ├── mapper/                             # MyBatis映射文件
│       └── static/                             # 静态资源
└── test/                                       # 测试目录
    └── java/
        └── com/adplatform/
            ├── user/                           # 用户模块测试
            │   ├── controller/                 # 控制器测试
            │   └── service/                    # 服务层测试
            └── ad/                             # 广告模块测试
                ├── controller/                 # 控制器测试
                └── service/                    # 服务层测试
```

### 2.1.1 模块说明

1. **公共模块 (common)**
   - 异常处理：统一的异常处理机制
   - 响应处理：统一的响应格式

2. **用户模块 (user)**
   - 控制器层：处理HTTP请求
   - 服务层：实现业务逻辑
   - 数据访问层：与数据库交互
   - 安全相关：JWT认证和授权

3. **广告模块 (ad)**
   - 广告管理：完整的CRUD操作
   - 素材管理：文件上传和管理
   - 状态管理：广告状态流转
   - 数据转换：DTO与实体转换

### 2.1.2 分层说明

1. **控制器层 (Controller)**
   - 处理HTTP请求
   - 参数验证
   - 权限控制

2. **服务层 (Service)**
   - 实现业务逻辑
   - 事务管理
   - 数据校验

3. **数据访问层 (Mapper)**
   - 数据库操作
   - SQL映射
   - 查询优化

4. **实体层 (Entity)**
   - 数据库映射
   - 业务属性
   - 关联关系

### 2.2 数据库设计

#### 2.2.1 用户相关表
```sql
-- 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20),
  `user_type` tinyint NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `balance` decimal(10,2) DEFAULT '0.00',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);

-- 角色表
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `code` varchar(50) NOT NULL,
  `description` varchar(200),
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);

-- 用户角色关联表
CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`)
);
```

#### 2.2.2 广告相关表
```sql
-- 广告表
CREATE TABLE `advertisement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `description` text,
  `status` tinyint NOT NULL,
  `user_id` bigint NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);

-- 素材表
CREATE TABLE `material` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` tinyint NOT NULL,
  `url` varchar(255) NOT NULL,
  `size` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);
```

## 3. API文档实现

### 3.1 Swagger配置
项目集成了Swagger 2.0来自动生成API文档，主要配置包括：

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.adplatform.module"))
            .paths(PathSelectors.any())
            .build();
    }
}
```

### 3.2 接口文档说明

#### 3.2.1 用户认证接口
- 用户注册：POST `/v1/auth/register`
- 用户登录：POST `/v1/auth/login`

#### 3.2.2 用户管理接口
- 获取用户信息：GET `/v1/users/{id}`
- 更新用户状态：PUT `/v1/users/{id}/status`
- 获取当前用户信息：GET `/v1/users/me`

#### 3.2.3 广告管理接口
- 创建广告：POST `/v1/ads`
- 更新广告：PUT `/v1/ads/{id}`
- 获取广告详情：GET `/v1/ads/{id}`
- 获取广告列表：GET `/v1/ads`
- 提交审核：POST `/v1/ads/{id}/submit`
- 审核通过：POST `/v1/ads/{id}/approve`
- 审核拒绝：POST `/v1/ads/{id}/reject`
- 开始投放：POST `/v1/ads/{id}/start`
- 暂停投放：POST `/v1/ads/{id}/pause`

#### 3.2.4 素材管理接口
- 上传素材：POST `/v1/materials/upload`
- 获取素材详情：GET `/v1/materials/{id}`
- 获取广告素材列表：GET `/v1/materials/ad/{adId}`
- 删除素材：DELETE `/v1/materials/{id}`

### 3.3 接口注解说明
```java
@Api(tags = "模块名称")            // 模块分组
@ApiOperation("接口说明")          // 接口描述
@ApiResponses({                    // 响应说明
    @ApiResponse(code = 200, message = "成功"),
    @ApiResponse(code = 400, message = "参数错误")
})
@ApiParam("参数说明")              // 参数描述
```

## 4. 测试覆盖

### 4.1 单元测试
- 用户服务测试：UserServiceTest
- 认证服务测试：AuthServiceTest
- 广告服务测试：AdvertisementServiceTest
- 素材服务测试：MaterialServiceTest

### 4.2 集成测试
- API接口测试：使用Postman测试集合
- 前端页面测试：使用Selenium实现
- 文件上传测试：OSS服务测试

## 5. 安全措施

### 5.1 认证授权
- JWT token认证
- 基于角色的权限控制
- 密码加密存储

### 5.2 数据安全
- 参数验证和过滤
- SQL注入防护
- XSS防护

### 5.3 文件安全
- 文件上传限制
- 文件类型校验
- OSS访问权限控制

## 6. 性能优化

### 6.1 数据库优化
- 索引优��
- 分页查询
- 慢查询优化

### 6.2 缓存优化
- Redis缓存
- 本地缓存
- 缓存更新策略

### 6.3 文件处理优化
- 图片压缩
- 分片上传
- CDN加速

## 7. 部署说明

### 7.1 环境要求
- JDK: 11+
- MySQL: 8.0+
- Redis: 6.0+
- Maven: 3.6+
- Node.js: 12+

### 7.2 配置说明
- 数据库配置
- Redis配置
- OSS配置
- JWT配置

## 8. 后续优化建议

1. 完善API文档和示例
2. 增加接口版本控制
3. 优化错误码和错误信息
4. 添加接口限流措施
5. 增加监控和告警
6. 优化缓存策略
7. 完善测试覆盖率
8. 添加更多数据统计功能

## 9. 相关文档

- API文档：`http://localhost:8181/api/swagger-ui.html`
- Postman集合：`postman_collection.json`
- 环境配置：`postman_environment.json`
- 数据库文档：`docs/database/`
- 测试报告：`docs/test-reports/`