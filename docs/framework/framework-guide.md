# 广告平台项目框架详细说明

## 1. 整体架构

### 1.1 架构图
```
+------------------+     +------------------+     +------------------+
|   Presentation   |     |    Business      |     |      Data        |
|      Layer       |     |     Layer        |     |     Layer        |
+------------------+     +------------------+     +------------------+
| - Controllers    |     | - Services       |     | - Repositories   |
| - DTOs          |     | - Domain Logic   |     | - Entities       |
| - Validators    |     | - Events         |     | - Mappers        |
+------------------+     +------------------+     +------------------+
```

### 1.2 技术选型
- 基础框架：Spring Boot 2.7.5
- 持久层：MyBatis-Plus 3.5.2
- 数据库：MySQL 8.0
- 缓存：Redis
- 认证：Spring Security + JWT
- 文档：Swagger 3.0
- 构建：Maven 3.6+

## 2. 项目结构详解

### 2.1 目录结构
```
src/
├── main/
│   ├── java/
│   │   └── com/adplatform/
│   │       ├── common/                           # 公共模块
│   │       │   ├── exception/                    # 异常处理
│   │       │   │   ├── ApiException.java         # API异常
│   │       │   │   ├── BusinessException.java    # 业务异常
│   │       │   │   └── GlobalExceptionHandler.java # 全局异常处理
│   │       │   ├── response/                     # 响应处理
│   │       │   │   ├── Result.java              # 统一响应结果
│   │       │   │   └── ResultCode.java          # 响应状态码
│   │       └── module/                          # 业务模块
│   │           ├── user/                        # 用户模块
│   │           │   ├── controller/              # 控制器层
│   │           │   │   ├── AuthController.java  # 认证控制器
│   │           │   │   └── UserController.java  # 用户控制器
│   │           │   ├── service/                 # 服务层
│   │           │   │   ├── UserService.java     # 用户服务接口
│   │           │   │   └── impl/                # 服务实现
│   │           │   │       └── UserServiceImpl.java
│   │           │   ├── mapper/                  # 数据访问层
│   │           │   │   └── UserMapper.java      # 用户数据访问
│   │           │   ├── entity/                  # 实体类
│   │           │   │   ├── User.java            # 用户实体
│   │           │   │   └── Role.java            # 角色实体
│   │           │   ├── dto/                     # 数据传输对象
│   │           │   │   ├── UserDTO.java         # 用户DTO
│   │           │   │   └── LoginDTO.java        # 登录DTO
│   │           │   └── converter/               # 对象转换器
│   │           │       └── UserConverter.java   # 用户对象转换器
│   │           └── ad/                          # 广告模块
│   │               ├── controller/              # 控制器层
│   │               │   ├── AdvertisementController.java
│   │               │   └── MaterialController.java
│   │               ├── service/                 # 服务层
│   │               │   ├── AdvertisementService.java
│   │               │   ├── MaterialService.java
│   │               │   └── impl/
│   │               │       ├── AdvertisementServiceImpl.java
│   │               │       └── MaterialServiceImpl.java
│   │               ├── mapper/                  # 数据访问层
│   │               │   ├── AdvertisementMapper.java
│   │               │   └── MaterialMapper.java
│   │               ├── entity/                  # 实体类
│   │               │   ├── Advertisement.java
│   │               │   └── Material.java
│   │               ├── dto/                     # 数据传输对象
│   │               │   ├── AdvertisementDTO.java
│   │               │   └── MaterialDTO.java
│   │               ├── converter/               # 对象转换器
│   │               │   └── AdConverter.java
│   │               └── enums/                   # 枚举类
│   │                   ├── AdStatus.java
│   │                   └── MaterialType.java
│   ├── resources/
│   │   ├── mapper/                             # MyBatis映射文件
│   │   │   ├── UserMapper.xml
│   │   │   └── AdvertisementMapper.xml
│   │   ├── static/                             # 静态资源
│   │   │   └── advertisement/
│   │   │       └── list.html
│   │   ├── templates/                          # 模板文件
│   │   ├── application.yml                     # 应用配置
│   │   ├── application-dev.yml                 # 开发环境配置
│   │   ├── application-prod.yml                # 生产环境配置
│   │   └── db/                                 # 数据库脚本
│   │       ├── init.sql                        # 初始化脚本
│   │       └── migration/                      # 迁移脚本
│   │           └── V1.1__add_material_relation.sql
│   └── webapp/                                 # Web资源
└── test/                                       # 测试代码
    └── java/
        └── com/adplatform/
            ├── common/                         # 公共测试
            │   └── BaseTest.java
            ├── user/                           # 用户模块测试
            │   ├── controller/
            │   │   └── UserControllerTest.java
            │   └── service/
            │       └── UserServiceTest.java
            └── ad/                             # 广告模块测试
                ├── controller/
                │   ├── AdvertisementControllerTest.java
                │   └── MaterialControllerTest.java
                └── service/
                    ├── AdvertisementServiceTest.java
                    └── MaterialServiceTest.java
```

