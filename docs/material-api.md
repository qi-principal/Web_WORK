# 素材管理 API 文档

## 基础信息

### 服务器配置
- 基础路径: `http://localhost:8181/api`
- 服务端口: 8181
- Context Path: `/api`

### 通用说明
- 所有请求需要在 Header 中携带 token
- 需要 `ADVERTISER` 角色权限
- 返回格式统一使用 JSON

## API 接口详情

### 1. 获取指定用户的所有素材列表

#### 请求信息
- 请求方法: `GET`
- 请求路径: `/v1/materials/user/{userId}`
- 完整路径示例: `http://localhost:8181/api/v1/materials/user/123`

#### 请求参数
| 参数名 | 位置 | 类型 | 必填 | 说明 |
|--------|------|------|------|------|
| userId | path | Long | 是 | 用户ID |
| Authorization | header | String | 是 | Bearer token |

#### 响应结果
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "type": 1,
            "typeName": "图片",
            "content": "示例图片",
            "url": "http://example.com/image.jpg",
            "size": 1024,
            "createTime": "2023-12-20T10:00:00"
        }
        // ... 更多素材
    ]
}
```

### 2. 分页获取指定用户的素材列表

#### 请求信息
- 请求方法: `GET`
- 请求路径: `/v1/materials/user/{userId}/page`
- 完整路径示例: `http://localhost:8181/api/v1/materials/user/123/page?current=1&size=10`

#### 请求参数
| 参数名 | 位置 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|------|--------|
| userId | path | Long | 是 | 用户ID | - |
| current | query | Long | 否 | 当前页码 | 1 |
| size | query | Long | 否 | 每页数量 | 10 |
| Authorization | header | String | 是 | Bearer token | - |

#### 响应结果
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "records": [
            {
                "id": 1,
                "type": 1,
                "typeName": "图片",
                "content": "示例图片",
                "url": "http://example.com/image.jpg",
                "size": 1024,
                "createTime": "2023-12-20T10:00:00"
            }
        ],
        "total": 100,    // 总记录数
        "size": 10,      // 每页大小
        "current": 1,    // 当前页码
        "pages": 10      // 总页数
    }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 403 | 权限不足 |
| 404 | 资源不存在 |

## 错误响应示例

### 参数错误
```json
{
    "code": 400,
    "message": "用户ID不能为空",
    "data": null
}
```

### 权限不足
```json
{
    "code": 403,
    "message": "权限不足",
    "data": null
}
```

## 调用示例

### cURL
```bash
# 获取所有素材
curl -X GET 'http://localhost:8181/api/v1/materials/user/123' \
-H 'Authorization: Bearer your-token-here'

# 分页获取素材
curl -X GET 'http://localhost:8181/api/v1/materials/user/123/page?current=1&size=10' \
-H 'Authorization: Bearer your-token-here'
```

### JavaScript
```javascript
// 获取所有素材
async function getAllMaterials(userId) {
    const response = await fetch(`http://localhost:8181/api/v1/materials/user/${userId}`, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer your-token-here'
        }
    });
    return await response.json();
}

// 分页获取素材
async function getMaterialsByPage(userId, current = 1, size = 10) {
    const response = await fetch(
        `http://localhost:8181/api/v1/materials/user/${userId}/page?current=${current}&size=${size}`, 
        {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer your-token-here'
            }
        }
    );
    return await response.json();
}
```

## 注意事项
1. 所有请求必须携带有效的 Authorization Token
2. 用户只能访问自己的数据
3. 分页接口的 size 建议不要设置过大，避免数据量过大影响性能
4. 返回的时间格式为 ISO-8601 标准格式

## 更新历史
| 版本 | 日期 | 描述 |
|------|------|------|
| 1.0 | 2023-12-22 | 初始版本 | 