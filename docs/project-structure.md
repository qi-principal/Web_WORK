# 广告平台项目框架说明文档

## 1. 项目概述

本项目是一个广告投放平台，支持广告主发布广告、网站主接入广告，以及管理员进行平台管理。项目采用 Spring Boot + Vue.js 的技术栈，后端使用 MyBatis-Plus 进行数据库操作。

## 2. 技术栈

### 2.1 后端技术栈
- 核心框架：Spring Boot 2.7.5
- ORM框架：MyBatis-Plus 3.5.2
- 数据库：MySQL 8.0
- 缓存：Redis
- 文件存储：阿里云 OSS
- 安全框架：Spring Security
- API文档：Swagger 3.0
- 项目构建：Maven

### 2.2 前端技术栈
- 核心框架：Vue.js
- UI组件库：Element UI
- HTTP客户端：Axios
- 路由：Vue Router
- 状态管理：Vuex

## 3. 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/adplatform/
│   │       ├── common/           # 公共组件
│   │       │   ├── config/       # 配置类
│   │       │   ├── exception/    # 异常处理
│   │       │   └── util/         # 工具类
│   │       ├── module/
│   │       │   ├── ad/          # 广告模块
│   │       │   │   ├── controller/
│   │       │   │   ├── service/
│   │       │   │   ├── mapper/
│   │       │   │   ├── entity/
│   │       │   │   ├── dto/
│   │       │   │   ├── converter/
│   │       │   │   └── enums/
│   │       │   └── user/        # 用户模块
│   │       │       ├── controller/
│   │       │       ├── service/
│   │       │       ├── mapper/
│   │       │       ├── entity/
│   │       │       └── security/
│   ├── resources/
│   │   ├── static/             # 静态资源
│   │   ├── templates/          # 模板文件
│   │   ├── application.yml     # 应用配置
│   │   └── db/                 # 数据库脚本
│   └── webapp/                 # Web资源
└── test/                       # 测试代码
```

## 4. 核心模块说明

### 4.1 广告模块 (ad)
- **功能**：广告管理、素材管理、投放管理
- **主要类**：
  - `AdvertisementController`: 广告相关接口
  - `MaterialController`: 素材管理接口
  - `AdvertisementService`: 广告业务逻辑
  - `MaterialService`: 素材业务逻辑
  - `AdConverter`: 实体转换器

### 4.2 用户模块 (user)
- **功能**：用户管理、角色管理、权限控制
- **主要类**：
  - `UserController`: 用户相关接口
  - `SecurityConfig`: 安全配置
  - `UserService`: 用户业务逻辑
  - `RoleService`: 角色业务逻辑

## 5. 数据库设计

详细的数据库设计请参考 `docs/database/database-design.md`。

主要表结构：
- advertisement: 广告表
- ad_material: 素材表
- ad_material_relation: 广告素材关联表
- user: 用户表
- role: 角色表
- user_role: 用户角色关联表

## 6. API 接口说明

### 6.1 广告相关接口
- `POST /v1/advertisements`: 创建广告
- `PUT /v1/advertisements/{id}`: 更新广告
- `GET /v1/advertisements/{id}`: 获取广告详情
- `GET /v1/advertisements`: 分页查询广告列表
- `POST /v1/advertisements/{id}/submit`: 提交审核
- `POST /v1/advertisements/{id}/approve`: 审核通过
- `POST /v1/advertisements/{id}/reject`: 审核拒绝

### 6.2 素材相关接口
- `POST /v1/materials/upload`: 上传素材
- `GET /v1/materials/{id}`: 获取素材详情
- `GET /v1/materials/ad/{adId}`: 获取广告的素材列表
- `DELETE /v1/materials/{id}`: 删除素材
- `POST /v1/materials/{materialId}/ads/{adId}`: 添加素材到广告
- `DELETE /v1/materials/{materialId}/ads/{adId}`: 从广告中移除素材

### 6.3 用户相关接口
- `POST /v1/users/register`: 用户注册
- `POST /v1/users/login`: 用户登录
- `GET /v1/users/current`: 获取当前用户信息
- `PUT /v1/users/password`: 修改密码

## 7. 开发规范

### 7.1 代码规范
- 遵循阿里巴巴Java开发手册
- 使用统一的代码格式化配置
- 类、方法、变量命名采用驼峰命名法
- 必须添加适当的注释和文档

### 7.2 提交规范
- 提交信息格式：`type(scope): subject`
- type: feat/fix/docs/style/refactor/test/chore
- 每次提交保持功能单一性

### 7.3 分支管理
- master: 主分支，用于生产环境
- develop: 开发分支
- feature/*: 功能分支
- hotfix/*: 紧急修复分支

## 8. 部署说明

### 8.1 环境要求
- JDK 11+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 8.2 配置说明
主要配置文件：`application.yml`
- 数据库配置
- Redis配置
- OSS配置
- 安全配置
- 日志配置

### 8.3 部署步骤
1. 准备环境
2. 配置数据库
3. 修改配置文件
4. 打包：`mvn clean package`
5. 运行：`java -jar xxx.jar`

## 9. 下一步开发建议

1. 完善单元测试覆盖率
2. 添加广告效果统计功能
3. 优化素材管理，支持更多类型
4. 增加支付模块
5. 添加数据分析和报表功能
6. 优化缓存策略
7. 添加消息通知功能
8. 完善日志记录和监控 