# 广告平台前端开发指南

## 1. 开发环境配置

### 1.1 基础配置
- 后端服务地址：`http://localhost:8181`
- API基础路径：`/api`
- 文件上传限制：
  - 单个文件最大：10MB
  - 请求总大小限制：10MB
- 数据库：MySQL 5.7+
- Redis：3.0+
- JDK版本：11
- Maven：3.6+

### 1.2 开发工具
- Postman（已提供配置文件）
  - `postman_collection.json`：接口集合
  - `postman_environment.json`：环境配置
- IDE推荐：VSCode、WebStorm
- API文档：`http://localhost:8181/api/swagger-ui.html`

## 2. 接口规范

### 2.1 统一响应格式
```json
{
    "code": 200,          // 状态码
    "message": "成功",     // 提示信息
    "data": {             // 数据主体
        // 具体数据
    }
}
```

### 2.2 认证机制
- 采用JWT Token认证
- Token获取：通过登录接口
- Token使用：
  ```
  Header: {
    "Authorization": "Bearer <token>"
  }
  ```

### 2.3 错误码对照表
| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 2.4 用户角色权限
| 角色ID | 角色名称 | 说明 |
|--------|----------|------|
| 1 | 广告主 | 管理广告投放 |
| 2 | 网站主 | 管理广告位 |
| 3 | 管理员 | 系统管理 |

## 3. 前端开发示例

### 3.1 API调用示例
```javascript
// API 基础配置
const BASE_URL = '/api/v1';
const STATIC_BASE = '/api';

// 登录示例
const login = async (username, password) => {
    try {
        const response = await fetch(`${BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        const result = await response.json();
        if (result.code === 200) {
            // 保存token
            localStorage.setItem('token', result.data.token);
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error(error);
        throw error;
    }
};

// 带认证的请求示例
const getProfile = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${BASE_URL}/users/profile`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        const result = await response.json();
        if (result.code === 200) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error(error);
        throw error;
    }
};
```

### 3.2 文件上传示例
```javascript
const uploadMaterial = async (file) => {
    try {
        const token = localStorage.getItem('token');
        const formData = new FormData();
        formData.append('file', file);
        
        const response = await fetch(`${BASE_URL}/materials/upload`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });
        const result = await response.json();
        if (result.code === 200) {
            return result.data;
        } else {
            throw new Error(result.message);
        }
    } catch (error) {
        console.error(error);
        throw error;
    }
};
```

## 4. 安全性要求

### 4.1 XSS防护
- 所有用户输入必须进行HTML转义
- 使用安全的HTML解析库
- 避免使用innerHTML，优先使用textContent

### 4.2 CSRF防护
- 所有POST请求都需要带上token
- 使用正确的Content-Type
- 避免使用JSONP

### 4.3 数据安全
- 敏感信息不要存储在localStorage
- 使用HTTPS进行数据传输
- 及时清理敏感数据
- JWT Token有效期：24小时

### 4.4 文件上传安全
- 使用OSS进行文件存储
- OSS域名：`https://web-andrew.oss-cn-shanghai.aliyuncs.com`
- 上传前进行文件类型验证
- 上传前进行文件大小验证

## 5. 文件上传规范

### 5.1 支持的文件类型
- 图片：jpg, jpeg, png, gif
- 视频：mp4, avi
- 文档：pdf, doc, docx

### 5.2 文件限制
- 图片：最大5MB
- 视频：最大50MB
- 文档：最大10MB

### 5.3 上传要求
- 使用multipart/form-data格式
- 文件名不能包含特殊字符
- 必须验证文件类型和大小

## 6. 开发注意事项

### 6.1 代码规范
- 使用ESLint进行代码检查
- 遵循项目的命名规范
- 编写必要的注释

### 6.2 性能优化
- 合理使用缓存
- 压缩静态资源
- 延迟加载非关键资源

### 6.3 兼容性要求
- 支持主流浏览器最新版本
- 响应式设计适配移动端
- 优雅降级处理

## 7. 常见问题处理

### 7.1 Token过期
```javascript
// 响应拦截器示例
const handleResponse = async (response) => {
    if (response.status === 401) {
        // 清除token
        localStorage.removeItem('token');
        // 跳转到登录页
        window.location.href = '/login';
    }
    return response;
};
```

### 7.2 请求错误处理
```javascript
const handleError = (error) => {
    if (error.response) {
        switch (error.response.status) {
            case 400:
                console.error('参数错误');
                break;
            case 403:
                console.error('权限不足');
                break;
            case 404:
                console.error('资源不存在');
                break;
            case 500:
                console.error('服务器错误');
                break;
            default:
                console.error('未知错误');
        }
    } else {
        console.error('网络错误');
    }
};
```

## 8. 部署说明

### 8.1 环境要求
- Node.js 12+
- NPM 6+
- 现代浏览器（Chrome、Firefox、Safari、Edge）

### 8.2 环境配置
- 开发环境：`.env.development`
  ```
  VUE_APP_API_URL=http://localhost:8181/api
  VUE_APP_OSS_DOMAIN=https://web-andrew.oss-cn-shanghai.aliyuncs.com
  ```
- 生产环境：`.env.production`
  ```
  VUE_APP_API_URL=/api
  VUE_APP_OSS_DOMAIN=https://web-andrew.oss-cn-shanghai.aliyuncs.com
  ```
- 测试环境：`.env.test`

### 8.3 构建命令
```bash
# 安装依赖
npm install

# 开发环境
npm run dev

# 生产环境构建
npm run build
```

### 8.4 部署检查清单
- [ ] 检查API地址配置
- [ ] 删除测试代码和console
- [ ] 确认静态资源路径
- [ ] 验证所有功能是否正常
- [ ] 检查文件上传功能
- [ ] 验证OSS配置
- [ ] 确认Redis连接
- [ ] 测试WebSocket连接（如果使用）