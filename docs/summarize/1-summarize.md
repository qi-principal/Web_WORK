# 用户模块开发总结文档

## 1. 项目概述

本模块实现了广告投放平台的用户管理系统，包括用户注册、登录、信息管理等功能。采用了Spring Boot + Spring Security + JWT的技术栈，实现了基于Token的无状态认证机制。

## 2. 技术栈

- 后端框架：Spring Boot 2.7.5
- 安全框架：Spring Security
- 数据库：MySQL + MyBatis-Plus 3.5.2
- 认证方案：JWT (JSON Web Token)
- 前端技术：原生JavaScript + HTML + CSS
- 开发语言：Java 11

## 3. 核心功能

### 3.1 用户认证
- 用户注册：支持广告主和网站主两种角色注册
- 用户登录：基于JWT的token认证
- 退出登录：清除客户端token

### 3.2 用户管理
- 用户信息查询
- 用户状态管理（启用/禁用）
- 个人信息维护

## 4. 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/adplatform/
│   │       ├── common/
│   │       │   ├── exception/
│   │       │   │   ├── ApiException.java          # API异常类
│   │       │   │   ├── BusinessException.java     # 业务异常类
│   │       │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │       │   └── response/
│   │       │       ├── Result.java                # 统一响应结果类
│   │       │       └── ResultCode.java            # 响应状态码枚举
│   │       └── module/
│   │           └── user/
│   │               ├── controller/
│   │               │   ├── AuthController.java     # 认证控制器
│   │               │   └── UserController.java     # 用户控制器
│   │               ├── dto/
│   │               │   ├── LoginRequest.java       # 登录请求DTO
│   │               │   ├── LoginResponse.java      # 登录响应DTO
│   │               │   ├── RegisterRequest.java    # 注册请求DTO
│   │               │   └── UserDTO.java            # 用户信息DTO
│   │               ├── entity/
│   │               │   ├── Role.java               # 角色实体
│   │               │   ├── User.java               # 用户实体
│   │               │   └── UserRole.java           # 用户角色关联实体
│   │               ├── mapper/
│   │               │   └── UserMapper.java         # 用户数据访问接口
│   │               ├── security/
│   │               │   ├── JwtAuthenticationFilter.java # JWT认证过滤器
│   │               │   ├── JwtTokenProvider.java   # JWT令牌工具类
│   │               │   ├── SecurityConfig.java     # 安全配置类
│   │               │   ├── SecurityService.java    # 安全服务类
│   │               │   ├── UserDetailsServiceImpl.java # 用户详情服务实现
│   │               │   └── UserPrincipal.java      # 用户认证信息类
│   │               └── service/
│   │                   ├── UserService.java        # 用户服务接口
│   │                   └── impl/
│   │                       └── UserServiceImpl.java # 用户服务实现类
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   │   └── style.css                      # 样式文件
│       │   ├── js/
│       │   │   ├── api.js                         # API调用封装
│       │   │   └── main.js                        # 主要业务逻辑
│       │   └── index.html                         # 主页面
│       └── application.yml                        # 应用配置文件
└── test/
    └── java/
        └── com/adplatform/
            └── module/
                └── user/
                    ├── controller/
                    │   ├── AuthControllerTest.java  # 认证控制器测试
                    │   └── UserControllerTest.java  # 用户控制器测试
                    ├── service/
                    │   └── UserServiceTest.java     # 用户服务测试
                    └── security/
                        └── JwtTokenProviderTest.java # JWT工具类测试
```

## 5. 数据库设计

### 5.1 数据库表结构

#### 5.1.1 用户表(user)
```sql
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 5.1.2 角色表(role)
```sql
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `code` varchar(50) NOT NULL,
  `description` varchar(200),
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 5.1.3 用户角色关联表(user_role)
```sql
CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 5.2 数据库相关类说明

#### 5.2.1 实体类
- `User.java`: 用户实体类，对应user表
  - 包含用户基本信息字段
  - 使用MyBatis-Plus注解映射数据库
  - 包含审计字段（创建时间、更新时间）

- `Role.java`: 角色实体类，对应role表
  - 定义系统角色信息
  - 包含角色名称、编码和描述

- `UserRole.java`: 用户角色关联实体类，对应user_role表
  - 维护用户和角色的多对多关系
  - 使用复合主键

#### 5.2.2 数据访问层
- `UserMapper.java`: 用户数据访问接口
  - 继承BaseMapper<User>
  - 提供基础的CRUD操作
  - 可扩展自定义的查询方法

#### 5.2.3 数据传输对象
- `UserDTO.java`: 用户信息传输对象
  - 用于前后端数据交互
  - 过滤敏感信息（如密码）
  - 添加必要的业务字段

## 6. API接口说明

### 6.1 认证接口
- POST `/api/v1/auth/register`: 用户注册
- POST `/api/v1/auth/login`: 用户登录

### 6.2 用户管理接口
- GET `/api/v1/users/me`: 获取当前用户信息
- GET `/api/v1/users/{id}`: 获取指定用户信息
- PUT `/api/v1/users/{id}/status`: 更新用户状态

## 7. 前端实现

### 7.1 页面结构
- `index.html`: 主页面
- `css/style.css`: 样式文件
- `js/api.js`: API调用封装
- `js/main.js`: 主要业务逻辑

### 7.2 关键功能
- 表单验证
- Token管理
- 状态管理
- 消息提示

## 8. 注意事项

### 8.1 安全考虑
- 密码加密存储
- Token基于Base64编码
- 接口权限控制
- CORS配置

### 8.2 性能优化
- 使用MyBatis-Plus提升数据访问效率
- 合理的缓存策略
- 异步处理长耗时操作

## 9. 待优化点

1. 添加验证码功能
2. 实现密码重置功能
3. 完善用户信息编辑功能
4. 添加日志记录功能
5. 优化异常处理机制
6. 增加单元测试覆盖率

## 10. 开发环境

- JDK: 11
- IDE: 推荐使用IntelliJ IDEA
- 数据库: MySQL 8.0+
- Maven: 3.6+

## 11. 部署说明

1. 配置数据库连接（application.yml）
2. 配置JWT密钥（application.yml）
3. 执行数据库初始化脚本
4. 配置跨域设置（如需要）
5. 启动应用程序

## 12. 测试账号

- 管理员账号：admin/admin123
- 测试广告主：advertiser/123456
- 测试网站主：publisher/123456

## 13. 联系方式

如有问题，请联系：
- 开发者：andrew
- 邮箱：[开发者邮箱]
- 开发时间：2023-11-21 至 2023-12-19 