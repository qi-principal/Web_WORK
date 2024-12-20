# 广告投放平台项目指南

## 1. 项目概述

本项目是一个完整的广告投放平台，支持广告主发布广告、网站主接入广告，以及管理员进行平台管理。采用前后端分离架构，使用Spring Boot + Vue.js技术栈开发。

### 1.1 核心功能

#### 1.1.1 用户模块
- 用户认证：注册、登录、退出
- 用户管理：信息查询、状态管理
- 角色管理：广告主、网站主和管理员三种角色

#### 1.1.2 广告模块
- 广告管理：创建、编辑、删除和状态流转
- 素材管理：多种类型素材的上传、预览和管理
- OSS存储：集成阿里云OSS服务存储素材文件

## 2. 技术栈

### 2.1 后端技术栈
- 核心框架：Spring Boot 2.7.5
- ORM框架：MyBatis-Plus 3.5.2
- 数据库：MySQL 8.0
- 缓存：Redis
- 文件存储：阿里云 OSS
- 安全框架：Spring Security + JWT
- API文档：Swagger 3.0
- 项目构建：Maven 3.6+

### 2.2 前端技术栈
- 核心框架：Vue.js 2.x
- UI组件库：Element UI 2.15.7
- HTTP客户端：Axios
- 路由：Vue Router
- 状态管理：Vuex

### 2.3 测试框架
- 单元测试：JUnit 5
- 接口测试：Postman
- UI测试：Selenium WebDriver

## 3. 系统架构

### 3.1 项目结构
```
src/
├── main/
│   ├── java/
│   │   └── com/adplatform/
│   │       ├── common/                           # 公共模块
│   │       │   ├── config/                       # 配置类
│   │       │   ├── exception/                    # 异常处理
│   │       │   │   ├── ApiException.java         # API异常
│   │       │   │   ├── BusinessException.java    # 业务异常
│   │       │   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   │       │   ├── response/                     # 响应处理
│   │       │   │   ├── Result.java              # 统一响应结果
│   │       │   │   └── ResultCode.java          # 响应状态码
│   │       │   └── util/                        # 工具类
│   │       └── module/                          # 业务模块
│   │           ├── user/                        # 用户模块
│   │           │   ├── controller/              # 控制器层
│   │           │   ├── service/                 # 服务层
│   │           │   ├── mapper/                  # 数据访问层
│   │           │   ├── entity/                  # 实体类
│   │           │   └── security/                # 安全相关
│   │           └── ad/                          # 广告模块
│   │               ├── controller/              # 控制器层
│   │               ├── service/                 # 服务层
│   │               ├── mapper/                  # 数据访问层
│   │               ├── entity/                  # 实体类
│   │               ├── dto/                     # 数据传输对象
│   │               ├── converter/               # 对象转换器
│   │               └── enums/                   # 枚举类
│   ├── resources/
│   │   ├── static/                             # 静态资源
│   │   ├── templates/                          # 模板文件
│   │   ├── application.yml                     # 应用配置
│   │   └── db/                                 # 数据库脚本
│   └── webapp/                                 # Web资源
└── test/                                       # 测试代码
```

### 3.2 分层说明

#### 3.2.1 控制器层 (Controller)
- 处理HTTP请求
- 参数验证
- 权限控制
- 调用服务层处理业务逻辑

#### 3.2.2 服务层 (Service)
- 实现业务逻辑
- 事务管理
- 数据校验
- 调用数据访问层操作数据

#### 3.2.3 数据访问层 (Mapper)
- 数据库操作
- SQL映射
- 查询优化
- 使用MyBatis-Plus简化开发

#### 3.2.4 实体层 (Entity)
- 数据库映射
- 业务属性
- 关联关系
- 使用Lombok简化代码

## 4. 数据库设计

详细的数据库设计请参考 `docs/database/database-design.md`。

### 4.1 主要表结构
- advertisement: 广告表
- ad_material: 素材表
- ad_material_relation: 广告素材关联表
- user: 用户表
- role: 角色表
- user_role: 用户角色关联表

## 5. API接口说明

### 5.1 广告相关接口
- `POST /v1/advertisements`: 创建广告
- `PUT /v1/advertisements/{id}`: 更新广告
- `GET /v1/advertisements/{id}`: 获取广告详情
- `GET /v1/advertisements`: 分页查询广告列表
- `POST /v1/advertisements/{id}/submit`: 提交审核
- `POST /v1/advertisements/{id}/approve`: 审核通过
- `POST /v1/advertisements/{id}/reject`: 审核拒绝

### 5.2 素材相关接口
- `POST /v1/materials/upload`: 上传素材
- `GET /v1/materials/{id}`: 获取素材详情
- `GET /v1/materials/ad/{adId}`: 获取广告的素材列表
- `DELETE /v1/materials/{id}`: 删除素材
- `POST /v1/materials/{materialId}/ads/{adId}`: 添加素材到广告
- `DELETE /v1/materials/{materialId}/ads/{adId}`: 从广告中移除素材

### 5.3 用户相关接口
- `POST /v1/users/register`: 用户注册
- `POST /v1/users/login`: 用户登录
- `GET /v1/users/current`: 获取当前用户信息
- `PUT /v1/users/password`: 修改密码

## 6. 安全措施

### 6.1 认证授权
- JWT token认证
- 基于角色的权限控制
- 密码加密存储

### 6.2 数据安全
- 参数验证和过滤
- SQL注入防护
- XSS防护

### 6.3 文件安全
- 文件上传限制
- 文件类型校验
- OSS访问权限控制

## 7. 性能优化

### 7.1 数据库优化
- 索引优化
- 分页查询
- 慢查询优化

### 7.2 缓存优化
- Redis缓存
- 本地缓存
- 缓存更新策略

### 7.3 文件处理优化
- 图片压缩
- 分片上传
- CDN加速

## 8. 开发规范

### 8.1 代码规范
- 遵循阿里巴巴Java开发手册
- 使用统一的代码格式化配置
- 类、方法、变量命名采用驼峰命名法
- 必须添加适当的注释和文档

### 8.2 提交规范
- 提交信息格式：`type(scope): subject`
- type: feat/fix/docs/style/refactor/test/chore
- 每次提交保持功能单一性

### 8.3 分支管理
- master: 主分支，用于生产环境
- develop: 开发分支
- feature/*: 功能分支
- hotfix/*: 紧急修复分支

## 9. 部署说明

### 9.1 环境要求
- JDK 11+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+
- Node.js 12+

### 9.2 配置说明
主要配置文件：`application.yml`
- 数据库配置
- Redis配置
- OSS配置
- 安全配置
- 日志配置

### 9.3 部署步骤
1. 准备环境
2. 配置数据库
3. 修改配置文件
4. 打包：`mvn clean package`
5. 运行：`java -jar xxx.jar`

## 10. 下一步开发建议

1. 完善单元测试覆盖率
2. 添加广告效果统计功能
3. 优化素材管理，支持更多类型
4. 增加支付模块
5. 添加数据分析和报表功能
6. 优化缓存策略
7. 添加消息通知功能
8. 完善日志记录和监控
9. 增加接口版本控制
10. 优化错误码和错误信息
11. 添加接口限流措施
12. 增加监控和告警

## 11. 相关文档

- API文档：`http://localhost:8181/api/swagger-ui.html`
- Postman集合：`postman_collection.json`
- 环境配置：`postman_environment.json`
- 数据库文档：`docs/database/`
- 测试报告：`docs/test-reports/` 