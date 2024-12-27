# 广告平台API参考文档

## 0. 接口通用信息

### 0.1 基础配置
- 基础URL：`http://localhost:8181/api`
- 接口版本：v1
- 数据格式：JSON
- 字符编码：UTF-8

### 0.2 认证方式
- 类型：JWT Token
- 有效期：24小时
- 获取方式：登录接口
- 使用方式：
  ```
  Header: {
    "Authorization": "Bearer <token>"
  }
  ```

### 0.3 通用响应格式
```json
{
    "code": 200,          // 状态码
    "message": "成功",     // 提示信息
    "data": {             // 数据主体
        // 具体数据
    }
}
```

### 0.4 文件上传说明
- 上传方式：multipart/form-data
- 大小限制：
  - 单个文件：10MB
  - 总请求大小：10MB
- 存储方式：阿里云OSS
- 访问域名：`https://web-andrew.oss-cn-shanghai.aliyuncs.com`

### 0.5 分页参数说明
- page：页码，从1开始
- size：每页数量，默认10
- 示例：`?page=1&size=10`

## 1. 用户认证模块

### 1.1 用户注册
- **接口**：`POST /v1/auth/register`
- **描述**：新用户注册
- **请求参数**：
  ```json
  {
    "username": "string",    // 用户名
    "password": "string",    // 密码
    "email": "string",       // 邮箱
    "userType": "integer"    // 用户类型：1-广告主，2-网站主
  }
  ```
- **响应示例**：
  ```json
  {
    "code": 200,
    "message": "注册成功",
    "data": {
      "id": "integer",
      "username": "string",
      "email": "string",
      "userType": "integer",
      "createTime": "string"
    }
  }
  ```

### 1.2 用户登录
- **接口**：`POST /v1/auth/login`
- **描述**：用户登录认证
- **请求参数**：
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **响应示例**：
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "string",
      "user": {
        "id": "integer",
        "username": "string",
        "email": "string",
        "userType": "integer"
      }
    }
  }
  ```

## 2. 用户管理模块

### 2.1 获取用户信息
- **接口**：`GET /v1/users/{id}`
- **描述**：获取指定用户信息
- **权限**：需要管理员权限或是当前用户
- **响应示例**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": "integer",
      "username": "string",
      "email": "string",
      "userType": "integer",
      "status": "integer",
      "createTime": "string",
      "updateTime": "string"
    }
  }
  ```

### 2.2 更新用户状态
- **接口**：`PUT /v1/users/{id}/status`
- **描述**：更新用户状态
- **权限**：需要管理员权限
- **请求参数**：
  - status：状态值（query参数）
- **响应示例**：
  ```json
  {
    "code": 200,
    "message": "更新成功",
    "data": null
  }
  ```

## 3. 网站管理模块

### 3.1 创建网站
- **接口**：`POST /v1/websites`
- **描述**：创建新网站
- **权限**：需要WEBSITE_CREATE权限
- **请求参数**：
  ```json
  {
    "name": "string",
    "url": "string",
    "description": "string"
  }
  ```
- **响应示例**：
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "id": "integer",
      "name": "string",
      "url": "string",
      "description": "string",
      "status": 0,
      "createTime": "string"
    }
  }
  ```

### 3.2 获取网站列表
- **接口**：`GET /v1/websites`
- **描述**：分页获取网站列表
- **权限**：需要WEBSITE_READ权限
- **请求参数**：
  - userId：用户ID（可选）
  - status：状态（可选）
  - page：页码（默认1）
  - size：每页数量（默认10）
- **响应示例**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "currentPage": 1,
      "pageSize": 10,
      "totalPages": 5,
      "totalItems": 50,
      "items": [
        {
          "id": "integer",
          "name": "string",
          "url": "string",
          "description": "string",
          "status": "integer",
          "createTime": "string",
          "updateTime": "string"
        }
      ]
    }
  }
  ```

## 4. 广告素材模块

### 4.1 上传素材
- **接口**：`POST /v1/materials/upload`
- **描述**：上传广告素材
- **权限**：需要ADVERTISER角色
- **请求参数**：
  - file：文件（multipart/form-data）
  - type：素材类型
- **响应示例**：
  ```json
  {
    "code": 200,
    "message": "上传成功",
    "data": {
      "id": "integer",
      "url": "string",
      "type": "integer",
      "size": "integer",
      "createTime": "string"
    }
  }
  ```

### 4.2 获取素材列表
- **接口**：`GET /v1/materials/ad/{adId}`
- **描述**：获取指定广告的素材列表
- **响应示例**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "id": "integer",
        "url": "string",
        "type": "integer",
        "size": "integer",
        "createTime": "string"
      }
    ]
  }
  ```

## 5. 广告投放模块

### 5.1 创建投放任务
- **接口**：`POST /api/delivery/tasks`
- **描述**：创建新的广告投放任务
- **请求参数**：
  ```json
  {
    "adId": "integer",
    "adSpaceId": "integer",
    "scheduleTime": "string"
  }
  ```
- **响应示例**：
  ```json
  {
    "code": 200,
    "message": "创建成功",
    "data": {
      "taskId": "integer"
    }
  }
  ```

### 5.2 获取投放任务列表
- **接口**：`GET /api/delivery/tasks`
- **描述**：分页获取投放任务列表
- **请求参数**：
  - current：当前页（默认1）
  - size：每页大小（默认10）
  - adId：广告ID（可选）
- **响应示例**：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "currentPage": 1,
      "pageSize": 10,
      "totalPages": 5,
      "totalItems": 50,
      "items": [
        {
          "id": "integer",
          "adId": "integer",
          "adSpaceId": "integer",
          "scheduleTime": "string",
          "status": "integer",
          "createTime": "string",
          "updateTime": "string"
        }
      ]
    }
  }
  ``` 