## 3. 核心模块说明

### 3.1 公共模块 (common)
- **配置类**：系统配置、第三方服务配置
- **异常处理**：统一异常处理机制
- **响应处理**：统一响应格式
- **安全相关**：认证授权配置
- **工具类**：通用工具方法
- **切面**：日志、验证等横切关注点

### 3.2 用户模块 (user)
- **认证授权**：用户登录、注册、权限控制
- **用户管理**：用户信息CRUD
- **角色管理**：角色分配、权限设置
- **安全机制**：JWT token、密码加密

### 3.3 广告模块 (ad)
- **广告管理**：广告CRUD、状态流转
- **素材管理**：素材上传、关联
- **投放管理**：广告投放控制
- **数据统计**：投放效果统计

## 4. 分层设计

### 4.1 表现层 (Presentation Layer)
- **职责**：处理HTTP请求、参数校验、返回结果
- **组件**：Controller、DTO、Validator
- **规范**：
  - 统一的响应格式
  - 请求参数验证
  - 异常处理
  - 权限控制

### 4.2 业务层 (Business Layer)
- **职责**：实现业务逻辑、事务管理
- **组件**：Service、Domain Object
- **规范**：
  - 事务管理
  - 业务校验
  - 领域逻辑封装
  - 依赖注入

### 4.3 数据层 (Data Layer)
- **职责**：数据访问、ORM映射
- **组件**：Entity、Mapper、Repository
- **规范**：
  - ORM映射
  - SQL优化
  - 数据库事务
  - 缓存处理

## 5. 关键技术实现

### 5.1 认证授权
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/v1/auth/**").permitAll()
            .anyRequest().authenticated();
    }
}
```

### 5.2 统一响应
```java
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }
}
```

### 5.3 异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
}
```

## 6. 开发规范

### 6.1 命名规范
- **包名**：全小写，例如：com.adplatform.module.user
- **类名**：大驼峰，例如：UserController
- **方法名**：小驼峰，例如：getUserById
- **变量名**：小驼峰，例如：userId
- **常量名**：全大写下划线，例如：MAX_SIZE

### 6.2 注释规范
- **类注释**：包含作者、日期、描述
- **方法注释**：包含功能描述、参数说明、返回值说明
- **关键代码注释**：复杂逻辑的说明

### 6.3 代码格式
- 使用统一的代码格式化配置
- 遵循阿里巴巴Java开发手册
- 保持适当的空行和缩进

## 7. 测试规范

### 7.1 单元测试
- 每个Service方法都要有对应的测试
- 使用Mock框架模拟依赖
- 测试覆盖率要求80%以上

### 7.2 集成测试
- 关键业务流程的端到端测试
- 数据库操作的集成测试
- 外部服务调用的集成测试

## 8. 部署架构

### 8.1 开发环境
- 本地开发环境
- 开发数据库
- 本地Redis
- 文件存储模拟

### 8.2 测试环境
- 独立测试服务器
- 测试数据库
- 测试Redis集群
- 测试OSS环境

### 8.3 生产环境
- 负载均衡
- 主从数据库
- Redis集群
- CDN加速
- 监控系统

## 9. 性能优化

### 9.1 数据库优化
- 合理的索引设计
- SQL语句优化
- 分页查询优化
- 批量操作优化

### 9.2 缓存优化
- 多级缓存策略
- 缓存预热
- 缓存更新策略
- 缓存穿透防护

### 9.3 代码优化
- 循环优化
- 对象复用
- 延迟加载
- 异步处理 